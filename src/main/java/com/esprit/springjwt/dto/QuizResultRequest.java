package com.esprit.springjwt.dto;

public class QuizResultRequest {
    private Long userId;
    private Long quizId;
    private int score;
    private int correct;
    private int total;

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public int getCorrect() { return correct; }
    public void setCorrect(int correct) { this.correct = correct; }

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
}
