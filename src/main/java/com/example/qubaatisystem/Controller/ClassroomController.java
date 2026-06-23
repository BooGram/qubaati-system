package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.ClassroomInDTO;
import com.example.qubaatisystem.DTO.In.EnrollStudentInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ClassroomService;
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
@RequestMapping("/api/v1/classrooms")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classroomService;

    // The classroom owner is derived from Basic Auth: a TEACHER owns it (body teacherId ignored); an ADMIN may
    // supply teacherId to create on a teacher's behalf. The teacher free-plan limit then applies to that teacher.
    @PostMapping("/add")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @Valid @RequestBody ClassroomInDTO dto) {
        classroomService.create(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Classroom created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(classroomService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(classroomService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ClassroomInDTO dto) {
        classroomService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Classroom updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        classroomService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Classroom deleted successfully"));
    }

    // ---------- ENROLLMENT (body-based; teacher derived from Basic Auth, must own the classroom) ----------

    @PostMapping("/students/enroll")
    public ResponseEntity<?> enroll(@AuthenticationPrincipal User user, @Valid @RequestBody EnrollStudentInDTO dto) {
        classroomService.enrollStudent(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Student enrolled in classroom successfully"));
    }

    @PostMapping("/students/remove")
    public ResponseEntity<?> removeFromClassroom(@AuthenticationPrincipal User user, @Valid @RequestBody EnrollStudentInDTO dto) {
        classroomService.removeStudent(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Student removed from classroom successfully"));
    }

    @PostMapping("/students/list")
    public ResponseEntity<?> getStudents(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(classroomService.getStudents(user, dto));
    }

    @PostMapping("/dashboard")
    public ResponseEntity<?> getDashboard(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(classroomService.getDashboard(user, dto));
    }

    @PostMapping("/progress")
    public ResponseEntity<?> getProgress(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(classroomService.getProgress(user, dto));
    }
}
