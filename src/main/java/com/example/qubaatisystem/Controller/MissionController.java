package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.MissionInDTO;
import com.example.qubaatisystem.Service.MissionService;
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
@RequestMapping("/api/v1/mission")
@RequiredArgsConstructor
public class MissionController {

    private final MissionService missionService;

    @PostMapping("/add")
    public ResponseEntity<?> create(@Valid @RequestBody MissionInDTO dto) {
        missionService.create(dto);
        return ResponseEntity.status(200).body(new ApiResponse("Mission created successfully"));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(missionService.getAll());
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(missionService.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody MissionInDTO dto) {
        missionService.update(id, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Mission updated successfully"));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        missionService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Mission deleted successfully"));
    }

    @GetMapping("/available/{studentId}")
    public ResponseEntity<?> getAvailableMissions(@PathVariable Integer studentId) {
        return ResponseEntity.status(200).body(missionService.getAvailableMissions(studentId));
    }

    @PostMapping("/generate/{studentId}/{worldId}")
    public ResponseEntity<?> generateMissionForStudent(@PathVariable Integer studentId, @PathVariable Integer worldId) {
        return ResponseEntity.status(200).body(missionService.generateMissionForStudent(studentId, worldId));
    }
}
