package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.DTO.In.TeacherInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardClassroomOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardStudentOutDTO;
import com.example.qubaatisystem.Enum.ActivityStatus;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ActivityService;
import com.example.qubaatisystem.Service.StudentPortfolioPdfService;
import com.example.qubaatisystem.Service.TeacherDashboardService;
import com.example.qubaatisystem.Service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/teachers")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherService teacherService;
    private final TeacherDashboardService teacherDashboardService;
    private final ActivityService activityService;
    private final StudentPortfolioPdfService studentPortfolioPdfService;

    // Creating/listing teachers is an admin operation (no public self-registration of teachers).
    @PostMapping("/add")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @Valid @RequestBody TeacherInDTO dto) {
        return ResponseEntity.status(200).body(teacherService.create(user, dto));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(teacherService.getAll(user));
    }

    // ---------- current-teacher ("me") endpoints — no teacherId in the path ----------

    @GetMapping("/me/dashboard")
    public ResponseEntity<?> getMyDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(teacherService.getMyDashboard(user));
    }

    @GetMapping("/me/classrooms")
    public ResponseEntity<List<TeacherDashboardClassroomOutDTO>> getMyClassrooms(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(teacherDashboardService.getMyClassrooms(user));
    }

    @GetMapping("/me/students")
    public ResponseEntity<List<TeacherDashboardStudentOutDTO>> getMyStudents(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(teacherDashboardService.getMyStudents(user));
    }

    @GetMapping("/me/activities")
    public ResponseEntity<List<ActivityOutDTO>> getMyActivities(@AuthenticationPrincipal User user,
                                                                @RequestParam(required = false) ActivityStatus status) {
        return ResponseEntity.status(200).body(activityService.getMyActivities(user, status));
    }

    @GetMapping("/{teacherId}/students/{studentId}/learning-profile/pdf")
    public ResponseEntity<byte[]> exportStudentLearningProfilePdf(@PathVariable Integer teacherId,
                                                                  @PathVariable Integer studentId) {
        byte[] pdf = studentPortfolioPdfService.generateTeacherStudentPortfolio(teacherId, studentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"student-" + studentId + "-portfolio.pdf\"")
                .body(pdf);
    }
}
