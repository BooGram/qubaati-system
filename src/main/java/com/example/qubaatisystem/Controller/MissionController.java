package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.GenerateMissionInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.MissionInDTO;
import com.example.qubaatisystem.DTO.In.StudentTargetInDTO;
import com.example.qubaatisystem.Service.MissionService;
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

    @PostMapping("/get")
    public ResponseEntity<?> getById(@Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(missionService.getById(dto.getId()));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody MissionInDTO dto) {
        missionService.update(dto.getId(), dto);
        return ResponseEntity.status(200).body(new ApiResponse("Mission updated successfully"));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@Valid @RequestBody IdInDTO dto) {
        missionService.delete(dto.getId());
        return ResponseEntity.status(200).body(new ApiResponse("Mission deleted successfully"));
    }

    @PostMapping("/available")
    public ResponseEntity<?> getAvailableMissions(@Valid @RequestBody StudentTargetInDTO dto) {
        return ResponseEntity.status(200).body(missionService.getAvailableMissions(dto.getStudentId()));
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateMissionForStudent(@Valid @RequestBody GenerateMissionInDTO dto) {
        return ResponseEntity.status(200)
                .body(missionService.generateMissionForStudent(dto.getStudentId(), dto.getWorldId()));
    }
}
