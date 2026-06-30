package com.esprit.springjwt.repository.appointment;

import com.esprit.springjwt.entity.appointment.WorkHours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkHoursRepository extends JpaRepository<WorkHours, Long> {
    List<WorkHours> findByDayOfWeek(String dayOfWeek);
}