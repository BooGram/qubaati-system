package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.CareerWorldInDTO;
import com.example.qubaatisystem.Service.CareerWorldService;
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
@RequestMapping("/api/v1/career-worlds")
@RequiredArgsConstructor
public class CareerWorldController {

    private final CareerWorldService careerWorldService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CareerWorldInDTO dto) {
        careerWorldService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("CareerWorld created successfully"));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(careerWorldService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(careerWorldService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody CareerWorldInDTO dto) {
        careerWorldService.update(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("CareerWorld updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        careerWorldService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("CareerWorld deleted successfully"));
    }
}
