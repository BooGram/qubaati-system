package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.LearningStyleHistoryInDTO;
import com.example.qubaatisystem.Service.LearningStyleHistoryService;
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
@RequestMapping("/api/v1/learning-style-history")
@RequiredArgsConstructor
public class LearningStyleHistoryController {

    private final LearningStyleHistoryService learningStyleHistoryService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody LearningStyleHistoryInDTO dto) {
        learningStyleHistoryService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyleHistory created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(learningStyleHistoryService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(learningStyleHistoryService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody LearningStyleHistoryInDTO dto) {
        learningStyleHistoryService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyleHistory updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        learningStyleHistoryService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyleHistory deleted successfully"));
    }
}
