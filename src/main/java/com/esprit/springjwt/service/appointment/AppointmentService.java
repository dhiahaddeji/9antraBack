package com.esprit.springjwt.service.appointment;


import com.esprit.springjwt.entity.appointment.Appointment;
import com.esprit.springjwt.repository.appointment.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AppointmentService {

    @Resource
    private AppointmentRepository appointmentRepository;

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    public Appointment createAppointment(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(Long id, Appointment appointmentDetails) {
        Appointment appointment = appointmentRepository.findById(id).orElse(null);
        if (appointment != null) {
            appointment.setSubject(appointmentDetails.getSubject());
            appointment.setStartTime(appointmentDetails.getStartTime());
            appointment.setEndTime(appointmentDetails.getEndTime());
            return appointmentRepository.save(appointment);
        }
        return null;
    }

    public void deleteAppointment(Long id) {
        appointmentRepository.deleteById(id);
    }
}
