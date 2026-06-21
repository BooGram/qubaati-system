package com.example.qubaatisystem.Model;

import com.example.qubaatisystem.Enum.PlanAudience;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "subscription_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Unique plan identifier used throughout the system (e.g., "PARENT_PLUS")
    @Column(nullable = false, unique = true, length = 50)
    private String code;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private PlanAudience audience;

    // Price in halalas (Saudi smallest currency unit; 100 halalas = 1 SAR)
    @Column(nullable = false)
    private Integer priceAmount;

    @Column(nullable = false, length = 10)
    private String currency;

    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private Boolean active;
}
