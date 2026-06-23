package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Service.AiAnalysisService;
import com.example.qubaatisystem.DTO.In.AiGenerateActivityInDTO;
import com.example.qubaatisystem.DTO.In.ChildSummaryInDTO;
import com.example.qubaatisystem.DTO.In.ClassroomSummaryInDTO;
import com.example.qubaatisystem.DTO.In.SubmissionTargetInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityDetailsOutDTO;
import com.example.qubaatisystem.DTO.Out.ActivitySubmissionOutDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.AiActivityService;
import com.example.qubaatisystem.Service.AiProviderHealthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiActivityService aiActivityService;
    private final AiAnalysisService aiAnalysisService;
    private final AiProviderHealthService aiProviderHealthService;

    // AI provider health/config — reports whether a key is configured and which model is set (key never
    // exposed). Pass ?probe=true to additionally send a tiny live ChatClient request.
    @GetMapping("/health")
    public ResponseEntity<?> health(@RequestParam(defaultValue = "false") boolean probe) {
        return ResponseEntity.status(200).body(aiProviderHealthService.health(probe));
    }

    // The generated activity's owner is derived from Basic Auth (teacher owns it; admin may pass a teacherId).
    @PostMapping("/activities/generate")
    public ResponseEntity<ActivityDetailsOutDTO> generateActivity(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody AiGenerateActivityInDTO dto,
            @RequestParam(defaultValue = "en") String language) {
        return ResponseEntity.status(200).body(aiActivityService.generateActivity(user, dto, language));
    }

    // Body-based refine: activityId is the target in the body; the teacher comes from Basic Auth and must own it.
    // Refines the FULL activity (title/description/questions/options/correctAnswer/points).
    @PostMapping("/activities/refine")
    public ResponseEntity<ActivityDetailsOutDTO> refine(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody com.example.qubaatisystem.DTO.In.AiActivityRefineInDTO body,
            @RequestParam(defaultValue = "en") String language) {
        return ResponseEntity.status(200).body(aiActivityService.refineActivity(user, body, language));
    }

    // Body-based evaluate: submissionId is the target in the body; the teacher comes from Basic Auth and must own it.
    @PostMapping("/activity-submissions/evaluate")
    public ResponseEntity<ActivitySubmissionOutDTO> evaluateSubmission(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmissionTargetInDTO body,
            @RequestParam(defaultValue = "en") String language) {
        return ResponseEntity.status(200).body(aiActivityService.evaluateSubmission(user, body, language));
    }

    // Body-based feedback generation: submissionId is the target in the body; the teacher comes from Basic Auth.
    @PostMapping("/activity-submissions/generate-feedback")
    public ResponseEntity<ActivitySubmissionOutDTO> generateFeedback(@AuthenticationPrincipal User user, @Valid @RequestBody SubmissionTargetInDTO body, @RequestParam(defaultValue = "student") String audience, @RequestParam(defaultValue = "en") String language) {
        return ResponseEntity.status(200).body(aiActivityService.generateFeedback(user, body, audience, language));
    }

    // classroomId is an ENTITY id (not a profile id) — a teacher may summarize only their own classroom.
    @PostMapping("/classrooms/summary")
    public ResponseEntity<?> getClassroomSummary(@AuthenticationPrincipal User user, @Valid @RequestBody ClassroomSummaryInDTO body) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeClassroom(user, body));
    }

    // ===== Current-user ("me") AI endpoints — no profile id in the path =====

    @PostMapping("/teachers/me/dashboard-insight")
    public ResponseEntity<?> getMyTeacherDashboardInsight(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeMyTeacherDashboard(user));
    }

    @PostMapping("/parents/me/dashboard-insight")
    public ResponseEntity<?> getMyFamilyDashboardInsight(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeMyFamilyInsight(user));
    }

    // Body-based child summary: studentId is the target in the body; the parent comes from Basic Auth and must own the child.
    @PostMapping("/parents/children/summary")
    public ResponseEntity<?> getMyChildSummary(@AuthenticationPrincipal User user, @Valid @RequestBody ChildSummaryInDTO body) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeMyChild(user, body));
    }
}
