package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Service.AiAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiAnalysisService aiAnalysisService;

    @PostMapping("/classrooms/{classroomId}/summary")
    public ResponseEntity<?> getClassroomSummary(@PathVariable Integer classroomId) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeClassroom(classroomId));
    }

    @PostMapping("/parents/{parentId}/children/{studentId}/summary")
    public ResponseEntity<?> getStudentSummary(@PathVariable Integer parentId,
                                               @PathVariable Integer studentId) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeStudent(parentId, studentId));
    }

    @PostMapping("/parents/{parentId}/dashboard-insight")
    public ResponseEntity<?> getFamilyDashboardInsight(@PathVariable Integer parentId) {
        return ResponseEntity.status(200).body(aiAnalysisService.analyzeFamilyInsight(parentId));
    }
}
