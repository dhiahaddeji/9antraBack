package com.esprit.springjwt.controllers.appointment;

import com.esprit.springjwt.entity.appointment.Break;
import com.esprit.springjwt.service.appointment.BreakService;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/breaks")
public class BreakController {
    @Resource
    private BreakService breakService;

    @GetMapping
    public List<Break> getAllBreaks() {
        return breakService.getAllBreaks();
    }

    @PostMapping
    public Break addBreak(@RequestBody Break breakz) {
        return breakService.addBreak(breakz);
    }

    @DeleteMapping("/{id}")
    public void deleteBreak(@PathVariable Long id) {
        breakService.deleteBreak(id);
    }
}
