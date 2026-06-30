package com.esprit.springjwt.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esprit.springjwt.entity.Quiz;
import com.esprit.springjwt.service.QuizService;

@RestController
@RequestMapping("/api/quiz")
@CrossOrigin("*")
public class Quizcontroller {
	
	@Autowired
	QuizService quizService;

	
	@GetMapping("/getAllQuizs")
	public ResponseEntity<?> getAllQuizs(@RequestParam(name="id",defaultValue ="0") Long id){
		List<Quiz> quizs = quizService.getAllQuiz(id);
		return ResponseEntity.ok(quizs);
	}
	
	@PostMapping("/addQuiz")
	public ResponseEntity<?> addQuiz(@RequestParam("name") String name , @RequestParam("id") Long id){
		try{
			quizService.addQuiz(name, id);
			return ResponseEntity.ok(Map.of("message", "Quiz added successfully"));
		}catch(Exception e) {
			return ResponseEntity.ok(Map.of("message", "Error while adding quiz"));
		}
	}
	
	@GetMapping("/filterQuizByFormation")
	public ResponseEntity<?> filterQuizByFormation(@RequestParam("id") Long id){
			List<Quiz> quizs = quizService.filterQuizByFormation(id);
			return ResponseEntity.ok(quizs);
	}
	
	@DeleteMapping("/deleteQuizById")
	public ResponseEntity<?> deleteQuizById(@RequestParam("id") Long id){
		quizService.deleteQuizById(id);
		return ResponseEntity.ok(Map.of("message", "Quiz deleted successfully"));
	}
	
	
	
}
