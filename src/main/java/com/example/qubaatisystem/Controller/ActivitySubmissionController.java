package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.ActivitySubmissionInDTO;
import com.example.qubaatisystem.DTO.In.ActivitySubmissionReturnInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.StartAssignmentInDTO;
import com.example.qubaatisystem.DTO.In.SubmissionTargetInDTO;
import com.example.qubaatisystem.DTO.In.TeacherFeedbackInDTO;
import com.example.qubaatisystem.DTO.Out.ActivitySubmissionOutDTO;
import com.example.qubaatisystem.DTO.Out.ActivitySubmissionTeacherDetailsOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentActivityAttemptOutDTO;
import com.example.qubaatisystem.Service.ActivitySubmissionService;
import jakarta.validation.Valid;
import com.example.qubaatisystem.Model.User;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActivitySubmissionController {

    private final ActivitySubmissionService activitySubmissionService;

    // ---------- CRUD ----------

    @PostMapping("/activity-submissions/add")
    public ResponseEntity<?> create(@Valid @RequestBody ActivitySubmissionInDTO dto) {
        activitySubmissionService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("ActivitySubmission created successfully"));
    }

    @GetMapping("/activity-submissions/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(activitySubmissionService.getAll());
    }

    @PostMapping("/activity-submissions/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activitySubmissionService.getById(dto.getId()));
    }

    @PutMapping("/activity-submissions/update")
    public ResponseEntity<?> update(@Valid @RequestBody ActivitySubmissionInDTO dto) {
        activitySubmissionService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("ActivitySubmission updated successfully"));
    }

    @DeleteMapping("/activity-submissions/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        activitySubmissionService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("ActivitySubmission deleted successfully"));
    }

    // ---------- FLOW: SUBMISSION ----------

    // Body-based start: the student is derived from Basic Auth (no studentId). The assignment must be for them.
    @PostMapping("/activity-assignments/start")
    public ResponseEntity<StudentActivityAttemptOutDTO> start(@AuthenticationPrincipal User user, @Valid @RequestBody StartAssignmentInDTO body) {
        return ResponseEntity.status(200).body(activitySubmissionService.startAssignment(user, body));
    }

    // Body-based current-submission view: the student must own the submission.
    @PostMapping("/activity-submissions/current")
    public ResponseEntity<ActivitySubmissionOutDTO> getCurrentSubmission(@AuthenticationPrincipal User user, @Valid @RequestBody SubmissionTargetInDTO body) {
        return ResponseEntity.status(200).body(activitySubmissionService.getCurrentSubmission(user, body));
    }

    // Body-based submit: the student must own the submission.
    @PostMapping("/activity-submissions/submit")
    public ResponseEntity<ActivitySubmissionOutDTO> submit(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SubmissionTargetInDTO body,
            @RequestParam(defaultValue = "en") String language) {
        // Submit validates all questions are answered, then evaluates automatically -> GRADED with feedback.
        return ResponseEntity.status(200).body(activitySubmissionService.submitActivity(user, body, language));
    }

    // Body-based result.
    @PostMapping("/activity-submissions/result")
    public ResponseEntity<ActivitySubmissionOutDTO> result(@AuthenticationPrincipal User user, @Valid @RequestBody SubmissionTargetInDTO body) {
        return ResponseEntity.status(200).body(activitySubmissionService.getSubmissionResult(user, body));
    }

    // Body-based feedback view: the student must own the submission.
    @PostMapping("/activity-submissions/feedback")
    public ResponseEntity<ActivitySubmissionOutDTO> getSubmissionFeedback(@AuthenticationPrincipal User user, @Valid @RequestBody SubmissionTargetInDTO body) {
        return ResponseEntity.status(200).body(activitySubmissionService.getSubmissionFeedback(user, body));
    }

    @GetMapping("/students/me/activity-results")
    public ResponseEntity<List<ActivitySubmissionOutDTO>> getMyActivityResults(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(activitySubmissionService.getMyActivityResults(user));
    }

    @PatchMapping("/activity-submissions/return-to-student")
    public ResponseEntity<ApiResponse> returnToStudent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody ActivitySubmissionReturnInDTO request) {
        activitySubmissionService.returnToStudent(user, request);
        return ResponseEntity.status(200).body(new ApiResponse("Submission returned to student successfully"));
    }

    @PatchMapping("/activity-submissions/teacher-feedback")
    public ResponseEntity<ActivitySubmissionOutDTO> addTeacherFeedback(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TeacherFeedbackInDTO request) {
        return ResponseEntity.status(200).body(activitySubmissionService.addTeacherFeedback(user, request));
    }

    // Body-based reopen: the teacher must own the submission.
    @PatchMapping("/activity-submissions/reopen")
    public ResponseEntity<StudentActivityAttemptOutDTO> reopenSubmission(@AuthenticationPrincipal User user, @Valid @RequestBody SubmissionTargetInDTO body) {
        return ResponseEntity.status(200).body(activitySubmissionService.reopenSubmission(user, body));
    }

    @GetMapping("/teachers/me/activity-submissions/pending-grading")
    public ResponseEntity<List<ActivitySubmissionOutDTO>> getMyPendingGradingSubmissions(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(activitySubmissionService.getMyPendingGradingSubmissions(user));
    }

    // ---------- TEACHER SUBMISSION LISTS / DETAILS ----------

    // Body-based: list submissions for an assignment (reuses StartAssignmentInDTO{assignmentId}). Teacher-only view.
    @PostMapping("/activity-submissions/by-assignment")
    public ResponseEntity<List<ActivitySubmissionOutDTO>> getSubmissionsByAssignment(
            @AuthenticationPrincipal User user, @Valid @RequestBody StartAssignmentInDTO body) {
        return ResponseEntity.status(200).body(activitySubmissionService.getSubmissionsByAssignment(user, body));
    }

    // Body-based: list submissions for an activity. activityId is a target in the body. This left the
    // /activities/** (TEACHER) prefix, so the teacher-owns-activity guard is enforced here.
    @PostMapping("/activity-submissions/by-activity")
    public ResponseEntity<List<ActivitySubmissionOutDTO>> getSubmissionsByActivity(
            @AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activitySubmissionService.getSubmissionsByActivity(user, dto));
    }

    // Teacher-only: includes answers + correct answers. Never exposed to students. Ownership-checked.
    @PostMapping("/activity-submissions/teacher-details")
    public ResponseEntity<ActivitySubmissionTeacherDetailsOutDTO> getTeacherSubmissionDetails(@AuthenticationPrincipal User user, @Valid @RequestBody SubmissionTargetInDTO body) {
        return ResponseEntity.status(200).body(activitySubmissionService.getTeacherSubmissionDetails(user, body));
    }
}
