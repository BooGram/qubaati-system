package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Enum.NotificationStatus;
import com.example.qubaatisystem.Model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Notification findNotificationById(Integer id);

    List<Notification> findNotificationsByRecipientId(Integer recipientId);

    List<Notification> findNotificationsByRecipientIdAndStatus(Integer recipientId, NotificationStatus status);
}
