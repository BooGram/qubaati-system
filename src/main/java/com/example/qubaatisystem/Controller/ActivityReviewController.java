package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.ActivityReviewInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.Service.ActivityReviewService;
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
@RequestMapping("/api/v1/activity-reviews")
@RequiredArgsConstructor
public class ActivityReviewController {

    private final ActivityReviewService activityReviewService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody ActivityReviewInDTO dto) {
        activityReviewService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("ActivityReview created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(activityReviewService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(activityReviewService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ActivityReviewInDTO dto) {
        activityReviewService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("ActivityReview updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        activityReviewService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("ActivityReview deleted successfully"));
    }
}
