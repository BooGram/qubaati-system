package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.MissionSessionInDTO;
import com.example.qubaatisystem.Service.MissionSessionService;
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
@RequestMapping("/api/v1/mission-sessions")
@RequiredArgsConstructor
public class MissionSessionController {

    private final MissionSessionService missionSessionService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody MissionSessionInDTO dto) {
        missionSessionService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("MissionSession created successfully"));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(missionSessionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(missionSessionService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody MissionSessionInDTO dto) {
        missionSessionService.update(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("MissionSession updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        missionSessionService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("MissionSession deleted successfully"));
    }
}
