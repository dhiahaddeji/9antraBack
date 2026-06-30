package com.esprit.springjwt.service.appointment;

import com.esprit.springjwt.entity.appointment.WorkHours;
import com.esprit.springjwt.repository.appointment.WorkHoursRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class WorkHoursService {
    @Resource
    private WorkHoursRepository workHoursRepository;

    public List<WorkHours> getWorkHoursByDay(String dayOfWeek) {
        return workHoursRepository.findByDayOfWeek(dayOfWeek);
    }

    public WorkHours saveWorkHours(WorkHours workHours) {
        // Find existing work hours by dayOfWeek
        List<WorkHours> existingWorkHoursList = workHoursRepository.findByDayOfWeek(workHours.getDayOfWeek());

        if (!existingWorkHoursList.isEmpty()) {
            // Update the first existing work hours (since dayOfWeek is unique, there should be only one)
            WorkHours existingWorkHours = existingWorkHoursList.get(0);
            existingWorkHours.setStart(workHours.getStart());
            existingWorkHours.setEnd(workHours.getEnd());
            return workHoursRepository.save(existingWorkHours);
        } else {
            // Save new work hours
            return workHoursRepository.save(workHours);
        }
    }

}