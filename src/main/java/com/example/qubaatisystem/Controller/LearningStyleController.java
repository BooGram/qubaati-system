package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.LearningStyleInDTO;
import com.example.qubaatisystem.Service.LearningStyleService;
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
@RequestMapping("/api/v1/learning-styles")
@RequiredArgsConstructor
public class LearningStyleController {

    private final LearningStyleService learningStyleService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody LearningStyleInDTO dto) {
        learningStyleService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyle created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(learningStyleService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(learningStyleService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody LearningStyleInDTO dto) {
        learningStyleService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyle updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        learningStyleService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyle deleted successfully"));
    }
}
