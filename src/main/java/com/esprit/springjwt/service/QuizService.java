package com.esprit.springjwt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esprit.springjwt.entity.Answer;
import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.entity.Quiz;
import com.esprit.springjwt.repository.AnswerRepository;
import com.esprit.springjwt.repository.FormationRepository;
import com.esprit.springjwt.repository.QuizRepository;

@Service
public class QuizService {

	@Autowired
	QuizRepository quizRepository;
	
	@Autowired
	AnswerRepository answerRepository;
	
	@Autowired
	FormationRepository formationRepository;
	
	public List<Quiz> getAllQuiz(Long id){
		List<Quiz> quizs;
		if(id==0) {
			quizs = quizRepository.findAll();
		}else {
			quizs = quizRepository.filterQuizByFormation(id);
		} 
		return quizs;
	}
	
	public void addQuiz(String name, Long id) throws Exception {
		Formation formation = formationRepository.getById(id);
		Quiz newQuiz = new Quiz();
		if(formation !=null) {
			newQuiz.setName(name);
			newQuiz.setFormationId(formation);
			quizRepository.save(newQuiz);
		}else {
			throw new Exception("Error while adding quiz");
		}
	}
	
	public List<Quiz> filterQuizByFormation(Long id){
		List<Quiz> quizs = quizRepository.filterQuizByFormation(id);
		return quizs;
	}
	
	public void deleteQuizById(Long id){
		Quiz quiz = quizRepository.getById(id);
		List<Answer> answers = answerRepository.getQuestionsByQuizId(id);
		if(quiz != null) {
		quizRepository.delete(quiz);
			if(answers.size()>0) {
				for(int i=0;i<answers.size();i++) {
					answerRepository.deleteById(answers.get(i).getId());
				}
			}
		}
	}
}
