package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    // Public — listing plans is harmless (also permitAll in SecurityConfig).
    @GetMapping("/plans")
    public ResponseEntity<?> getActivePlans() {
        return ResponseEntity.ok(subscriptionService.getActivePlans());
    }

    // Current-user subscription status — no profile id in the path.
    @GetMapping("/parents/me/status")
    public ResponseEntity<?> getMyParentStatus(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(subscriptionService.getMyParentStatus(user));
    }

    @GetMapping("/teachers/me/status")
    public ResponseEntity<?> getMyTeacherStatus(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(subscriptionService.getMyTeacherStatus(user));
    }
}
