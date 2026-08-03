package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.UserXP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserXPRepository extends JpaRepository<UserXP, Long> {
    Optional<UserXP> findByUserId(Long userId);
    List<UserXP> findTop20ByOrderByTotalXpDesc();
}
