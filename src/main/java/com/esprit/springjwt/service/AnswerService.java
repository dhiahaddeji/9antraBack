package com.esprit.springjwt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;import com.esprit.springjwt.controllers.AnwserController;
import com.esprit.springjwt.entity.Answer;
import com.esprit.springjwt.entity.Quiz;
import com.esprit.springjwt.repository.AnswerRepository;
import com.esprit.springjwt.repository.QuizRepository;

@Service
public class AnswerService {

	@Autowired
	AnswerRepository answerRepository;
	
	@Autowired
	QuizRepository quizRepository;
	
	
	public void addQA(Answer answer , Long id) throws Exception{
		Answer newAnswer = new Answer();
		Quiz quiz = quizRepository.getById(id);
		if(quiz != null) {
			newAnswer.setQuestion(answer.getQuestion());
			newAnswer.setCorrect_answer(answer.getCorrect_answer());
			newAnswer.setWrong_answer1(answer.getWrong_answer1());
			newAnswer.setWrong_answer2(answer.getWrong_answer2());
			newAnswer.setQuizId(quiz);
			answerRepository.save(newAnswer);
		}else {
			throw new Exception("Error while adding answers");
		}
	}
	
	public int getCountQuestionsByQuizId(Long id) {
		return answerRepository.getCountQuestionsByQuizId(id);
	}
	
	public List<Answer> getQuestionsByQuizId(Long id) {
		List<Answer> answers = answerRepository.getQuestionsByQuizId(id);
		return answers;
	}
	
	public void deleteById(Long id) {
		Answer answer = answerRepository.getById(id);
		answerRepository.delete(answer);
	}
}
