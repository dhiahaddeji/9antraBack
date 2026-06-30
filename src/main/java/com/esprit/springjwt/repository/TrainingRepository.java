package com.esprit.springjwt.repository;

import com.esprit.springjwt.entity.Training;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainingRepository  extends JpaRepository<Training, Long> {
}
