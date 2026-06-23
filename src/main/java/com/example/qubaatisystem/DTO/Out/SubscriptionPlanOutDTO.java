package com.example.qubaatisystem.DTO.Out;

import com.example.qubaatisystem.Enum.PlanAudience;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlanOutDTO {

    private Integer id;
    private String code;
    private String name;
    private PlanAudience audience;
    private Integer priceAmount;
    private String currency;
    private Integer durationDays;
    private Boolean active;
}
