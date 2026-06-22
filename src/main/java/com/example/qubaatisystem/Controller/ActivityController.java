package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.ActivityInDTO;
import com.example.qubaatisystem.DTO.In.ActivityReviewActionInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityDetailsOutDTO;
import com.example.qubaatisystem.Enum.ActivityStatus;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ActivityService;
import com.example.qubaatisystem.Service.AiActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/activities")
@RequiredArgsConstructor
public class ActivityController {

    private final ActivityService activityService;
    private final AiActivityService aiActivityService;

    // ---------- CRUD ----------

    // The owner is derived from Basic Auth: a TEACHER owns the activity (body teacherId ignored); an ADMIN may
    // supply teacherId to create on a teacher's behalf. PARENT/STUDENT are blocked by role + this resolver.
    @PostMapping("/add")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @Valid @RequestBody ActivityInDTO dto) {
        activityService.create(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Activity created successfully"));
    }

    // Optional status filter (enum, not free text): GET /activities/get-all?status=PENDING_REVIEW serves the
    // review queue / status views. No param returns all activities (backward compatible).
    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@RequestParam(required = false) ActivityStatus status) {
        return ResponseEntity.status(200).body(activityService.getByStatus(status));
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activityService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ActivityInDTO dto) {
        activityService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Activity updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        activityService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Activity deleted successfully"));
    }

    // ---------- REVIEW / APPROVAL FLOW ----------
    // There is no public submit-for-review endpoint: AI generation auto-submits the activity to the
    // review queue (Issue 3). The transition stays available as ActivityService.submitForReview(...).

    @GetMapping("/review-queue")
    public ResponseEntity<?> getReviewQueue() {
        return ResponseEntity.status(200).body(activityService.getReviewQueue());
    }

    @PatchMapping("/approve")
    public ResponseEntity<?> approve(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ActivityReviewActionInDTO request) {
        activityService.approveActivity(user, request);
        return ResponseEntity.status(200).body(new ApiResponse("Activity approved successfully"));
    }

    @PatchMapping("/reject")
    public ResponseEntity<?> reject(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ActivityReviewActionInDTO request) {
        activityService.rejectActivity(user, request);
        return ResponseEntity.status(200).body(new ApiResponse("Activity rejected successfully"));
    }

    @PatchMapping("/request-revision")
    public ResponseEntity<?> requestRevision(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ActivityReviewActionInDTO request) {
        activityService.requestRevision(user, request);
        return ResponseEntity.status(200).body(new ApiResponse("Revision requested successfully"));
    }

    @PostMapping("/review-history")
    public ResponseEntity<?> getReviewHistory(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activityService.getReviewHistory(dto.getId()));
    }

    // Teacher/reviewer full activity details — includes questions, options, correctAnswer and isCorrect.
    // NOT student-safe (the student start/reopen views deliberately hide correct answers).
    @PostMapping("/details")
    public ResponseEntity<ActivityDetailsOutDTO> getActivityDetails(
            @Valid @RequestBody IdInDTO dto,
            @RequestParam(defaultValue = "en") String language) {
        return ResponseEntity.status(200).body(aiActivityService.getActivityDetails(dto.getId(), language));
    }
}
