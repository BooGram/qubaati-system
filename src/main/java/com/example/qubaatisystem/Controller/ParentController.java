package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Api.ApiResponse;
import com.example.qubaatisystem.DTO.In.ChildCreateInDTO;
import com.example.qubaatisystem.DTO.In.ChildTargetInDTO;
import com.example.qubaatisystem.DTO.In.ChildUpdateProfileInDTO;
import com.example.qubaatisystem.DTO.In.IdInDTO;
import com.example.qubaatisystem.DTO.In.ParentInDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    // Creating/listing parents is an admin operation (no public self-registration of parents).
    @PostMapping("/add")
    public ResponseEntity<?> create(@AuthenticationPrincipal User user, @Valid @RequestBody ParentInDTO dto) {
        return ResponseEntity.status(200).body(parentService.createForUser(user, dto));
    }

    @GetMapping("/get-all")
    public ResponseEntity<?> getAll(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(parentService.getAllForUser(user));
    }

    @PostMapping("/get")
    public ResponseEntity<?> getById(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        return ResponseEntity.status(200).body(parentService.getByIdForUser(user, dto));
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@AuthenticationPrincipal User user, @Valid @RequestBody ParentInDTO dto) {
        return ResponseEntity.status(200).body(parentService.updateForUser(user, dto));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@AuthenticationPrincipal User user, @Valid @RequestBody IdInDTO dto) {
        parentService.deleteForUser(user, dto);
        return ResponseEntity.status(200).body(new ApiResponse("Parent deleted successfully"));
    }

    // ---------- current-parent ("me") endpoints — parent derived from Basic Auth ----------

    @GetMapping("/me/dashboard")
    public ResponseEntity<?> getMyDashboard(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(parentService.getMyDashboard(user));
    }

    // A parent creates their own child account (the parent is derived from Basic Auth; no parentId in the body).
    @PostMapping("/me/children")
    public ResponseEntity<?> createMyChild(@AuthenticationPrincipal User user, @Valid @RequestBody ChildCreateInDTO dto) {
        return ResponseEntity.status(200).body(parentService.createMyChild(user, dto));
    }

    @GetMapping("/me/children")
    public ResponseEntity<?> getMyChildren(@AuthenticationPrincipal User user) {
        return ResponseEntity.status(200).body(parentService.getMyChildren(user));
    }

    @PostMapping("/me/children/overview")
    public ResponseEntity<?> getMyChildOverview(@AuthenticationPrincipal User user,
                                                @Valid @RequestBody ChildTargetInDTO dto) {
        return ResponseEntity.status(200).body(parentService.getMyChildOverview(user, dto));
    }

    // Combined learning profile: skills, learning style, activity performance, recent mission insight,
    // recommendations, and activity/mission completion for one child.
    @PostMapping("/me/children/learning-profile")
    public ResponseEntity<?> getMyChildLearningProfile(@AuthenticationPrincipal User user,
                                                       @Valid @RequestBody ChildTargetInDTO dto) {
        return ResponseEntity.status(200).body(parentService.getMyChildLearningProfile(user, dto));
    }

    @PatchMapping("/me/children/profile")
    public ResponseEntity<?> updateMyChildProfile(@AuthenticationPrincipal User user,
                                                  @Valid @RequestBody ChildUpdateProfileInDTO dto) {
        return ResponseEntity.status(200).body(parentService.updateMyChildProfile(user, dto));
    }

    @PostMapping("/me/children/activity-results")
    public ResponseEntity<?> getMyChildActivityResults(@AuthenticationPrincipal User user,
                                                       @Valid @RequestBody ChildTargetInDTO dto) {
        return ResponseEntity.status(200).body(parentService.getChildActivityResults(user, dto));
    }

    @PostMapping("/me/children/mission-history")
    public ResponseEntity<?> getMyChildMissionHistory(@AuthenticationPrincipal User user,
                                                      @Valid @RequestBody ChildTargetInDTO dto) {
        return ResponseEntity.status(200).body(parentService.getChildMissionHistory(user, dto));
    }

    // PDF export of a child's learning profile. studentId is a target in the body; the parent comes from Basic Auth
    // and must own the child (no parentId/studentId in the path). (updateChildProfile is already served by the
    // body-based PATCH /me/children/profile above; the merge's path-variable duplicate was removed.)
    @PostMapping("/me/children/learning-profile/pdf")
    public ResponseEntity<byte[]> exportMyChildLearningProfilePdf(@AuthenticationPrincipal User user,
                                                                  @Valid @RequestBody ChildTargetInDTO dto) {
        byte[] pdf = parentService.generateMyChildPortfolioPdf(user, dto);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"student-" + dto.getStudentId() + "-portfolio.pdf\"")
                .body(pdf);
    }
}
