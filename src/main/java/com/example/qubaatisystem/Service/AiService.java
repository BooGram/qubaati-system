package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.MissionInsightAiResult;
import com.example.qubaatisystem.DTO.In.RecommendationAiItem;
import com.example.qubaatisystem.DTO.In.RecommendationsAiResult;
import com.example.qubaatisystem.Model.CareerWorld;
import com.example.qubaatisystem.Model.Decision;
import com.example.qubaatisystem.Model.Insight;
import com.example.qubaatisystem.Model.LearningStyle;
import com.example.qubaatisystem.Model.Mission;
import com.example.qubaatisystem.Model.MissionSession;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentSkill;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Student-3 AI features via Spring AI {@link ChatClient}: multi-step mission generation, mission-insight
 * generation, and recommendation generation. Each "generateX" method returns a parsed result or {@code null}
 * when the call fails / key is missing / JSON is invalid, so callers apply their deterministic fallback.
 */
@Service
public class AiService {

    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    public AiService(ChatClient.Builder builder, ObjectMapper objectMapper) {
        this.chatClient = builder.build();
        this.objectMapper = objectMapper;
    }

    public String chat(String prompt) {
        return chatClient.prompt(prompt).call().content();
    }

    public String sendJsonPrompt(String prompt) {
        String aiJson = chat(prompt);
        if (aiJson == null || aiJson.isBlank()) {
            throw new ApiException("AI request failed");
        }
        return cleanAiJsonResponse(aiJson);
    }

