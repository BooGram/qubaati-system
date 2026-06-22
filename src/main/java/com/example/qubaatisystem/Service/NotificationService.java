package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.NotificationInDTO;
import com.example.qubaatisystem.DTO.Out.NotificationOutDTO;
import com.example.qubaatisystem.Enum.NotificationStatus;
import com.example.qubaatisystem.Enum.NotificationType;
import com.example.qubaatisystem.Model.Notification;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Repository.NotificationRepository;
import com.example.qubaatisystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final com.example.qubaatisystem.Config.SecurityOwnershipService security;

    public List<NotificationOutDTO> getAll() {
        return notificationRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public NotificationOutDTO getById(Integer id) {
        Notification notification = notificationRepository.findNotificationById(id);
        if (notification == null) {
            throw new ApiException("Notification with id " + id + " not found");
        }
        return toOut(notification);
    }

    public void create(NotificationInDTO dto) {
        Notification notification = modelMapper.map(dto, Notification.class);

        applyRelationships(notification, dto);

        notification.setId(null);
        notificationRepository.save(notification);
    }

    public void update(Integer id, NotificationInDTO dto) {
        Notification notification = notificationRepository.findNotificationById(id);
        if (notification == null) {
            throw new ApiException("Notification with id " + id + " not found");
        }

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        notification.setRecipient(null);
        modelMapper.map(dto, notification);
        notification.setId(id);

        applyRelationships(notification, dto);

        notificationRepository.save(notification);
    }

    public void delete(Integer id) {
        Notification notification = notificationRepository.findNotificationById(id);
        if (notification == null) {
            throw new ApiException("Notification with id " + id + " not found");
        }
        notificationRepository.delete(notification);
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(Notification notification, NotificationInDTO dto) {
        User recipient = userRepository.findUserById(dto.getRecipientId());
        if (recipient == null) {
            throw new ApiException("User with id " + dto.getRecipientId() + " not found");
        }
        notification.setRecipient(recipient);
    }

    private NotificationOutDTO toOut(Notification notification) {
        NotificationOutDTO out = modelMapper.map(notification, NotificationOutDTO.class);
        if (notification.getRecipient() != null) {
            out.setRecipientId(notification.getRecipient().getId());
            out.setRecipientUsername(notification.getRecipient().getUsername());
            out.setRecipientEmail(notification.getRecipient().getEmail());
        }
        return out;
    }

    // ====================== mission flow ======================

    public List<NotificationOutDTO> getByUser(Integer userId) {
        requireUser(userId);
        return notificationRepository.findNotificationsByRecipientId(userId)
                .stream().map(this::toOut).toList();
    }

    public List<NotificationOutDTO> getUnreadByUser(Integer userId) {
        requireUser(userId);
        return notificationRepository.findNotificationsByRecipientIdAndStatus(userId, NotificationStatus.UNREAD)
                .stream().map(this::toOut).toList();
    }

    public NotificationOutDTO markRead(Integer notificationId) {
        Notification notification = notificationRepository.findNotificationById(notificationId);
        if (notification == null) {
            throw new ApiException("Notification with id " + notificationId + " not found");
        }
        if (notification.getStatus() != NotificationStatus.READ) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
        return toOut(notification);
    }

    public void markAllRead(Integer userId) {
        requireUser(userId);
        for (Notification notification : notificationRepository.findNotificationsByRecipientIdAndStatus(userId, NotificationStatus.UNREAD)) {
            notification.setStatus(NotificationStatus.READ);
            notification.setReadAt(LocalDateTime.now());
            notificationRepository.save(notification);
        }
    }

    // ---------- current-user ("me") wrappers ----------

    /** Current-user wrapper: derives the acting user from Basic Auth, then delegates. */
    public List<NotificationOutDTO> getMyNotifications(User user) {
        return getByUser(security.getCurrentUserId(user));
    }

    /** Current-user wrapper: derives the acting user from Basic Auth, then delegates. */
    public List<NotificationOutDTO> getMyUnreadNotifications(User user) {
        return getUnreadByUser(security.getCurrentUserId(user));
    }

    /** Owner-guarded mark-read: the notification must belong to the acting user. */
    public NotificationOutDTO markMyNotificationRead(User user,
                                                     com.example.qubaatisystem.DTO.In.IdInDTO request) {
        security.assertUserOwnsNotification(user, request.getId());
        return markRead(request.getId());
    }

    /** Current-user wrapper: derives the acting user from Basic Auth, then delegates. */
    public void markAllMyNotificationsRead(User user) {
        markAllRead(security.getCurrentUserId(user));
    }

    /** Internal event helper: create an UNREAD notification for a user (no-op if recipient is null). */
    public void notify(User recipient, NotificationType type, String title, String message) {
        if (recipient == null) {
            return;
        }
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setStatus(NotificationStatus.UNREAD);
        notification.setCreatedAt(LocalDateTime.now());
        notification.setRecipient(recipient);
        notification.setId(null);
        notificationRepository.save(notification);
    }

    private void requireUser(Integer userId) {
        if (userRepository.findUserById(userId) == null) {
            throw new ApiException("User with id " + userId + " not found");
        }
    }
}
