package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.PaymentStatus;
import com.example.qubaatisystem.Enum.PlanAudience;
import com.example.qubaatisystem.Enum.SubscriptionStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentReceiptOutDTO {

    private String title;
    private String receiptReference;
    private PaymentStatus paymentStatus;
    private String planCode;
    private String planName;
    private Integer amountHalalas;
    private String amountSar;
    private String currency;
    private LocalDateTime paidAt;
    private String moyasarPaymentId;
    private PlanAudience subscriberType;
    private Integer subscriberId;
    private SubscriptionStatus subscriptionStatus;
    private LocalDateTime subscriptionStartsAt;
    private LocalDateTime subscriptionEndsAt;
}
