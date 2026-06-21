package com.example.qubaatisystem.DTO.Out;

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
public class SubscriptionStatusOutDTO {

    private boolean hasActivePlan;
    private String planCode;
    private String planName;
    private SubscriptionStatus status;
    private LocalDateTime startsAt;
    private LocalDateTime endsAt;
}