    public String cleanAiJsonResponse(String aiJson) {
        String cleaned = aiJson.trim();
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

    // ====================== mission generation (multi-step) ======================

    /** Generates a personalized MULTI-STEP mission as JSON. Throws ApiException on failure (caller falls back). */
    public String generateMissionJson(Student student, CareerWorld careerWorld) {
        return sendJsonPrompt(buildAiMissionPrompt(student, careerWorld));
    }

    private String buildAiMissionPrompt(Student student, CareerWorld careerWorld) {
        return "You are generating ONE interactive, multi-step educational mission for a child.\n"
                + "Return ONLY valid JSON. No markdown, no explanation.\n"
                + "All fields are required unless stated optional. Do not return null/blank/empty values except nextStepOrder.\n\n"
                + "Required JSON shape:\n"
                + "{\n"
                + "  \"title\": \"short mission title\",\n"
                + "  \"description\": \"one short sentence describing the mission\",\n"
                + "  \"scenario\": \"one short overall intro sentence (no choices)\",\n"
                + "  \"difficulty\": \"one of EASY, MEDIUM, HARD\",\n"
                + "  \"estimatedMinutes\": 5,\n"
                + "  \"maxScore\": 100,\n"
                + "  \"skill\": { \"name\": \"skill name\", \"description\": \"skill description\", \"skillType\": \"one of PROBLEM_SOLVING, DECISION_MAKING, FOCUS, ENGAGEMENT, REASONING, COMMUNICATION, LOGIC\" },\n"
                + "  \"steps\": [\n"
                + "    { \"stepOrder\": 1, \"scenario\": \"first step scenario\", \"choices\": [\n"
                + "      { \"content\": \"choice text\", \"scoreImpact\": 10, \"nextStepOrder\": 2 },\n"
                + "      { \"content\": \"choice text\", \"scoreImpact\": 5, \"nextStepOrder\": 2 }\n"
                + "    ] },\n"
                + "    { \"stepOrder\": 2, \"scenario\": \"final step scenario\", \"choices\": [\n"
                + "      { \"content\": \"choice text\", \"scoreImpact\": 10, \"nextStepOrder\": null },\n"
                + "      { \"content\": \"choice text\", \"scoreImpact\": 0, \"nextStepOrder\": null }\n"
                + "    ] }\n"
                + "  ]\n"
                + "}\n\n"
                + "Student profile:\n"
                + "- Name: " + student.getFullName() + "\n"
                + "- Age: " + student.getAge() + "\n"
                + "- Grade: " + student.getGrade() + "\n"
                + "- Total points: " + student.getTotalPoints() + "\n"
                + "- Completed missions count: " + student.getCompletedMissionsCount() + "\n\n"
                + "Learning style:\n" + learningStyleText(student.getLearningStyle()) + "\n\n"
                + "Student skills:\n" + getStudentSkillsPrompt(student) + "\n\n"
                + "Career world:\n"
                + "- Title: " + careerWorld.getTitle() + "\n"
                + "- Category: " + careerWorld.getCategory() + "\n"
                + "- Description: " + careerWorld.getDescription() + "\n\n"
                + "Generation rules:\n"
                + "- Personalize using the student's age, grade, skills and learning style; child-friendly.\n"
                + "- Produce 2 to 3 connected steps; each step has 2 or 3 choices.\n"
                + "- stepOrder starts at 1 and increases by 1.\n"
                + "- For non-final steps, every choice's nextStepOrder points to a real later step.\n"
                + "- For the LAST step, every choice's nextStepOrder must be null.\n"
                + "- Do not mark choices correct/incorrect; more than one can be reasonable.\n"
                + "- scoreImpact reflects how strong/thoughtful the choice is (may be 0 or negative for weak choices).";
    }

    // ====================== mission insight ======================

    /** AI-FIRST mission insight. Returns null on any failure so the caller uses the rule-based fallback. */
    public MissionInsightAiResult generateMissionInsight(Student student, Mission mission, MissionSession session,
                                                         List<Decision> decisions, List<StudentSkill> currentSkills,
                                                         LearningStyle learningStyle) {
        try {
            String json = sendJsonPrompt(buildInsightPrompt(student, mission, session, decisions, currentSkills, learningStyle));
            MissionInsightAiResult result = objectMapper.readValue(json, MissionInsightAiResult.class);
            if (result == null || result.getSummary() == null || result.getSummary().isBlank()) {
                return null;
            }
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    private String buildInsightPrompt(Student student, Mission mission, MissionSession session,
                                      List<Decision> decisions, List<StudentSkill> currentSkills,
                                      LearningStyle learningStyle) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are an educational analyst. Analyze a child's decisions in an interactive mission.\n");
        sb.append("Return ONLY valid JSON. No markdown, no explanation. Be supportive and non-judgmental.\n");
        sb.append("Required JSON shape (all integer scores 0-100):\n");
        sb.append("{ \"summary\": \"short paragraph\", \"recommendation\": \"one practical recommendation\", ");
        sb.append("\"focusScore\": 0, \"engagementScore\": 0, \"reasoningScore\": 0, ");
        sb.append("\"problemSolvingScore\": 0, \"decisionMakingScore\": 0 }\n\n");
        sb.append("Student: ").append(student != null ? student.getFullName() : "")
                .append(" | Age: ").append(student != null ? student.getAge() : "")
                .append(" | Grade: ").append(student != null ? student.getGrade() : "").append("\n");
        if (mission != null) {
            sb.append("Mission: ").append(mission.getTitle())
                    .append(" | Source: ").append(mission.getSource())
                    .append(" | Difficulty: ").append(mission.getDifficulty())
                    .append(" | MaxScore: ").append(mission.getMaxScore()).append("\n");
        }
        if (session != null) {
            sb.append("Final score: ").append(session.getScore()).append("\n");
        }
        sb.append("Learning style: ").append(learningStyleText(learningStyle).replace("\n", " ")).append("\n");
        sb.append("Current skills:\n").append(skillsList(currentSkills)).append("\n");
        sb.append("Decisions (the student's path; scoreImpact is internal, do not reveal it):\n");
        if (decisions != null) {
            int i = 1;
            for (Decision d : decisions) {
                Integer order = d.getMissionStep() != null ? d.getMissionStep().getStepOrder() : i;
                String stepScenario = d.getMissionStep() != null ? d.getMissionStep().getScenario() : "";
                sb.append("- Step ").append(order).append(": ").append(stepScenario)
                        .append(" | chose: \"").append(d.getChoice()).append("\"")
                        .append(" | reason: \"").append(d.getReason() == null ? "" : d.getReason()).append("\"")
                        .append(" | responseTimeSeconds: ").append(d.getResponseTimeSeconds())
                        .append(" | scoreImpact: ").append(d.getScoreImpact()).append("\n");
                i++;
            }
        }
        sb.append("\nRules: base the analysis ONLY on the data above. Do not invent numbers or names.");
        return sb.toString();
    }

    // ====================== recommendations ======================

    /** AI-FIRST recommendations. Returns null on any failure so the caller uses the rule-based fallback. */
    public List<RecommendationAiItem> generateRecommendations(Student student, List<Insight> recentInsights,
                                                              List<StudentSkill> skills, LearningStyle learningStyle,
                                                              List<MissionSession> completedSessions) {
        try {
            String json = sendJsonPrompt(buildRecommendationsPrompt(student, recentInsights, skills, learningStyle, completedSessions));
            RecommendationsAiResult result = objectMapper.readValue(json, RecommendationsAiResult.class);
            if (result == null || result.getRecommendations() == null || result.getRecommendations().isEmpty()) {
                return null;
            }
            return result.getRecommendations();
        } catch (Exception e) {
            return null;
        }
    }

    private String buildRecommendationsPrompt(Student student, List<Insight> recentInsights, List<StudentSkill> skills,
                                              LearningStyle learningStyle, List<MissionSession> completedSessions) {
        StringBuilder sb = new StringBuilder();
        sb.append("You generate supportive learning recommendations for a child.\n");
        sb.append("Return ONLY valid JSON. No markdown. Provide 1 to 3 recommendations.\n");
        sb.append("Required JSON shape:\n");
        sb.append("{ \"recommendations\": [ { \"title\": \"...\", \"description\": \"...\", ");
        sb.append("\"type\": \"one of ACTIVITY, MISSION, SKILL_PRACTICE, REVIEW, LEARNING_PATH\", ");
        sb.append("\"priority\": \"one of LOW, MEDIUM, HIGH\" } ] }\n\n");
        sb.append("Student: ").append(student != null ? student.getFullName() : "")
                .append(" | Age: ").append(student != null ? student.getAge() : "").append("\n");
        sb.append("Learning style: ").append(learningStyleText(learningStyle).replace("\n", " ")).append("\n");
        sb.append("Skills:\n").append(skillsList(skills)).append("\n");
        sb.append("Completed sessions: ").append(completedSessions != null ? completedSessions.size() : 0).append("\n");
        if (recentInsights != null && !recentInsights.isEmpty()) {
            sb.append("Recent insight summaries:\n");
            for (Insight in : recentInsights) {
                if (in != null && in.getSummary() != null) {
                    sb.append("- ").append(in.getSummary()).append("\n");
                }
            }
        }
        sb.append("\nRules: base recommendations ONLY on the data above; keep them encouraging and practical.");
        return sb.toString();
    }

    // ====================== helpers ======================

    private String learningStyleText(LearningStyle learningStyle) {
        if (learningStyle == null) {
            return "- Primary style: MIXED\n- Secondary style: Not detected\n- Confidence: Not detected";
        }
        return "- Primary style: " + learningStyle.getPrimaryStyle() + "\n"
                + "- Secondary style: " + learningStyle.getSecondaryStyle() + "\n"
                + "- Confidence: " + learningStyle.getConfidence();
    }

    private String getStudentSkillsPrompt(Student student) {
        return skillsList(student != null && student.getStudentSkills() != null
                ? List.copyOf(student.getStudentSkills()) : List.of());
    }

    private String skillsList(List<StudentSkill> studentSkills) {
        if (studentSkills == null || studentSkills.isEmpty()) {
            return "- No skills recorded yet.";
        }
        StringBuilder sb = new StringBuilder();
        for (StudentSkill ss : studentSkills) {
            if (ss != null && ss.getSkill() != null) {
                sb.append("- ").append(ss.getSkill().getName())
                        .append(" | Type: ").append(ss.getSkill().getSkillType())
                        .append(" | Score: ").append(ss.getScore())
                        .append(" | Level: ").append(ss.getLevel()).append("\n");
            }
        }
        return sb.length() == 0 ? "- No skills recorded yet." : sb.toString();
    }
}
