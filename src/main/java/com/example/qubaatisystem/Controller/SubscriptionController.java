package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Security.SecurityOwnershipService;
import com.example.qubaatisystem.Service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;
    private final SecurityOwnershipService security;

    // Public — listing plans is harmless (also permitAll in SecurityConfig).
    @GetMapping("/plans")
    public ResponseEntity<?> getActivePlans() {
        return ResponseEntity.ok(subscriptionService.getActivePlans());
    }

    @GetMapping("/parents/{parentId}/status")
    public ResponseEntity<?> getParentStatus(@PathVariable Integer parentId) {
        security.assertCurrentParentOrAdmin(parentId);
        return ResponseEntity.ok(subscriptionService.getParentStatus(parentId));
    }

    @GetMapping("/teachers/{teacherId}/status")
    public ResponseEntity<?> getTeacherStatus(@PathVariable Integer teacherId) {
        security.assertCurrentTeacherOrAdmin(teacherId);
        return ResponseEntity.ok(subscriptionService.getTeacherStatus(teacherId));
    }
}
