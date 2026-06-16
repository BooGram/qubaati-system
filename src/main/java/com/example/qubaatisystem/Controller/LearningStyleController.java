package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.LearningStyleInDTO;
import com.example.qubaatisystem.Service.LearningStyleService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/learning-styles")
@RequiredArgsConstructor
public class LearningStyleController {

    private final LearningStyleService learningStyleService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody LearningStyleInDTO dto) {
        learningStyleService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyle created successfully"));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(learningStyleService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(learningStyleService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody LearningStyleInDTO dto) {
        learningStyleService.update(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyle updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        learningStyleService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("LearningStyle deleted successfully"));
    }
}
