package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.DTO.In.AiAnswerGradeResult;
import com.example.qubaatisystem.Model.ActivitySubmission;
import com.example.qubaatisystem.Model.Question;
import com.example.qubaatisystem.Model.StudentAnswer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

/**
 * AI-assisted grading of free-text (SHORT_ANSWER / OPEN_ENDED) answers via Spring AI {@link ChatClient}.
 *
 * <p>This is a deliberately tiny, dependency-light service: it depends ONLY on {@code ChatClient} and Jackson,
 * never on {@code ActivitySubmissionService}. That matters because {@code AiActivityService} already injects
 * {@code ActivitySubmissionService}; injecting an AI grader into {@code ActivitySubmissionService} therefore
 * has to avoid {@code AiActivityService} to keep the bean graph acyclic. {@code ActivitySubmissionService}
 * injects THIS service instead, so there is no cycle.
 *
 * <p>The question's correctAnswer is sent to the model for comparison but is NEVER returned to the student;
 * only the resulting points/status/feedback are used, and the student-facing DTOs never include them.
 */
@Service
public class AiAnswerGradingService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiAnswerGradingService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.chatClient = chatClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Grades one text answer. Returns a structured result, or {@code null} on any failure (no/invalid key,
     * blank or invalid JSON) so the caller applies its deterministic fallback. earnedPoints is left to the
     * caller to clamp into [0, question.points].
     */
    public AiAnswerGradeResult gradeTextAnswer(Question question, StudentAnswer answer, ActivitySubmission submission) {
        if (question == null || answer == null) {
            return null;
        }
        int maxPoints = question.getPoints() != null ? question.getPoints() : 0;
        String studentText = answer.getAnswerText() == null ? "" : answer.getAnswerText().trim();
        if (studentText.isEmpty()) {
            return null; // nothing to grade -> caller marks INCORRECT deterministically
        }
        try {
            String system = "You are a fair, encouraging teacher grading a child's short text answer. "
                    + "Compare the student's answer to the reference answer for meaning, not exact wording. "
                    + "Award partial credit when the answer is partly right. "
                    + "Respond with STRICT JSON ONLY, no markdown, no extra text, in this exact shape: "
                    + "{\"earnedPoints\": <integer 0..MAX>, \"status\": \"CORRECT|INCORRECT|PARTIAL\", "
                    + "\"feedback\": \"one short, kind sentence for the student\"}.";
            String user = "Question: " + safe(question.getContent()) + "\n"
                    + "Question type: " + question.getType() + "\n"
                    + "Maximum points (MAX): " + maxPoints + "\n"
                    + "Reference answer (do NOT reveal it to the student): " + safe(question.getCorrectAnswer()) + "\n"
                    + "Student answer: " + studentText + "\n"
                    + "Grade now. earnedPoints must be between 0 and " + maxPoints
                    + ". Use CORRECT only for full marks, INCORRECT for no credit, otherwise PARTIAL.";

            String content = chatClient.prompt().system(system).user(user).call().content();
            if (content == null || content.isBlank()) {
                return null;
            }
            AiAnswerGradeResult result = objectMapper.readValue(cleanJson(content), AiAnswerGradeResult.class);
            if (result == null || result.getEarnedPoints() == null) {
                return null;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

    /** Strips ```json fences the model sometimes adds so the JSON parses. */
    private String cleanJson(String raw) {
        String cleaned = raw.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7).trim();
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3).trim();
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3).trim();
        }
        return cleaned;
    }
}
