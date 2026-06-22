package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.StudentInDTO;
import com.example.qubaatisystem.DTO.Out.StudentActivityDashboardOutDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ActivityDashboardService;
import com.example.qubaatisystem.Service.StudentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final ActivityDashboardService activityDashboardService;

    // ---------- CRUD ----------

    // Students are created by an admin (or by a parent via POST /parents/{parentId}/children).
    @PostMapping("/add")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @Valid @RequestBody StudentInDTO dto) {
        return ResponseEntity.status(200).body(studentService.create(user, dto));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(studentService.getAll(user));
    }

    // Current student's own profile — no studentId in the path.
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(studentService.getMe(user));
    }

    // Generic student update/delete by id are admin-only (a student edits nothing here; a parent manages a child's
    // profile via /parents/me/children/profile, and a student reads their own data via the /me endpoints).
    @PutMapping("/update")
    public ResponseEntity<?> update(@AuthenticationPrincipal User user, @Valid @RequestBody StudentInDTO dto) {
        return ResponseEntity.status(200).body(studentService.update(user, dto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        studentService.delete(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Student deleted successfully"));
    }

    // ---------- AVAILABILITY / HISTORY ----------

    // Admin-only "view any student" variant (students use GET /students/me/career-worlds/available instead, which
    // derives the student from Basic Auth — so a student can never read another student's data through here).
    @PostMapping("/career-worlds/available")
    public ResponseEntity<?> getAvailableCareerWorlds(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(studentService.getAvailableCareerWorlds(user, dto));
    }

    @GetMapping("/me/skills/history")
    public ResponseEntity<?> getMySkillHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(studentService.getMySkillHistory(user));
    }

    @GetMapping("/me/learning-style/history")
    public ResponseEntity<?> getMyLearningStyleHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(studentService.getMyLearningStyleHistory(user));
    }

    // Admin-only "view any student" variant (students use GET /students/me/learning-style/history).
    @PostMapping("/learning-style/history")
    public ResponseEntity<?> getLearningStyleHistory(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(studentService.getLearningStyleHistory(user, dto));
    }

    // ---------- ACTIVITY DASHBOARD (Student 2) ----------

    // Admin-only "view any student" variant (students use GET /students/me/activity-dashboard).
    @PostMapping("/activity-dashboard")
    public ResponseEntity<StudentActivityDashboardOutDTO> getActivityDashboard(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activityDashboardService.getActivityDashboard(user, dto));
    }

    // ---------- current-student ("me") endpoints — no studentId in the path ----------

    @GetMapping("/me/activity-dashboard")
    public ResponseEntity<StudentActivityDashboardOutDTO> getMyActivityDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(activityDashboardService.getMyActivityDashboard(user));
    }

    @GetMapping("/me/career-worlds/available")
    public ResponseEntity<?> getMyAvailableCareerWorlds(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(studentService.getMyAvailableCareerWorlds(user));
    }
}
