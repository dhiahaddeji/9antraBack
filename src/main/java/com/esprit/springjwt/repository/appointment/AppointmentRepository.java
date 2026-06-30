package com.esprit.springjwt.repository.appointment;


import com.esprit.springjwt.entity.appointment.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
