package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.ChildCreateInDTO;
import com.example.qubaatisystem.DTO.In.ChildUpdateProfileInDTO;
import com.example.qubaatisystem.DTO.In.ParentInDTO;
import com.example.qubaatisystem.Service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ParentInDTO dto) {
        return ResponseEntity.status(200).body(parentService.create(dto));
    }

    @GetMapping
    public ResponseEntity<?> getAll() {
        return ResponseEntity.status(200).body(parentService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(parentService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @Valid @RequestBody ParentInDTO dto) {
        return ResponseEntity.status(200).body(parentService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        parentService.delete(id);
        return ResponseEntity.status(200).body(new ApiResponse("Parent deleted successfully"));
    }

    @GetMapping("/{parentId}/dashboard")
    public ResponseEntity<?> getDashboard(@PathVariable Integer parentId) {
        return ResponseEntity.status(200).body(parentService.getDashboard(parentId));
    }

    @PostMapping("/{parentId}/children")
    public ResponseEntity<?> createChild(@PathVariable Integer parentId,
                                         @Valid @RequestBody ChildCreateInDTO dto) {
        return ResponseEntity.status(200).body(parentService.createChild(parentId, dto));
    }

    @GetMapping("/{parentId}/children")
    public ResponseEntity<?> getChildren(@PathVariable Integer parentId) {
        return ResponseEntity.status(200).body(parentService.getChildren(parentId));
    }

    @GetMapping("/{parentId}/children/{studentId}/overview")
    public ResponseEntity<?> getChildOverview(@PathVariable Integer parentId,
                                              @PathVariable Integer studentId) {
        return ResponseEntity.status(200).body(parentService.getChildOverview(parentId, studentId));
    }

    // Combined learning profile: skills, learning style, activity performance, recent mission insight,
    // recommendations, and activity/mission completion for one child.
    @GetMapping("/{parentId}/children/{studentId}/learning-profile")
    public ResponseEntity<?> getChildLearningProfile(@PathVariable Integer parentId,
                                                     @PathVariable Integer studentId) {
        return ResponseEntity.status(200).body(parentService.getChildLearningProfile(parentId, studentId));
    }

    @PatchMapping("/{parentId}/children/{studentId}/profile")
    public ResponseEntity<?> updateChildProfile(@PathVariable Integer parentId,
                                                @PathVariable Integer studentId,
                                                @Valid @RequestBody ChildUpdateProfileInDTO dto) {
        return ResponseEntity.status(200).body(parentService.updateChildProfile(parentId, studentId, dto));
    }
}
