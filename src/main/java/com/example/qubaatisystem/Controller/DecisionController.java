package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.DecisionInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.Service.DecisionService;
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
@RequestMapping("/api/v1/decisions")
@RequiredArgsConstructor
public class DecisionController {

    private final DecisionService decisionService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody DecisionInDTO dto) {
        decisionService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Decision created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(decisionService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(decisionService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody DecisionInDTO dto) {
        decisionService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Decision updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        decisionService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Decision deleted successfully"));
    }
}
