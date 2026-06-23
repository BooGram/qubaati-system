package com.example.qubaatisystem.Controller;

import com.example.qubaatisystem.DTO.In.CheckoutInDTO;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@AuthenticationPrincipal User user, @RequestBody CheckoutInDTO dto) {
        return ResponseEntity.ok(paymentService.checkout(user, dto));
    }

    // Moyasar redirects here after payment; id is the Moyasar payment id.
    // localReference is read from metadata inside PaymentService — not from query params.
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam String id) {
        paymentService.handleCallback(id);
        return ResponseEntity.ok("Payment processed");
    }

    @GetMapping("/status")
    public ResponseEntity<?> status(@AuthenticationPrincipal User user, @RequestParam String ref) {
        return ResponseEntity.ok(paymentService.getStatus(user, ref));
    }

    @GetMapping("/receipt")
    public ResponseEntity<?> receipt(@AuthenticationPrincipal User user, @RequestParam String ref) {
        return ResponseEntity.ok(paymentService.getReceipt(user, ref));
    }
}
