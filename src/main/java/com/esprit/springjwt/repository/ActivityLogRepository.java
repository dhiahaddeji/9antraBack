package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.ActivityLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    Page<ActivityLog> findAllByOrderByTimestampDesc(Pageable pageable);

    Page<ActivityLog> findByActionOrderByTimestampDesc(String action, Pageable pageable);

    Page<ActivityLog> findByUsernameContainingIgnoreCaseOrderByTimestampDesc(String username, Pageable pageable);

    @Query("SELECT l FROM ActivityLog l WHERE " +
           "(:action IS NULL OR l.action = :action) AND " +
           "(:username IS NULL OR LOWER(l.username) LIKE LOWER(CONCAT('%',:username,'%'))) AND " +
           "(:from IS NULL OR l.timestamp >= :from) AND " +
           "(:to IS NULL OR l.timestamp <= :to) " +
           "ORDER BY l.timestamp DESC")
    Page<ActivityLog> filter(@Param("action")   String action,
                             @Param("username") String username,
                             @Param("from")     Date from,
                             @Param("to")       Date to,
                             Pageable pageable);

    @Query("SELECT l.action, COUNT(l) FROM ActivityLog l GROUP BY l.action")
    List<Object[]> countByAction();
}
