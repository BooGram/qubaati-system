package com.example.qubaatisystem;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.Out.SubscriptionStatusOutDTO;
import com.example.qubaatisystem.Enum.PlanAudience;
import com.example.qubaatisystem.Enum.SubscriptionStatus;
import com.example.qubaatisystem.Model.Parent;
import com.example.qubaatisystem.Model.Payment;
import com.example.qubaatisystem.Model.Subscription;
import com.example.qubaatisystem.Model.SubscriptionPlan;
import com.example.qubaatisystem.Repository.ClassroomRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.SubscriptionPlanRepository;
import com.example.qubaatisystem.Repository.SubscriptionRepository;
import com.example.qubaatisystem.Service.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock SubscriptionPlanRepository planRepository;
    @Mock SubscriptionRepository subscriptionRepository;
    @Mock StudentRepository studentRepository;
    @Mock ClassroomRepository classroomRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks SubscriptionService subscriptionService;

    // ── getParentStatus ───────────────────────────────────────────────────────

    @Test
    void getParentStatus_returnsHasActivePlanFalse_whenNoSubscriptionFound() {
        when(subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(1), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        SubscriptionStatusOutDTO status = subscriptionService.getParentStatus(1);

        assertThat(status.isHasActivePlan()).isFalse();
        assertThat(status.getPlanCode()).isNull();
    }

    @Test
    void getParentStatus_returnsHasActivePlanTrue_whenActiveSubscriptionExists() {
        SubscriptionPlan plan = makePlan("PARENT_PLUS", PlanAudience.PARENT, 5000);
        Subscription sub = makeSubscription(plan);
        when(subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(1), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.of(sub));

        SubscriptionStatusOutDTO status = subscriptionService.getParentStatus(1);

        assertThat(status.isHasActivePlan()).isTrue();
        assertThat(status.getPlanCode()).isEqualTo("PARENT_PLUS");
    }

    // ── assertCanAddChild — parent child limit ────────────────────────────────

    @Test
    void assertCanAddChild_doesNothing_whenParentIdIsNull() {
        subscriptionService.assertCanAddChild(null);
        // no repository calls expected — completes without exception
    }

    @Test
    void assertCanAddChild_throwsApiException_whenFreePlanAndAtChildLimit() {
        when(subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(1), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(studentRepository.countByParentId(1)).thenReturn(2);

        assertThatThrownBy(() -> subscriptionService.assertCanAddChild(1))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Free plan");
    }

    @Test
    void assertCanAddChild_doesNotThrow_whenActivePaidPlanExists() {
        // Active paid subscription bypasses the free-plan child limit entirely
        SubscriptionPlan plan = makePlan("PARENT_PLUS", PlanAudience.PARENT, 5000);
        Subscription sub = makeSubscription(plan);
        when(subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(1), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.of(sub));

        // Should not throw even though count would exceed the free limit
        subscriptionService.assertCanAddChild(1);
    }

    @Test
    void assertCanAddChild_throwsApiException_whenExpiredSubscriptionAndAtChildLimit() {
        // An expired subscription is not returned by the derived query (endsAt must be after now).
        // The service therefore treats the parent as on the free tier and enforces the limit.
        when(subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(1), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());  // expired — query returns nothing
        when(studentRepository.countByParentId(1)).thenReturn(2);

        assertThatThrownBy(() -> subscriptionService.assertCanAddChild(1))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Free plan");
    }

    // ── assertCanCreateClassroom — teacher classroom limit ────────────────────

    @Test
    void assertCanCreateClassroom_throwsApiException_whenFreePlanAndAtClassroomLimit() {
        when(subscriptionRepository
                .findTopByTeacherIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(5), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(classroomRepository.countByTeacherId(5)).thenReturn(2);

        assertThatThrownBy(() -> subscriptionService.assertCanCreateClassroom(5))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Free plan");
    }

    @Test
    void assertCanCreateClassroom_doesNotThrow_whenActivePaidPlanExists() {
        SubscriptionPlan plan = makePlan("TEACHER_PRO", PlanAudience.TEACHER, 5000);
        Subscription sub = makeSubscription(plan);
        when(subscriptionRepository
                .findTopByTeacherIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(5), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.of(sub));

        subscriptionService.assertCanCreateClassroom(5);
        // No exception thrown — paid plan has no classroom limit
    }

    @Test
    void assertCanCreateClassroom_throwsApiException_whenExpiredSubscriptionAndAtClassroomLimit() {
        // Expired subscription is invisible to the query — teacher falls back to free-tier limits
        when(subscriptionRepository
                .findTopByTeacherIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(5), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(classroomRepository.countByTeacherId(5)).thenReturn(2);

        assertThatThrownBy(() -> subscriptionService.assertCanCreateClassroom(5))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Free plan");
    }

    // ── activateOrExtendSubscription ──────────────────────────────────────────

    @Test
    void activateOrExtendSubscription_createsNewSubscription_whenNoActiveSubscriptionExists() {
        SubscriptionPlan plan = makePlan("PARENT_PLUS", PlanAudience.PARENT, 5000);
        Payment payment = makePayment(plan, 1, null);

        when(subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(1), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());
        when(subscriptionRepository.save(any(Subscription.class))).thenAnswer(inv -> inv.getArgument(0));

        Subscription result = subscriptionService.activateOrExtendSubscription(payment);

        assertThat(result.getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);
        assertThat(result.getEndsAt()).isAfter(LocalDateTime.now());
        verify(subscriptionRepository).save(any(Subscription.class));
    }

    @Test
    void activateOrExtendSubscription_extendsExistingSubscription_insteadOfCreatingNewRow() {
        SubscriptionPlan plan = makePlan("PARENT_PLUS", PlanAudience.PARENT, 5000);
        Payment payment = makePayment(plan, 1, null);

        LocalDateTime originalEndsAt = LocalDateTime.now().plusDays(15);  // 15 days remaining
        Subscription existing = makeSubscription(plan);
        existing.setEndsAt(originalEndsAt);

        when(subscriptionRepository
                .findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
                        eq(1), eq(SubscriptionStatus.ACTIVE), any(LocalDateTime.class)))
                .thenReturn(Optional.of(existing));
        when(subscriptionRepository.save(existing)).thenReturn(existing);

        Subscription result = subscriptionService.activateOrExtendSubscription(payment);

        // Must reuse the same row (same object reference), not create a new one
        assertThat(result).isSameAs(existing);
        // endsAt must be extended from originalEndsAt, not from now
        assertThat(result.getEndsAt()).isAfter(originalEndsAt);
        assertThat(result.getEndsAt()).isEqualToIgnoringNanos(originalEndsAt.plusDays(plan.getDurationDays()));
        verify(subscriptionRepository).save(existing);
    }

    // ── requirePlan ───────────────────────────────────────────────────────────

    @Test
    void requirePlan_throwsApiException_whenPlanCodeNotFound() {
        when(planRepository.findByCode("NONEXISTENT")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> subscriptionService.requirePlan("NONEXISTENT"))
                .isInstanceOf(ApiException.class)
                .hasMessageContaining("Plan not found");
    }

    @Test
    void requirePlan_returnsPlan_whenCodeExists() {
        SubscriptionPlan plan = makePlan("PARENT_PLUS", PlanAudience.PARENT, 5000);
        when(planRepository.findByCode("PARENT_PLUS")).thenReturn(Optional.of(plan));

        SubscriptionPlan result = subscriptionService.requirePlan("PARENT_PLUS");

        assertThat(result.getCode()).isEqualTo("PARENT_PLUS");
        assertThat(result.getPriceAmount()).isEqualTo(5000);
    }

    @Test
    void save_persistsAndReturnsSubscription() {
        Subscription sub = makeSubscription(makePlan("PARENT_PLUS", PlanAudience.PARENT, 5000));
        when(subscriptionRepository.save(sub)).thenReturn(sub);

        Subscription saved = subscriptionService.save(sub);

        verify(subscriptionRepository).save(sub);
        assertThat(saved).isSameAs(sub);
    }

    // ---------- helpers ----------

    private Payment makePayment(SubscriptionPlan plan, Integer parentId, Integer teacherId) {
        Payment payment = new Payment();
        payment.setPlan(plan);
        if (parentId != null) {
            Parent parent = new Parent();
            parent.setId(parentId);
            payment.setParent(parent);
        }
        payment.setAmount(plan.getPriceAmount());
        payment.setCurrency(plan.getCurrency());
        return payment;
    }

    private SubscriptionPlan makePlan(String code, PlanAudience audience, int price) {
        SubscriptionPlan plan = new SubscriptionPlan();
        plan.setCode(code);
        plan.setName(code);
        plan.setAudience(audience);
        plan.setPriceAmount(price);
        plan.setCurrency("SAR");
        plan.setDurationDays(30);
        plan.setActive(true);
        return plan;
    }

    private Subscription makeSubscription(SubscriptionPlan plan) {
        Subscription sub = new Subscription();
        sub.setPlan(plan);
        sub.setStatus(SubscriptionStatus.ACTIVE);
        sub.setStartsAt(LocalDateTime.now().minusDays(1));
        sub.setEndsAt(LocalDateTime.now().plusDays(29));
        sub.setCreatedAt(LocalDateTime.now().minusDays(1));
        return sub;
    }
}
