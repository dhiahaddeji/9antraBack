package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.XPEvent;
import com.esprit.springjwt.entity.XPEventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface XPEventRepository extends JpaRepository<XPEvent, Long> {
    boolean existsByUserIdAndEventTypeAndReferenceId(Long userId, XPEventType eventType, Long referenceId);

    @Query("SELECT COUNT(e) FROM XPEvent e WHERE e.user.id = ?1 AND e.eventType = 'QUIZ_PASSED'")
    long countPassedQuizzesByUserId(Long userId);

    @Query("SELECT COUNT(e) FROM XPEvent e WHERE e.user.id = ?1 AND e.eventType = 'FEEDBACK_GIVEN'")
    long countFeedbackGivenByUserId(Long userId);
}
