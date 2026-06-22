package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.MissionSessionInDTO;
import com.example.qubaatisystem.Service.MissionSessionService;
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
@RequestMapping("/api/v1/mission-sessions")
@RequiredArgsConstructor
public class MissionSessionController {

    private final MissionSessionService missionSessionService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody MissionSessionInDTO dto) {
        missionSessionService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("MissionSession created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(missionSessionService.getAll());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(missionSessionService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody MissionSessionInDTO dto) {
        missionSessionService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("MissionSession updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        missionSessionService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("MissionSession deleted successfully"));
    }
}
