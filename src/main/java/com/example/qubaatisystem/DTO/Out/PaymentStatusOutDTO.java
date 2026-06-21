package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusOutDTO {

    private String localReference;
    private PaymentStatus status;
    private String planName;
    private Integer amount;
    private String currency;
    private LocalDateTime paidAt;
    private String failureMessage;
}
