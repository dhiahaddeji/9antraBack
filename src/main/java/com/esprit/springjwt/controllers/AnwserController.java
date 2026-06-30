package com.esprit.springjwt.controllers;

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

import com.esprit.springjwt.entity.Answer;
import com.esprit.springjwt.entity.Quiz;
import com.esprit.springjwt.repository.AnswerRepository;
import com.esprit.springjwt.service.AnswerService;

@RestController
@RequestMapping("/api/quiz/answer")
@CrossOrigin("*")
public class AnwserController {
	@Autowired
	AnswerRepository anwserRepository;
	
	@Autowired
	AnswerService answerService;
	
	@PostMapping("/addQuiz")
	public ResponseEntity<?> addOption(@RequestBody Answer answer , @RequestParam("id") Long id){
		try{
			answerService.addQA(answer, id);
			return ResponseEntity.ok(Map.of("message", "Option added successfully"));
		}catch(Exception e) {
			return ResponseEntity.ok(Map.of("message", "Error while adding options")); 
		}
	}
	
	@GetMapping("/getCountQuestionsByQuizId")
	public ResponseEntity<?> getCountQuestionsByQuizId(@RequestParam("id") Long id){
		 return ResponseEntity.ok(answerService.getCountQuestionsByQuizId(id));
	}
	
	@GetMapping("/getQuestionsByQuizId")
	public ResponseEntity<?> getQuestionsByQuizId(@RequestParam("id") Long id){
		 return ResponseEntity.ok(answerService.getQuestionsByQuizId(id));
	}
	
	@DeleteMapping("/deleteById")
	public ResponseEntity<?> deleteById(@RequestParam("id") Long id){
		 answerService.deleteById(id);
		 return ResponseEntity.ok("Answer Deleted successfully");
	}

	
}
