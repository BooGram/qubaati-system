package com.example.qubaatisystem.DTO.In;

import com.example.qubaatisystem.Enum.PlanAudience;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutInDTO {

    @JsonProperty("planCode")
    private String planCode;

    // PARENT or TEACHER — reuses the existing PlanAudience enum
    @JsonProperty("subscriberType")
    private PlanAudience subscriberType;

    // ID of the parent or teacher initiating the checkout
    @JsonProperty("subscriberId")
    private Integer subscriberId;
}
