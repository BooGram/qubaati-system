package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.AuditLogInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.Service.AuditLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody AuditLogInDTO dto) {
        auditLogService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("AuditLog created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(auditLogService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(auditLogService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody AuditLogInDTO dto) {
        auditLogService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("AuditLog updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        auditLogService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("AuditLog deleted successfully"));
    }
}
