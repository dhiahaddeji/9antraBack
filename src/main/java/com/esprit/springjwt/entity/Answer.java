package com.esprit.springjwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.ColumnDefault;


@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answer {
	 @Id
	    @GeneratedValue (strategy = GenerationType.IDENTITY)
	    private Long id ;
	    private String question;
	    private String correct_answer;
	    private String wrong_answer1;
	    private String wrong_answer2;
	    
	    
	    @ManyToOne(fetch = FetchType.EAGER)
	    @JoinColumn(name="quiz_id")
	    Quiz quizId;
	

}
