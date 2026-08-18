package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Answer;
import com.esprit.springjwt.entity.Quiz;
import com.esprit.springjwt.repository.AnswerRepository;
import com.esprit.springjwt.repository.QuizRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class GeminiService {

    @Value("${gemini.api.key:}")
    private String geminiApiKey;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private AnswerRepository answerRepository;

    private final ObjectMapper mapper = new ObjectMapper();

    public List<Answer> generateQuestions(Long quizId, String topic, String courseContent,
                                          int count, String difficulty, String questionType) throws Exception {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            throw new IllegalStateException("GEMINI_API_KEY is not configured");
        }

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found: " + quizId));

        String prompt = buildPrompt(topic, courseContent, count, difficulty, questionType);
        String rawJson = callGemini(prompt);
        List<Answer> answers = parseAnswers(rawJson, quiz);
        answerRepository.saveAll(answers);
        System.out.println("[Gemini] Generated and saved " + answers.size() + " questions for quiz " + quizId);
        return answers;
    }

    private String buildPrompt(String topic, String courseContent, int count, String difficulty, String questionType) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an expert quiz creator for an e-learning platform.\n\n");
        sb.append("Generate exactly ").append(count).append(" quiz questions about \"").append(topic).append("\".\n");
        sb.append("Difficulty: ").append(difficulty).append("\n");

        if (questionType.equals("trueFalse")) {
            sb.append("Question type: True/False only\n");
        } else if (questionType.equals("multiple")) {
            sb.append("Question type: Multiple choice with 1 correct answer and 2 wrong answers\n");
        } else {
            sb.append("Question type: Mix of multiple choice and true/false\n");
        }

        if (courseContent != null && !courseContent.isBlank()) {
            sb.append("\nBase your questions on this course material:\n").append(courseContent).append("\n");
        }

        sb.append("\nReturn ONLY a valid JSON array. No explanation, no markdown. Each object must have:\n");
        sb.append("- \"question\": the question text (no trailing '?')\n");
        sb.append("- \"correct_answer\": the correct answer\n");
        sb.append("- \"wrong_answer1\": first wrong answer\n");
        sb.append("- \"wrong_answer2\": second wrong answer (for true/false, same as wrong_answer1)\n");
        sb.append("- \"type\": \"multiple\" or \"trueFalse\"\n\n");
        sb.append("Example: [{\"question\":\"What is React\",\"correct_answer\":\"A UI library\",\"wrong_answer1\":\"A database\",\"wrong_answer2\":\"A server\",\"type\":\"multiple\"}]");
        return sb.toString();
    }

    private String callGemini(String prompt) throws Exception {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + geminiApiKey;

        String body = mapper.writeValueAsString(java.util.Map.of(
            "contents", java.util.List.of(java.util.Map.of(
                "parts", java.util.List.of(java.util.Map.of("text", prompt))
            )),
            "generationConfig", java.util.Map.of(
                "temperature", 0.7,
                "maxOutputTokens", 4096
            )
        ));

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .timeout(Duration.ofSeconds(60))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Gemini API error " + response.statusCode() + ": " + response.body());
        }

        JsonNode root = mapper.readTree(response.body());
        String text = root.path("candidates").get(0)
                .path("content").path("parts").get(0)
                .path("text").asText();

        // Strip markdown code fences if present
        text = text.trim();
        if (text.startsWith("```")) {
            text = text.replaceAll("```json\\n?", "").replaceAll("```\\n?", "").trim();
        }
        return text;
    }

    private List<Answer> parseAnswers(String json, Quiz quiz) throws Exception {
        JsonNode arr = mapper.readTree(json);
        List<Answer> result = new ArrayList<>();
        for (JsonNode node : arr) {
            Answer a = new Answer();
            a.setQuestion(node.path("question").asText());
            a.setCorrect_answer(node.path("correct_answer").asText());
            a.setWrong_answer1(node.path("wrong_answer1").asText());
            a.setWrong_answer2(node.path("wrong_answer2").asText());
            a.setType(node.path("type").asText("multiple"));
            a.setQuizId(quiz);
            result.add(a);
        }
        return result;
    }
}
