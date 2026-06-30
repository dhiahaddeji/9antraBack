package com.esprit.springjwt.controllers;


import com.esprit.springjwt.entity.Progress;
import com.esprit.springjwt.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Progress")
public class ProgressController {
    @Autowired
    ProgressService  ProgressService;

    @GetMapping("/allProgress")
    public List<Progress> getAllProgress() {
        return ProgressService.getAllProgress();

    }
    @PostMapping("/addProgress")
    public Progress addProgress(@RequestBody Progress Progress){
        return ProgressService.addProgress(Progress);
    }
    @PutMapping("/updateProgress")
    public Progress updateProgress(@RequestBody Progress Progress){
        return ProgressService.updateProgress(Progress);
    }

    @GetMapping("/getProgressById/{id}")
    public Progress getProgressById(@PathVariable("id") Long id)
    {
        return ProgressService.getProgressById(id);
    }
    @GetMapping("/deleteProgress/{id}")
    public void deleteProgress(@PathVariable("id") Long id){
        ProgressService.deleteProgress(id);
    }


}
