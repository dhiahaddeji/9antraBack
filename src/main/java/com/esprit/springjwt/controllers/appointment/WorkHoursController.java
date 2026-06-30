package com.esprit.springjwt.controllers.appointment;

import com.esprit.springjwt.entity.appointment.WorkHours;
import com.esprit.springjwt.service.appointment.WorkHoursService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/work-hours")
public class WorkHoursController {
    @Resource
    private WorkHoursService workHoursService;

    @GetMapping("/{dayOfWeek}")
    public List<WorkHours> getWorkHours(@PathVariable String dayOfWeek) {
        return workHoursService.getWorkHoursByDay(dayOfWeek);
    }

    @PostMapping
    public WorkHours saveWorkHours(@RequestBody WorkHours workHours) {
        return workHoursService.saveWorkHours(workHours);
    }
}