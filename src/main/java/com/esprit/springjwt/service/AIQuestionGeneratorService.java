package com.esprit.springjwt.service;

import com.esprit.springjwt.dto.AIGeneratedQuestion;
import com.esprit.springjwt.dto.GenerateQuizRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AIQuestionGeneratorService {

    @Value("${openai.api.key:}")
    private String apiKey;

    private final OkHttpClient httpClient = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public List<AIGeneratedQuestion> generateQuestions(GenerateQuizRequest request) throws Exception {
        if (apiKey == null || apiKey.isEmpty()) {
            throw new Exception("OpenAI API key not configured");
        }

        String prompt = buildPrompt(request);
        String response = callOpenAIAPI(prompt);
        return parseQuestionsFromResponse(response, request.getQuestionType());
    }

    private String buildPrompt(GenerateQuizRequest request) {
        int numQuestions = Math.max(1, Math.min(request.getNumberOfQuestions(), 20)); // Limit to 20 questions
        String difficulty = request.getDifficulty() != null ? request.getDifficulty() : "mixed";
        String questionType = request.getQuestionType() != null ? request.getQuestionType() : "multiple";

        String typeInstruction = "";
        if ("trueFalse".equals(questionType)) {
            typeInstruction = "Generate ONLY True/False questions. For each question, provide the question and ONE correct answer and ONE wrong answer.";
        } else if ("multiple".equals(questionType)) {
            typeInstruction = "Generate ONLY Multiple Choice questions (MCQ) with 4 options each (1 correct, 3 wrong). For each question, provide the question and answers.";
        } else {
            typeInstruction = "Generate a mix of Multiple Choice (3 options: 1 correct, 2 wrong) and True/False questions.";
        }

        String difficultyInstruction = "";
        if ("easy".equals(difficulty)) {
            difficultyInstruction = "Make the questions EASY, suitable for beginners.";
        } else if ("hard".equals(difficulty)) {
            difficultyInstruction = "Make the questions HARD, requiring deep understanding.";
        } else if ("medium".equals(difficulty)) {
            difficultyInstruction = "Make the questions MEDIUM difficulty.";
        } else {
            difficultyInstruction = "Mix easy, medium, and hard questions.";
        }

        String courseInfo = request.getCourseContent() != null && !request.getCourseContent().isEmpty()
                ? "Course Content: " + request.getCourseContent()
                : "";

        return String.format(
                "You are an expert educational quiz creator. Generate %d quiz questions about the topic: '%s'. %s %s %s\n\n" +
                "%s\n\n" +
                "IMPORTANT: Return the response in EXACTLY this JSON format for each question (no markdown, no code blocks, just raw JSON):\n" +
                "{\n" +
                "  \"questions\": [\n" +
                "    {\n" +
                "      \"question\": \"The question text\",\n" +
                "      \"correct_answer\": \"The correct answer\",\n" +
                "      \"wrong_answer1\": \"Wrong option 1\",\n" +
                "      \"wrong_answer2\": \"Wrong option 2\",\n" +
                "      \"type\": \"multiple\",\n" +
                "      \"difficulty\": \"easy\"\n" +
                "    },\n" +
                "    ...\n" +
                "  ]\n" +
                "}\n\n" +
                "Make sure questions are clear, educational, and well-structured.",
                numQuestions,
                request.getTopic(),
                typeInstruction,
                difficultyInstruction,
                courseInfo
        );
    }

    private String callOpenAIAPI(String prompt) throws IOException {
        String jsonBody = new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(
                java.util.Map.of(
                        "model", "gpt-3.5-turbo",
                        "messages", new Object[]{
                                java.util.Map.of(
                                        "role", "user",
                                        "content", prompt
                                )
                        },
                        "temperature", 0.7,
                        "max_tokens", 4000
                )
        );

        RequestBody body = RequestBody.create(jsonBody, MediaType.parse("application/json; charset=utf-8"));

        Request httpRequest = new Request.Builder()
                .url(OPENAI_API_URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

        try (Response httpResponse = httpClient.newCall(httpRequest).execute()) {
            if (!httpResponse.isSuccessful()) {
                String errorBody = httpResponse.body() != null ? httpResponse.body().string() : "Unknown error";
                log.error("OpenAI API error: {} - {}", httpResponse.code(), errorBody);
                throw new IOException("Failed to call OpenAI API: " + httpResponse.code());
            }

            String responseBody = httpResponse.body().string();
            JsonNode jsonResponse = objectMapper.readTree(responseBody);

            if (jsonResponse.has("choices") && jsonResponse.get("choices").isArray() && jsonResponse.get("choices").size() > 0) {
                JsonNode message = jsonResponse.get("choices").get(0).get("message");
                if (message != null && message.has("content")) {
                    return message.get("content").asText();
                }
            }

            throw new IOException("Unexpected response format from OpenAI API");
        }
    }

    private List<AIGeneratedQuestion> parseQuestionsFromResponse(String response, String preferredType) {
        List<AIGeneratedQuestion> questions = new ArrayList<>();

        try {
            // Extract JSON from response
            String jsonContent = extractJSON(response);
            JsonNode root = objectMapper.readTree(jsonContent);

            if (root.has("questions") && root.get("questions").isArray()) {
                for (JsonNode questionNode : root.get("questions")) {
                    AIGeneratedQuestion question = new AIGeneratedQuestion();

                    question.setQuestion(getStringValue(questionNode, "question"));
                    question.setCorrect_answer(getStringValue(questionNode, "correct_answer"));
                    question.setWrong_answer1(getStringValue(questionNode, "wrong_answer1"));
                    question.setWrong_answer2(getStringValue(questionNode, "wrong_answer2"));

                    String type = getStringValue(questionNode, "type");
                    question.setType(type != null ? type : (preferredType != null ? preferredType : "multiple"));

                    String difficulty = getStringValue(questionNode, "difficulty");
                    question.setDifficulty(difficulty != null ? difficulty : "medium");

                    // Validate question
                    if (isValidQuestion(question)) {
                        questions.add(question);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error parsing AI response: ", e);
        }

        return questions;
    }

    private String extractJSON(String response) {
        // Remove markdown code blocks if present
        String cleanedResponse = response.replaceAll("```json", "").replaceAll("```", "").trim();

        // Try to find JSON object
        int startIdx = cleanedResponse.indexOf('{');
        int endIdx = cleanedResponse.lastIndexOf('}');

        if (startIdx != -1 && endIdx != -1 && startIdx < endIdx) {
            return cleanedResponse.substring(startIdx, endIdx + 1);
        }

        return cleanedResponse;
    }

    private String getStringValue(JsonNode node, String fieldName) {
        if (node.has(fieldName) && !node.get(fieldName).isNull()) {
            return node.get(fieldName).asText().trim();
        }
        return null;
    }

    private boolean isValidQuestion(AIGeneratedQuestion question) {
        return question.getQuestion() != null && !question.getQuestion().isEmpty() &&
                question.getCorrect_answer() != null && !question.getCorrect_answer().isEmpty() &&
                question.getWrong_answer1() != null && !question.getWrong_answer1().isEmpty() &&
                question.getWrong_answer2() != null && !question.getWrong_answer2().isEmpty();
    }
}
