package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.Training;
import com.esprit.springjwt.service.TrainingServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/training")
public class TrainingController {

    @Autowired
    TrainingServices trainingServices;
/*
    @PostMapping("/addTraining/{idCategorie}")
    public Training addTraining(@RequestBody Training training){
       return trainingServices.addTraining( training);
    }

    @DeleteMapping("/deleteTraining/{id}")
    public void deleteTraining(@PathVariable("id") Long id){
        trainingServices.deleteTrainingById(id);
    }

    @PutMapping("/updateTraining")
    public Training updateTraining(@RequestBody Training training){
        return trainingServices.updateTraining(training);
    }

    @GetMapping("/getTrainingById/{id}")
    public Training getTrainigbyid(@PathVariable("id") Long id)
    {
        return trainingServices.getTrainingById(id);
    }
     /*
    @GetMapping("/getAllTraining")
    public List<Training> getAllTraining()
    {
        return trainingServices.getAllTraining();
    }

*/

}
