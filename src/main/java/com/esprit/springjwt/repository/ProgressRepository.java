package com.esprit.springjwt.repository;


import com.esprit.springjwt.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProgressRepository extends JpaRepository<Progress,Long> {
}
