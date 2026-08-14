package com.esprit.springjwt.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIGeneratedQuestion {
    private String question;
    private String correct_answer;
    private String wrong_answer1;
    private String wrong_answer2;
    private String type; // "multiple" or "trueFalse"
    private String difficulty; // "easy", "medium", "hard"
}
