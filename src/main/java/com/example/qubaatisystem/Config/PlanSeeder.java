package com.example.qubaatisystem.Config;

import com.example.qubaatisystem.Enum.PlanAudience;
import com.example.qubaatisystem.Model.SubscriptionPlan;
import com.example.qubaatisystem.Repository.SubscriptionPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Seeds paid subscription plans on startup if not already present.
 * Free access is the default state — no free plan records are needed.
 * Seeding is idempotent: each plan is only inserted when its code is absent.
 */
@Component
@RequiredArgsConstructor
public class PlanSeeder implements CommandLineRunner {

    private final SubscriptionPlanRepository planRepository;

    @Override
    public void run(String... args) {
        seedPlan("PARENT_PLUS",  "Parent Plus",  PlanAudience.PARENT,  5000, "SAR", 30, true);
        seedPlan("TEACHER_PRO",  "Teacher Pro",  PlanAudience.TEACHER, 5000, "SAR", 30, true);
    }

    private void seedPlan(String code, String name, PlanAudience audience,
                          int priceAmount, String currency, int durationDays, boolean active) {
        if (planRepository.findByCode(code).isEmpty()) {
            SubscriptionPlan plan = new SubscriptionPlan();
            plan.setCode(code);
            plan.setName(name);
            plan.setAudience(audience);
            plan.setPriceAmount(priceAmount);
            plan.setCurrency(currency);
            plan.setDurationDays(durationDays);
            plan.setActive(active);
            planRepository.save(plan);
        }
    }
}
