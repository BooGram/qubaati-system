package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.TeacherInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardClassroomOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardStudentOutDTO;
import com.example.qubaatisystem.Enum.ActivityStatus;
import com.example.qubaatisystem.Service.ActivityService;
import com.example.qubaatisystem.Service.TeacherDashboardService;
import com.example.qubaatisystem.Service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody TeacherInDTO dto) {
        return ResponseEntity.status(200).body(teacherService.create(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(teacherService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(teacherService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody TeacherInDTO dto) {
        return ResponseEntity.status(200).body(teacherService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        teacherService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Teacher deleted successfully"));
    }

    @GetMapping("/{teacherId}/dashboard")
    public ResponseEntity<?> getDashboard(@PathVariable Integer teacherId) {
        return ResponseEntity.status(200).body(teacherService.getDashboard(teacherId));
    }

    // ---------- teacher ownership / visibility (Student 1) ----------

    // Optional status filter (enum). GET /teachers/{id}/activities?status=DRAFT etc.
    @GetMapping("/{teacherId}/activities")
    public ResponseEntity<List<ActivityOutDTO>> getTeacherActivities(
            @PathVariable Integer teacherId,
            @RequestParam(required = false) ActivityStatus status) {
        return ResponseEntity.status(200).body(activityService.getActivitiesByTeacher(teacherId, status));
    }

    @GetMapping("/{teacherId}/classrooms")
    public ResponseEntity<List<TeacherDashboardClassroomOutDTO>> getTeacherClassrooms(@PathVariable Integer teacherId) {
        return ResponseEntity.status(200).body(teacherDashboardService.getTeacherClassrooms(teacherId));
    }

    @GetMapping("/{teacherId}/students")
    public ResponseEntity<List<TeacherDashboardStudentOutDTO>> getTeacherStudents(@PathVariable Integer teacherId) {
        return ResponseEntity.status(200).body(teacherDashboardService.getTeacherStudents(teacherId));
    }
}
