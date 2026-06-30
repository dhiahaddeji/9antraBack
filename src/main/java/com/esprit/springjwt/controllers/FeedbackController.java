package com.esprit.springjwt.controllers;


import com.esprit.springjwt.entity.Categorie;
import com.esprit.springjwt.entity.Feedback;
import com.esprit.springjwt.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/Feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {
    @Autowired
    FeedbackService FeedbackService;

    //getAll
    @GetMapping("/allFeedback")
    public List<Feedback> getAllEvaluations() {return FeedbackService.getAllFeedback();
    }
    
    //add feedback
    @PostMapping("/addFeedback")
    public Feedback addFeedback(@RequestBody Feedback Feedback){
        return FeedbackService.addFeedback(Feedback);
    }
    
    //update feedback by posted admin
    @PatchMapping("/updateFeedbackPosted/{id}")
    public Feedback updateFeedbackPosted(@PathVariable("id") Long id){
        return FeedbackService.updateFeedbackPosted(id);
    }
    
    //delete feedback admin
    @DeleteMapping("/deleteFeedback/{id}")
    public void deleteFeedback(@PathVariable("id") Long id){
        FeedbackService.deleteFeedback(id);
    }
    
    
    // get feedback by posted true
    @GetMapping("/getFeedbackByPosted")
    public List<Feedback> getFeedbackByPosted(){
        return FeedbackService.getFeedbackPosted();
    }
    
    // get feedback formation id
    @GetMapping("/getFeedbackByFormation/{formation}")
    public List <Feedback> getFeedbackByFormation(@PathVariable("formation") String formation)
    {
        return FeedbackService.getFeedbackByFormation(formation);
    }
    
    
    
    
    @GetMapping("/getFeedbackById/{id}")
    public Feedback getFeedbackById(@PathVariable("id") Long id)
    {
        return FeedbackService.getFeedbackById(id);
    }

    

    @PutMapping("/updateFeedback/{idSession}")
    public Feedback updateFeedback(@RequestBody Feedback Feedback, @PathVariable("idSession") Long idSession){
        return FeedbackService.updateFeedback(Feedback, idSession);
    }
  

}
