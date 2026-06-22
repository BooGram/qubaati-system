package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.CareerWorldInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.Service.CareerWorldService;
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
@RequestMapping("/api/v1/career-worlds")
@RequiredArgsConstructor
public class CareerWorldController {

    private final CareerWorldService careerWorldService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody CareerWorldInDTO dto) {
        careerWorldService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("CareerWorld created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(careerWorldService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(careerWorldService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody CareerWorldInDTO dto) {
        careerWorldService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("CareerWorld updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        careerWorldService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("CareerWorld deleted successfully"));
    }
}
