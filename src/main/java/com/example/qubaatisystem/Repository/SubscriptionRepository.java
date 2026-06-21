package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Enum.SubscriptionStatus;
import com.example.qubaatisystem.Model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Integer> {

    // Returns the latest active subscription for a parent (farthest endsAt wins when multiple exist)
    Optional<Subscription> findTopByParentIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
            Integer parentId, SubscriptionStatus status, LocalDateTime now);

    // Returns the latest active subscription for a teacher
    Optional<Subscription> findTopByTeacherIdAndStatusAndEndsAtAfterOrderByEndsAtDesc(
            Integer teacherId, SubscriptionStatus status, LocalDateTime now);
}
