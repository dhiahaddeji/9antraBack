package com.esprit.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GenerateQuizRequest {
    private Long quizId;
    private String topic;
    private String courseContent;
    private int numberOfQuestions; // Default 5
    private String difficulty; // "easy", "medium", "hard", or "mixed"
    private String questionType; // "multiple", "trueFalse", or "mixed"
}
