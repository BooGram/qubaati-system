package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.CareerWorldTargetInDTO;
import com.example.qubaatisystem.DTO.In.DecisionSubmitInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.MissionRegenerateInDTO;
import com.example.qubaatisystem.DTO.In.MissionSessionTargetInDTO;
import com.example.qubaatisystem.DTO.In.MissionStepBatchInDTO;
import com.example.qubaatisystem.DTO.In.MissionTargetInDTO;
import com.example.qubaatisystem.DTO.In.StartMissionSessionInDTO;
import com.example.qubaatisystem.DTO.Out.AvailableMissionOutDTO;
import com.example.qubaatisystem.DTO.Out.AvailableMissionsOutDTO;
import com.example.qubaatisystem.DTO.Out.DecisionSubmitOutDTO;
import com.example.qubaatisystem.DTO.Out.InsightOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionCompletionOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionStepsAdminOutDTO;
import com.example.qubaatisystem.DTO.Out.NotificationOutDTO;
import com.example.qubaatisystem.DTO.Out.RecommendationOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentMissionAttemptOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentSkillOutDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.MissionService;
import com.example.qubaatisystem.Service.MissionSessionService;
import com.example.qubaatisystem.Service.NotificationService;
import com.example.qubaatisystem.Service.RecommendationService;
import com.example.qubaatisystem.Service.StudentSkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Student-3 mission flow (REST controller, not a service): available missions, regeneration, the mission
 * session lifecycle (start/current/decision/complete/abandon/insight), and the student-facing
 * recommendation/notification/skill endpoints. Uses absolute paths because the routes span several resources.
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MissionFlowController {

    private final MissionService missionService;
    private final MissionSessionService missionSessionService;
    private final RecommendationService recommendationService;
    private final NotificationService notificationService;
    private final StudentSkillService studentSkillService;

    // ---------- available / default missions ----------

    // Current-student alias — the student comes from Basic Auth. careerWorldId is a non-id OPTION (RequestParam).
    @GetMapping("/students/me/missions/available")
    public ResponseEntity<AvailableMissionsOutDTO> getMyAvailableMissions(
            @AuthenticationPrincipal User user,
            @RequestParam Integer careerWorldId) {
        return ResponseEntity.status(200)
                .body(missionService.getMyAvailableMissions(user, careerWorldId));
    }

    @PostMapping("/career-worlds/missions")
    public ResponseEntity<List<AvailableMissionOutDTO>> getCareerWorldDefaultMissions(
            @Valid @RequestBody CareerWorldTargetInDTO request) {
        return ResponseEntity.status(200).body(missionService.getCareerWorldDefaultMissions(request.getCareerWorldId()));
    }

    // Current-student regenerate — the student comes from Basic Auth; the target missionId is in the body.
    @PatchMapping("/students/me/missions/regenerate")
    public ResponseEntity<AvailableMissionOutDTO> regenerateMyMission(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody MissionRegenerateInDTO request) {
        return ResponseEntity.status(200).body(missionService.regenerateMyMission(user, request));
    }

    // ---------- step authoring / seeding (teacher/admin) ----------

    @PostMapping("/missions/steps/batch")
    public ResponseEntity<MissionStepsAdminOutDTO> replaceMissionSteps(
            @Valid @RequestBody MissionStepBatchInDTO request) {
        return ResponseEntity.status(200).body(missionService.replaceSteps(request.getMissionId(), request));
    }

    @PostMapping("/missions/steps")
    public ResponseEntity<MissionStepsAdminOutDTO> getMissionSteps(@Valid @RequestBody MissionTargetInDTO request) {
        return ResponseEntity.status(200).body(missionService.getSteps(request.getMissionId()));
    }

    @DeleteMapping("/missions/steps")
    public ResponseEntity<ApiResponse> deleteMissionSteps(@Valid @RequestBody MissionTargetInDTO request) {
        missionService.deleteSteps(request.getMissionId());
        return ResponseEntity.status(200).body(new ApiResponse("Mission steps deleted"));
    }

    // ---------- mission session lifecycle ----------

    // studentId is OPTIONAL: a student starts as themselves (derived from Basic Auth); an admin may pass studentId.
    @PostMapping("/missions/sessions/start")
    public ResponseEntity<StudentMissionAttemptOutDTO> startSession(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StartMissionSessionInDTO request) {
        return ResponseEntity.status(200)
                .body(missionSessionService.startSession(user, request));
    }

    @PostMapping("/mission-sessions/current")
    public ResponseEntity<StudentMissionAttemptOutDTO> getCurrentSession(
            @Valid @RequestBody MissionSessionTargetInDTO request) {
        return ResponseEntity.status(200).body(missionSessionService.getCurrentSession(request.getSessionId()));
    }

    @PostMapping("/mission-sessions/decisions")
    public ResponseEntity<DecisionSubmitOutDTO> submitDecision(@Valid @RequestBody DecisionSubmitInDTO request) {
        return ResponseEntity.status(200)
                .body(missionSessionService.submitDecision(request.getSessionId(), request.getChoiceId(), request.getReason()));
    }

    @PatchMapping("/mission-sessions/complete")
    public ResponseEntity<MissionCompletionOutDTO> completeSession(
            @Valid @RequestBody MissionSessionTargetInDTO request) {
        return ResponseEntity.status(200).body(missionSessionService.completeSession(request.getSessionId()));
    }

    @PatchMapping("/mission-sessions/abandon")
    public ResponseEntity<ApiResponse> abandonSession(@Valid @RequestBody MissionSessionTargetInDTO request) {
        missionSessionService.abandonSession(request.getSessionId());
        return ResponseEntity.status(200).body(new ApiResponse("Mission session abandoned"));
    }

    @PostMapping("/mission-sessions/insight")
    public ResponseEntity<InsightOutDTO> getInsight(@Valid @RequestBody MissionSessionTargetInDTO request) {
        return ResponseEntity.status(200).body(missionSessionService.getInsight(request.getSessionId()));
    }

    /** OPTIONAL manual re-run of the AI-first insight (the normal flow generates it on completion). */
    @PatchMapping("/mission-sessions/insight/regenerate")
    public ResponseEntity<InsightOutDTO> regenerateInsight(@Valid @RequestBody MissionSessionTargetInDTO request) {
        return ResponseEntity.status(200).body(missionSessionService.regenerateInsight(request.getSessionId()));
    }

    // ---------- recommendations ----------

    // Current-student recommendation endpoints (the student comes from Basic Auth).
    @GetMapping("/students/me/recommendations")
    public ResponseEntity<List<RecommendationOutDTO>> getMyRecommendations(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(recommendationService.getMyRecommendations(user));
    }

    @PostMapping("/students/me/recommendations/regenerate")
    public ResponseEntity<List<RecommendationOutDTO>> regenerateMyRecommendations(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200)
                .body(missionSessionService.regenerateMyRecommendations(user));
    }

    // accept / dismiss / complete are STUDENT-or-ADMIN actions, restricted to the owning student.
    @PatchMapping("/recommendations/accept")
    public ResponseEntity<RecommendationOutDTO> acceptRecommendation(
            @AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO request) {
        return ResponseEntity.status(200).body(recommendationService.acceptOwned(user, request));
    }

    @PatchMapping("/recommendations/dismiss")
    public ResponseEntity<RecommendationOutDTO> dismissRecommendation(
            @AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO request) {
        return ResponseEntity.status(200).body(recommendationService.dismissOwned(user, request));
    }

    @PatchMapping("/recommendations/complete")
    public ResponseEntity<RecommendationOutDTO> completeRecommendation(
            @AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO request) {
        return ResponseEntity.status(200).body(recommendationService.completeOwned(user, request));
    }

    // ---------- notifications (userId ownership: a user only sees their OWN notifications) ----------

    // Current-user ("me") notification endpoints — the user comes from Basic Auth.
    @GetMapping("/users/me/notifications")
    public ResponseEntity<List<NotificationOutDTO>> getMyNotifications(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getMyNotifications(user));
    }

    @GetMapping("/users/me/notifications/unread")
    public ResponseEntity<List<NotificationOutDTO>> getMyUnreadNotifications(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(notificationService.getMyUnreadNotifications(user));
    }

    @PatchMapping("/users/me/notifications/read")
    public ResponseEntity<NotificationOutDTO> markMyNotificationRead(
            @AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO request) {
        return ResponseEntity.status(200).body(notificationService.markMyNotificationRead(user, request));
    }

    @PatchMapping("/users/me/notifications/read-all")
    public ResponseEntity<ApiResponse> markAllMyNotificationsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllMyNotificationsRead(user);
        return ResponseEntity.status(200).body(new ApiResponse("All notifications marked as read"));
    }

    // ---------- skills ----------

    @GetMapping("/students/me/skills")
    public ResponseEntity<List<StudentSkillOutDTO>> getMySkills(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(studentSkillService.getMySkills(user));
    }
}
