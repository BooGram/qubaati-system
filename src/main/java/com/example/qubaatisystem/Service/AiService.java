package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.Model.CareerWorld;
import com.example.qubaatisystem.Model.LearningStyle;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentSkill;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AiService {

    private final ChatClient chatClient;

    public AiService(ChatClient.Builder builder){
        chatClient = builder.build();
    }

    public String chat(String prompt){
        return chatClient.prompt(prompt).call().content();
    }

    public String sendJsonPrompt(String prompt) {
        String aiJson = chat(prompt);

        if (aiJson == null || aiJson.isBlank()) {
            throw new ApiException("AI request failed");
        }

        return cleanAiJsonResponse(aiJson);
    }

    public String cleanAiJsonResponse(String aiMissionJson) {
        String cleanedJson = aiMissionJson.trim();

        if (cleanedJson.startsWith("```json")) {
            cleanedJson = cleanedJson.substring(7).trim();
        } else if (cleanedJson.startsWith("```")) {
            cleanedJson = cleanedJson.substring(3).trim();
        }

        if (cleanedJson.endsWith("```")) {
            cleanedJson = cleanedJson.substring(0, cleanedJson.length() - 3).trim();
        }

        return cleanedJson;
    }


    //generate student mission
    public String generateMissionJson(Student student, CareerWorld careerWorld) {
        String prompt = buildAiMissionPrompt(student, careerWorld);
        return sendJsonPrompt(prompt);
    }

    private String buildAiMissionPrompt(Student student, CareerWorld careerWorld) {
        LearningStyle learningStyle = student.getLearningStyle();
        String learningStyleText;

        if (learningStyle == null) {
            learningStyleText = "- Primary style: MIXED\n"
                    + "- Secondary style: Not detected\n"
                    + "- Confidence: Not detected";
        } else {
            learningStyleText = "- Primary style: " + learningStyle.getPrimaryStyle() + "\n"
                    + "- Secondary style: " + learningStyle.getSecondaryStyle() + "\n"
                    + "- Confidence: " + learningStyle.getConfidence();
        }

        return "You are generating one interactive educational mission for a child.\n"
                + "Return ONLY valid JSON. Do not return markdown or explanation.\n"
                + "All fields are required. Do not return null, blank, empty, zero, or negative values.\n\n"
                + "Required JSON shape:\n"
                + "{\n"
                + "  \"title\": \"short mission title\",\n"
                + "  \"scenario\": \"one clear scenario question without the choices text\",\n"
                + "  \"choices\": [\n"
                + "    {\"key\": \"A\", \"text\": \"first choice\", \"scoreImpact\": 10},\n"
                + "    {\"key\": \"B\", \"text\": \"second choice\", \"scoreImpact\": 20},\n"
                + "    {\"key\": \"C\", \"text\": \"third choice\", \"scoreImpact\": 15}\n"
                + "  ],\n"
                + "  \"skill\": {\n"
                + "    \"name\": \"skill name\",\n"
                + "    \"description\": \"skill description\",\n"
                + "    \"skillType\": \"one of PROBLEM_SOLVING, DECISION_MAKING, FOCUS, ENGAGEMENT, REASONING, COMMUNICATION, LOGIC\"\n"
                + "  },\n"
                + "  \"difficulty\": \"one of EASY, MEDIUM, HARD\",\n"
                + "  \"estimatedMinutes\": 5,\n"
                + "  \"maxScore\": 100\n"
                + "}\n\n"
                + "Student profile:\n"
                + "- Name: " + student.getFullName() + "\n"
                + "- Age: " + student.getAge() + "\n"
                + "- Grade: " + student.getGrade() + "\n"
                + "- Total points: " + student.getTotalPoints() + "\n"
                + "- Completed missions count: " + student.getCompletedMissionsCount() + "\n\n"
                + "Learning style:\n"
                + learningStyleText + "\n\n"
                + "Student skills:\n"
                + getStudentSkillsPrompt(student) + "\n\n"
                + "Career world:\n"
                + "- Title: " + careerWorld.getTitle() + "\n"
                + "- Category: " + careerWorld.getCategory() + "\n"
                + "- Description: " + careerWorld.getDescription() + "\n\n"
                + "Generation rules:\n"
                + "- Analyze the student's profile, skills, scores, levels, and learning style yourself.\n"
                + "- Choose the best target skill based only on the provided student data.\n"
                + "- Choose the difficulty yourself based only on the provided student data.\n"
                + "- Personalize the mission using the student's age, grade, points, skills, and learning style.\n"
                + "- Keep the mission child-friendly and focused on one clear decision.\n"
                + "- Return exactly 3 choices.\n"
                + "- Do not mark choices as correct or incorrect.\n"
                + "- More than one choice can be reasonable because the goal is to understand the student's thinking.\n"
                + "- Every choice must include key, text, and scoreImpact.\n"
                + "- scoreImpact must be a positive number that reflects how strong or thoughtful the choice is.";
    }

    private String getStudentSkillsPrompt(Student student) {
        Set<StudentSkill> studentSkills = student.getStudentSkills();
        if (studentSkills == null || studentSkills.isEmpty()) {
            return "- No skills recorded yet.";
        }

        String skillsText = "";

        for (StudentSkill studentSkill : studentSkills) {
            if (studentSkill.getSkill() == null) {
                continue;
            }

            skillsText += "- " + studentSkill.getSkill().getName()
                    + " | Type: " + studentSkill.getSkill().getSkillType()
                    + " | Score: " + studentSkill.getScore()
                    + " | Level: " + studentSkill.getLevel()
                    + "\n";
        }

        if (skillsText.isBlank()) {
            return "- No skills recorded yet.";
        }

        return skillsText;
    }
}
