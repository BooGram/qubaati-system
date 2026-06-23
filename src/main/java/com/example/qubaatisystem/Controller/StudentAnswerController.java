package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.BatchStudentAnswerInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.StudentAnswerInDTO;
import com.example.qubaatisystem.DTO.In.StudentAnswerManualGradeInDTO;
import com.example.qubaatisystem.DTO.Out.ActivitySubmissionOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentAnswerOutDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ActivitySubmissionService;
import com.example.qubaatisystem.Service.StudentAnswerService;
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
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class StudentAnswerController {

    private final StudentAnswerService studentAnswerService;
    private final ActivitySubmissionService activitySubmissionService;

    // ---------- CRUD ----------

    @PostMapping("/student-answers/add")
    public ResponseEntity<?> create(@Valid @RequestBody StudentAnswerInDTO dto) {
        studentAnswerService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("StudentAnswer created successfully"));
    }

    @GetMapping("/student-answers/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(studentAnswerService.getAll());
    }

    @PostMapping("/student-answers/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(studentAnswerService.getById(dto.getId()));
    }

    @PutMapping("/student-answers/update")
    public ResponseEntity<?> update(@Valid @RequestBody StudentAnswerInDTO dto) {
        studentAnswerService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("StudentAnswer updated successfully"));
    }

    @DeleteMapping("/student-answers/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        studentAnswerService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("StudentAnswer deleted successfully"));
    }

    // ---------- FLOW: BATCH ANSWERS ----------

    // Body-based batch save: submissionId is a target in the body. The student may only answer their OWN submission.
    @PostMapping("/activity-submissions/answers/batch")
    public ResponseEntity<List<StudentAnswerOutDTO>> saveBatchAnswers(@AuthenticationPrincipal User user,
                                                                      @Valid @RequestBody BatchStudentAnswerInDTO dto) {
        return ResponseEntity.status(200).body(studentAnswerService.saveBatchAnswers(user, dto));
    }

    // ---------- TEACHER MANUAL GRADING / SCORE OVERRIDE ----------

    // Returns the full submission because the manual grade recalculates the submission score.
    @PatchMapping("/student-answers/grade")
    public ResponseEntity<ActivitySubmissionOutDTO> manualGradeAnswer(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody StudentAnswerManualGradeInDTO request) {
        return ResponseEntity.status(200).body(activitySubmissionService.manualGradeAnswer(user, request));
    }
}
