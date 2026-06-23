package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ParentWeeklyReportService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Parent weekly report endpoints (Student 1 / parent area). Current-parent-or-admin on the "me"
 * routes; ADMIN-only on the cross-parent batch. Triggers the n8n integration and exposes the stored reports.
 */
@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentWeeklyReportController {

    private final ParentWeeklyReportService parentWeeklyReportService;

    // A single stored report by id — parent owner (checked against the report's parent) or admin.
    @PostMapping("/weekly-reports/get")
    public ResponseEntity<?> byId(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(parentWeeklyReportService.getReportById(user, dto));
    }

    // Manual batch trigger for ALL parents (continues past failures). triggerType = BATCH_MANUAL. ADMIN only.
    @PostMapping("/weekly-reports/generate-all")
    public ResponseEntity<?> generateAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(parentWeeklyReportService.generateAllForAdmin(user));
    }

    // ---------- current-parent ("me") endpoints — parent derived from Basic Auth ----------

    @PostMapping("/me/weekly-report/generate")
    public ResponseEntity<?> generateMine(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(parentWeeklyReportService.generateForCurrentParent(user));
    }

    @GetMapping("/me/weekly-reports")
    public ResponseEntity<?> listMine(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(parentWeeklyReportService.getReportsForCurrentParent(user));
    }

    @GetMapping("/me/weekly-reports/latest")
    public ResponseEntity<?> latestMine(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(parentWeeklyReportService.getLatestReportForCurrentParent(user));
    }
}
