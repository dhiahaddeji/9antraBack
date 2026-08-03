package com.esprit.springjwt.service;

import com.esprit.springjwt.dto.LeaderboardEntryDTO;
import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class XPService {

    private static final int XP_CHAPTER    = 15;
    private static final int XP_QUIZ       = 30;
    private static final int XP_FORMATION  = 100;
    private static final int XP_FEEDBACK   = 10;
    private static final int XP_CERTIFICATE= 50;

    @Autowired private UserXPRepository userXPRepo;
    @Autowired private XPEventRepository xpEventRepo;
    @Autowired private UserBadgeRepository userBadgeRepo;
    @Autowired private UserRepository userRepo;

    public UserXP awardXP(Long userId, XPEventType eventType, Long referenceId) {
        // Idempotent: don't award twice for same action
        if (referenceId != null && xpEventRepo.existsByUserIdAndEventTypeAndReferenceId(userId, eventType, referenceId)) {
            return getOrCreateUserXP(userId);
        }

        User user = userRepo.findById(userId).orElseThrow();
        int xpToAdd = xpForEvent(eventType);

        XPEvent event = new XPEvent();
        event.setUser(user);
        event.setEventType(eventType);
        event.setReferenceId(referenceId);
        event.setXpEarned(xpToAdd);
        event.setTimestamp(LocalDateTime.now());
        xpEventRepo.save(event);

        UserXP userXP = getOrCreateUserXP(userId);
        userXP.setUser(user);
        userXP.setTotalXp(userXP.getTotalXp() + xpToAdd);
        userXP.setRank(XPRank.fromXp(userXP.getTotalXp()));
        userXP.setLastUpdated(LocalDateTime.now());

        return userXPRepo.save(userXP);
    }

    public UserXP getOrCreateUserXP(Long userId) {
        return userXPRepo.findByUserId(userId).orElseGet(() -> {
            UserXP xp = new UserXP();
            xp.setUser(userRepo.findById(userId).orElseThrow());
            return userXPRepo.save(xp);
        });
    }

    public List<LeaderboardEntryDTO> getLeaderboard() {
        return userXPRepo.findTop20ByOrderByTotalXpDesc().stream()
            .map(ux -> new LeaderboardEntryDTO(
                ux.getUser().getId(),
                trim(ux.getUser().getFirstName()) + " " + trim(ux.getUser().getLastName()),
                ux.getUser().getImage(),
                ux.getTotalXp(),
                ux.getRank().name()
            ))
            .collect(Collectors.toList());
    }

    public UserBadge awardBadge(Long userId, String category, String badgeName, String badgeKey, String icon) {
        if (userBadgeRepo.existsByUserIdAndBadgeKey(userId, badgeKey)) return null;

        User user = userRepo.findById(userId).orElseThrow();
        int level = userBadgeRepo.countByUserIdAndCategory(userId, category) + 1;

        UserBadge badge = new UserBadge();
        badge.setUser(user);
        badge.setBadgeKey(badgeKey);
        badge.setBadgeName(badgeName);
        badge.setCategory(category);
        badge.setLevel(level);
        badge.setIcon(icon);
        badge.setEarnedAt(LocalDateTime.now());

        return userBadgeRepo.save(badge);
    }

    public List<UserBadge> getUserBadges(Long userId) {
        return userBadgeRepo.findByUserId(userId);
    }

    public long countPassedQuizzes(Long userId) {
        return xpEventRepo.countPassedQuizzesByUserId(userId);
    }

    public long countFeedbackGiven(Long userId) {
        return xpEventRepo.countFeedbackGivenByUserId(userId);
    }

    private int xpForEvent(XPEventType type) {
        switch (type) {
            case CHAPTER_COMPLETE:   return XP_CHAPTER;
            case QUIZ_PASSED:        return XP_QUIZ;
            case FORMATION_COMPLETE: return XP_FORMATION;
            case FEEDBACK_GIVEN:     return XP_FEEDBACK;
            case CERTIFICATE_EARNED: return XP_CERTIFICATE;
            default:                 return 0;
        }
    }

    private String trim(String s) { return s != null ? s.trim() : ""; }
}
