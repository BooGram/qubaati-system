package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.Out.SubscriptionPlanOutDTO;
import com.example.qubaatisystem.DTO.Out.SubscriptionStatusOutDTO;
import com.example.qubaatisystem.Enum.PlanAudience;
import com.example.qubaatisystem.Enum.SubscriptionStatus;
import com.example.qubaatisystem.Model.Payment;
import com.example.qubaatisystem.Model.Subscription;
import com.example.qubaatisystem.Model.SubscriptionPlan;
import com.example.qubaatisystem.Repository.ClassroomRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.SubscriptionPlanRepository;
import com.example.qubaatisystem.Repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    // Free-plan child / classroom limits
    public static final int FREE_PARENT_CHILD_LIMIT = 2;
    public static final int FREE_TEACHER_CLASSROOM_LIMIT = 2;

    private final SubscriptionPlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final ModelMapper modelMapper;
    private final com.example.qubaatisystem.Config.SecurityOwnershipService security;

    // ── Plans ────────────────────────────────────────────────────────────────

    public List<SubscriptionPlanOutDTO> getActivePlans() {
        return planRepository.findByActiveTrue()
                .stream()
                .map(p -> modelMapper.map(p, SubscriptionPlanOutDTO.class))
                .toList();
    }

    public SubscriptionPlan requirePlan(String planCode) {
        return planRepository.findByCode(planCode)
                .orElseThrow(() -> new ApiException("Plan not found: " + planCode));
    }

    // ── Status ───────────────────────────────────────────────────────────────

    public SubscriptionStatusOutDTO getParentStatus(Integer parentId) {
        Optional<Subscription> sub = findActiveParentSubscription(parentId);
        return toStatusDTO(sub);
    }

    public SubscriptionStatusOutDTO getTeacherStatus(Integer teacherId) {
        Optional<Subscription> sub = findActiveTeacherSubscription(teacherId);
        return toStatusDTO(sub);
    }

    /** Current-user parent subscription status — derives the parent id from the caller. */
    public SubscriptionStatusOutDTO getMyParentStatus(com.example.qubaatisystem.Model.User user) {
        return getParentStatus(security.getCurrentParentId(user));
    }

    /** Current-user teacher subscription status — derives the teacher id from the caller. */
    public SubscriptionStatusOutDTO getMyTeacherStatus(com.example.qubaatisystem.Model.User user) {
        return getTeacherStatus(security.getCurrentTeacherId(user));
    }

    // ── Limit checks (called from StudentService and ClassroomService) ────────

    /**
     * Throws ApiException if the parent cannot add another child under their current plan.
     * Free plan: max 2 children. Paid plan: unlimited.
     */
    public void assertCanAddChild(Integer parentId) {
        if (parentId == null) return;
        boolean hasPaidPlan = findActiveParentSubscription(parentId)
                .map(s -> isPaidPlan(s.getPlan()))
                .orElse(false);
        if (!hasPaidPlan) {
            int count = studentRepository.countByParentId(parentId);
            if (count >= FREE_PARENT_CHILD_LIMIT) {
                throw new ApiException(
                        "Free plan allows up to " + FREE_PARENT_CHILD_LIMIT +
                        " children. Upgrade to Parent Plus to add more.");
            }
        }
    }

    /**
     * Throws ApiException if the teacher cannot create another classroom under their current plan.
     * Free plan: max 2 classrooms. Paid plan: unlimited.
     */
    public void assertCanCreateClassroom(Integer teacherId) {
        if (teacherId == null) return;
        boolean hasPaidPlan = findActiveTeacherSubscription(teacherId)
                .map(s -> isPaidPlan(s.getPlan()))
                .orElse(false);
        if (!hasPaidPlan) {
            int count = classroomRepository.countByTeacherId(teacherId);
            if (count >= FREE_TEACHER_CLASSROOM_LIMIT) {
                throw new ApiException(
                        "Free plan allows up to " + FREE_TEACHER_CLASSROOM_LIMIT +
                        " classrooms. Upgrade to Teacher Pro to create more.");
            }
        }
    }

    // ── Internal helpers ─────────────────────────────────────────────────────

    public Optional<Subscription> findActiveParentSubscription(Integer parentId) {
        return subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        parentId, SubscriptionStatus.ACTIVE, LocalDateTime.now());
    }

    public Optional<Subscription> findActiveTeacherSubscription(Integer teacherId) {
        return subscriptionRepository
                .findTopByTeacherIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        teacherId, SubscriptionStatus.ACTIVE, LocalDateTime.now());
    }

    private boolean isPaidPlan(SubscriptionPlan plan) {
        // A paid plan has a non-zero price
        return plan.getPriceAmount() != null && plan.getPriceAmount() > 0;
    }

    private SubscriptionStatusOutDTO toStatusDTO(Optional<Subscription> sub) {
        return sub.map(s -> new SubscriptionStatusOutDTO(
                        true,
                        s.getPlan().getCode(),
                        s.getPlan().getName(),
                        s.getStatus(),
                        s.getStartsAt(),
                        s.getEndsAt()))
                .orElse(new SubscriptionStatusOutDTO(false, null, null, null, null, null));
    }

    /**
     * Activates a new subscription or extends an existing active one.
     *
     * If the payment owner already has an active subscription:
     *   - Reuse that row (no new row created).
     *   - Extend endsAt by plan.durationDays from the later of now or the current endsAt,
     *     so a same-day renewal is never shorter than the remaining period.
     *
     * If no active subscription exists:
     *   - Create a new Subscription starting now.
     */
    public Subscription activateOrExtendSubscription(Payment payment) {
        Integer parentId  = payment.getParent()  != null ? payment.getParent().getId()  : null;
        Integer teacherId = payment.getTeacher() != null ? payment.getTeacher().getId() : null;

        Optional<Subscription> existing = (parentId != null)
                ? findActiveParentSubscription(parentId)
                : findActiveTeacherSubscription(teacherId);

        int durationDays = payment.getPlan().getDurationDays();
        LocalDateTime now = LocalDateTime.now();

        if (existing.isPresent()) {
            Subscription sub = existing.get();
            LocalDateTime extendFrom = sub.getEndsAt().isAfter(now) ? sub.getEndsAt() : now;
            sub.setEndsAt(extendFrom.plusDays(durationDays));
            return subscriptionRepository.save(sub);
        }

        Subscription subscription = new Subscription();
        subscription.setPlan(payment.getPlan());
        subscription.setParent(payment.getParent());
        subscription.setTeacher(payment.getTeacher());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartsAt(now);
        subscription.setEndsAt(now.plusDays(durationDays));
        subscription.setCreatedAt(now);
        return subscriptionRepository.save(subscription);
    }

    public Subscription save(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    public SubscriptionPlan findPlanByAudienceAndCode(PlanAudience audience, String planCode) {
        SubscriptionPlan plan = requirePlan(planCode);
        if (plan.getAudience() != audience) {
            throw new ApiException("Plan " + planCode + " does not match subscriber type.");
        }
        return plan;
    }
}
