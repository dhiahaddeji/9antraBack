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
import com.esprit.springjwt.service.GeminiService;

@RestController
@RequestMapping("/api/quiz/answer")
@CrossOrigin("*")
public class AnwserController {
	@Autowired
	AnswerRepository anwserRepository;

	@Autowired
	AnswerService answerService;

	@Autowired
	GeminiService geminiService;
	
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

	@PostMapping("/generateByAI")
	public ResponseEntity<?> generateByAI(@RequestBody java.util.Map<String, Object> body) {
		try {
			Long quizId = Long.valueOf(body.get("quizId").toString());
			String topic = body.getOrDefault("topic", "").toString();
			String courseContent = body.getOrDefault("courseContent", "").toString();
			int count = Integer.parseInt(body.getOrDefault("numberOfQuestions", 5).toString());
			String difficulty = body.getOrDefault("difficulty", "mixed").toString();
			String questionType = body.getOrDefault("questionType", "mixed").toString();

			java.util.List<com.esprit.springjwt.entity.Answer> generated =
				geminiService.generateQuestions(quizId, topic, courseContent, count, difficulty, questionType);

			return ResponseEntity.ok(java.util.Map.of(
				"count", generated.size(),
				"questions", generated
			));
		} catch (IllegalStateException e) {
			return ResponseEntity.status(503).body(java.util.Map.of("message", e.getMessage()));
		} catch (Exception e) {
			System.err.println("[Gemini] Error: " + e.getMessage());
			return ResponseEntity.status(500).body(java.util.Map.of("message", "AI generation failed: " + e.getMessage()));
		}
	}
}
