package com.esprit.springjwt.controllers;

import com.esprit.springjwt.dto.QuizResultRequest;
import com.esprit.springjwt.entity.XPEventType;
import com.esprit.springjwt.service.XPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/xp")
@CrossOrigin(origins = "*")
public class XPController {

    @Autowired
    private XPService xpService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserXP(@PathVariable Long userId) {
        return ResponseEntity.ok(xpService.getOrCreateUserXP(userId));
    }

    @GetMapping("/badges/{userId}")
    public ResponseEntity<?> getUserBadges(@PathVariable Long userId) {
        return ResponseEntity.ok(xpService.getUserBadges(userId));
    }

    @GetMapping("/leaderboard")
    public ResponseEntity<?> getLeaderboard() {
        return ResponseEntity.ok(xpService.getLeaderboard());
    }

    @PostMapping("/quiz-result")
    public ResponseEntity<?> recordQuizResult(@RequestBody QuizResultRequest req) {
        if (req.getTotal() > 0 && req.getCorrect() * 100 / req.getTotal() > 50) {
            xpService.awardXP(req.getUserId(), XPEventType.QUIZ_PASSED, req.getQuizId());
            long passed = xpService.countPassedQuizzes(req.getUserId());
            if (passed == 5) {
                xpService.awardBadge(req.getUserId(), "QUIZ", "Quiz Master", "QUIZ_MASTER_5", "🎯");
            } else if (passed == 10) {
                xpService.awardBadge(req.getUserId(), "QUIZ", "Quiz Legend", "QUIZ_LEGEND_10", "🏆");
            }
        }
        return ResponseEntity.ok(xpService.getOrCreateUserXP(req.getUserId()));
    }

    @PostMapping("/chapter-complete")
    public ResponseEntity<?> chapterComplete(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long chapterId = body.get("chapterId");
        xpService.awardXP(userId, XPEventType.CHAPTER_COMPLETE, chapterId);
        return ResponseEntity.ok(xpService.getOrCreateUserXP(userId));
    }

    @PostMapping("/formation-complete")
    public ResponseEntity<?> formationComplete(@RequestBody Map<String, Object> body) {
        Long userId = toLong(body.get("userId"));
        Long formationId = toLong(body.get("formationId"));
        String category = body.get("category") != null ? body.get("category").toString() : "General";

        xpService.awardXP(userId, XPEventType.FORMATION_COMPLETE, formationId);

        int catCount = xpService.getUserBadges(userId).stream()
            .filter(b -> b.getCategory().equalsIgnoreCase(category))
            .mapToInt(b -> 1).sum() + 1;
        String badgeKey = category.toUpperCase().replace(" ", "_") + "_LEVEL_" + catCount;
        xpService.awardBadge(userId, category, category + " Level " + catCount, badgeKey, "🏅");

        return ResponseEntity.ok(xpService.getOrCreateUserXP(userId));
    }

    @PostMapping("/feedback-given")
    public ResponseEntity<?> feedbackGiven(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long feedbackId = body.get("feedbackId");
        xpService.awardXP(userId, XPEventType.FEEDBACK_GIVEN, feedbackId);
        long count = xpService.countFeedbackGiven(userId);
        if (count == 3) {
            xpService.awardBadge(userId, "COMMUNITY", "Community Voice", "COMMUNITY_VOICE_3", "💬");
        }
        return ResponseEntity.ok(xpService.getOrCreateUserXP(userId));
    }

    @PostMapping("/certificate-earned")
    public ResponseEntity<?> certificateEarned(@RequestBody Map<String, Long> body) {
        Long userId = body.get("userId");
        Long certId = body.get("certificateId");
        xpService.awardXP(userId, XPEventType.CERTIFICATE_EARNED, certId);
        xpService.awardBadge(userId, "CERTIFICATE", "Certified", "CERTIFIED_" + certId, "📜");
        return ResponseEntity.ok(xpService.getOrCreateUserXP(userId));
    }

    private Long toLong(Object val) {
        if (val == null) return 0L;
        return Long.valueOf(val.toString());
    }
}
