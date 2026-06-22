package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.ActivityAssignmentBulkInDTO;
import com.example.qubaatisystem.DTO.In.ActivityAssignmentDeadlineInDTO;
import com.example.qubaatisystem.DTO.In.ActivityAssignmentInDTO;
import com.example.qubaatisystem.DTO.In.AssignClassroomInDTO;
import com.example.qubaatisystem.DTO.In.AssignStudentInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.StartAssignmentInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityAssignmentOutDTO;
import com.example.qubaatisystem.DTO.Out.DueSoonNotificationsOutDTO;
import com.example.qubaatisystem.DTO.Out.ExpireOverdueOutDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ActivityAssignmentService;
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

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ActivityAssignmentController {

    private final ActivityAssignmentService activityAssignmentService;

    // ---------- CRUD ----------

    // Generic assignment create (used for date-controlled seeding). The assigner is derived from Basic Auth; a
    // teacher may only create for their own activity + a student in their classroom.
    @PostMapping("/activity-assignments/add")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @Valid @RequestBody ActivityAssignmentInDTO dto) {
        activityAssignmentService.create(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("ActivityAssignment created successfully"));
    }

    @GetMapping("/activity-assignments/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(activityAssignmentService.getAll());
    }

    @PostMapping("/activity-assignments/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activityAssignmentService.getById(dto.getId()));
    }

    @PutMapping("/activity-assignments/update")
    public ResponseEntity<?> update(@Valid @RequestBody ActivityAssignmentInDTO dto) {
        activityAssignmentService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("ActivityAssignment updated successfully"));
    }

    @DeleteMapping("/activity-assignments/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        activityAssignmentService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("ActivityAssignment deleted successfully"));
    }

    // ---------- FLOW: ASSIGNMENT ----------

    // Body-based teacher "assign activity to student" — activityId/studentId are targets in the body; the
    // assigning teacher is derived from Basic Auth. No path actor/target ids.
    @PostMapping("/activity-assignments/assign-student")
    public ResponseEntity<ActivityAssignmentOutDTO> assignStudent(@AuthenticationPrincipal User user, @Valid @RequestBody AssignStudentInDTO body) {
        return ResponseEntity.status(200).body(activityAssignmentService.assignStudent(user, body));
    }

    // Body-based teacher "assign activity to classroom" — activityId/classroomId are targets in the body; the
    // assigning teacher is derived from Basic Auth. A teacher may assign only their own activity to their own classroom.
    @PostMapping("/activity-assignments/assign-classroom")
    public ResponseEntity<ActivityAssignmentOutDTO> assignClassroom(@AuthenticationPrincipal User user, @Valid @RequestBody AssignClassroomInDTO body) {
        return ResponseEntity.status(200).body(activityAssignmentService.assignClassroom(user, body));
    }

    // Body-based bulk assign — activityId is a target in the body; the assigning teacher is derived from Basic Auth.
    @PostMapping("/activity-assignments/assign-bulk-students")
    public ResponseEntity<ApiResponse> assignToBulkStudents(@AuthenticationPrincipal User user, @Valid @RequestBody ActivityAssignmentBulkInDTO dto) {
        return ResponseEntity.status(200).body(activityAssignmentService.assignToBulkStudents(user, dto));
    }

    // Body-based: list all assignments for an activity. activityId is a target in the body. This endpoint left the
    // /activities/** (TEACHER) prefix, so the teacher-owns-activity guard is enforced here in the controller.
    @PostMapping("/activity-assignments/by-activity")
    public ResponseEntity<List<ActivityAssignmentOutDTO>> getAssignmentsByActivity(
            @AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activityAssignmentService.getAssignmentsByActivity(user, dto));
    }

    // Current student's own assignments — no studentId in the path.
    @GetMapping("/students/me/activity-assignments")
    public ResponseEntity<List<ActivityAssignmentOutDTO>> getMyAssignments(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(activityAssignmentService.getMyAssignments(user));
    }

    // Body-based cancel — assignmentId is a target in the body (reuses StartAssignmentInDTO{assignmentId}). Teacher
    // guard added here because the endpoint no longer sits under a role-protected path prefix.
    @PatchMapping("/activity-assignments/cancel")
    public ResponseEntity<ApiResponse> cancelAssignment(@AuthenticationPrincipal User user,
                                                         @Valid @RequestBody StartAssignmentInDTO body) {
        activityAssignmentService.cancelAssignment(user, body);
        return ResponseEntity.status(200).body(new ApiResponse("ActivityAssignment cancelled successfully"));
    }

    // Body-based extend-deadline — assignmentId is a target in the body.
    @PatchMapping("/activity-assignments/extend-deadline")
    public ResponseEntity<ApiResponse> extendDeadline(@AuthenticationPrincipal User user,
                                                      @Valid @RequestBody ActivityAssignmentDeadlineInDTO dto) {
        activityAssignmentService.extendDeadline(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("ActivityAssignment deadline extended successfully"));
    }

    // ---------- DUE-SOON / OVERDUE AUTOMATION (system batch ops; teacher/admin only) ----------

    @PatchMapping("/activity-assignments/expire-overdue")
    public ResponseEntity<ExpireOverdueOutDTO> expireOverdue(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(activityAssignmentService.expireOverdueAssignments(user));
    }

    // hours is a small numeric filter (default 24), not free text.
    @PostMapping("/activity-assignments/due-soon-notifications")
    public ResponseEntity<DueSoonNotificationsOutDTO> sendDueSoonNotifications(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "24") Integer hours) {
        return ResponseEntity.status(200).body(activityAssignmentService.sendDueSoonNotifications(user, hours));
    }
}
