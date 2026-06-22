package com.example.qubaatisystem.Config;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.Enum.UserRole;
import com.example.qubaatisystem.Model.Activity;
import com.example.qubaatisystem.Model.ActivityAssignment;
import com.example.qubaatisystem.Model.ActivitySubmission;
import com.example.qubaatisystem.Model.Classroom;
import com.example.qubaatisystem.Model.Notification;
import com.example.qubaatisystem.Model.Parent;
import com.example.qubaatisystem.Model.Recommendation;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentAnswer;
import com.example.qubaatisystem.Model.Teacher;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Repository.ActivityRepository;
import com.example.qubaatisystem.Repository.ActivitySubmissionRepository;
import com.example.qubaatisystem.Repository.ClassroomRepository;
import com.example.qubaatisystem.Repository.NotificationRepository;
import com.example.qubaatisystem.Repository.ParentRepository;
import com.example.qubaatisystem.Repository.RecommendationRepository;
import com.example.qubaatisystem.Repository.StudentAnswerRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.TeacherRepository;
import com.example.qubaatisystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

/**
 * Ownership / current-principal checks layered on top of the coarse role rules in {@code SecurityConfig}.
 * A PARENT may only touch their own {@code parentId}, a TEACHER their own {@code teacherId}, a STUDENT their own
 * {@code studentId}; ADMIN bypasses every check. Denials throw {@link AccessDeniedException} (mapped to HTTP 403
 * by the global advice). No Optional, no JpaRepository.findById — uses the singular project finders.
 */
@Service
@RequiredArgsConstructor
public class SecurityOwnershipService {

    private final UserRepository userRepository;
    private final ParentRepository parentRepository;
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final NotificationRepository notificationRepository;
    private final ClassroomRepository classroomRepository;
    private final ActivityRepository activityRepository;
    private final ActivitySubmissionRepository activitySubmissionRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final RecommendationRepository recommendationRepository;

    private boolean teacherMatches(Teacher teacher, Integer teacherId) {
        return teacher != null && teacher.getId() != null && teacher.getId().equals(teacherId);
    }

    // =====================================================================================================
    // User-based API (Tahadaw style): controllers take @AuthenticationPrincipal User user and pass it here, so
    // the acting profile is ALWAYS derived from Basic Auth — never from a path/body id. ADMIN bypasses ownership.
    // These do not touch the SecurityContext; the principal arrives as the method argument.
    // =====================================================================================================

    private User requireUser(User user) {
        if (user == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        return user;
    }

    public boolean isAdmin(User user) {
        return requireUser(user).getRole() == UserRole.ADMIN;
    }

    public Integer getCurrentUserId(User user) {
        return requireUser(user).getId();
    }

    public Integer getCurrentParentId(User user) {
        Parent parent = parentRepository.findParentByUserId(requireUser(user).getId());
        if (parent == null) {
            throw new AccessDeniedException("Current user is not a parent");
        }
        return parent.getId();
    }

    public Integer getCurrentTeacherId(User user) {
        Teacher teacher = teacherRepository.findTeacherByUserId(requireUser(user).getId());
        if (teacher == null) {
            throw new AccessDeniedException("Current user is not a teacher");
        }
        return teacher.getId();
    }

    public Integer getCurrentStudentId(User user) {
        Student student = studentRepository.findStudentByUserId(requireUser(user).getId());
        if (student == null) {
            throw new AccessDeniedException("Current user is not a student");
        }
        return student.getId();
    }

    public void assertAdmin(User user) {
        if (!isAdmin(user)) {
            throw new AccessDeniedException("Admin privileges are required for this operation");
        }
    }

    /** Must be a TEACHER (or ADMIN). */
    public void assertTeacher(User user) {
        if (isAdmin(user)) {
            return;
        }
        if (teacherRepository.findTeacherByUserId(requireUser(user).getId()) == null) {
            throw new AccessDeniedException("Only a teacher may perform this operation");
        }
    }

    /** Must be a PARENT (or ADMIN). */
    public void assertParent(User user) {
        if (isAdmin(user)) {
            return;
        }
        if (parentRepository.findParentByUserId(requireUser(user).getId()) == null) {
            throw new AccessDeniedException("Only a parent may perform this operation");
        }
    }

    /** Must be a STUDENT (or ADMIN). */
    public void assertStudent(User user) {
        if (isAdmin(user)) {
            return;
        }
        if (studentRepository.findStudentByUserId(requireUser(user).getId()) == null) {
            throw new AccessDeniedException("Only a student may perform this operation");
        }
    }

    /** TEACHER → own teacherId (any requested id ignored); ADMIN → must supply requestedTeacherId; else 403. */
    public Integer resolveOwningTeacherId(User user, Integer requestedTeacherId) {
        if (requireUser(user).getRole() == UserRole.TEACHER) {
            return getCurrentTeacherId(user);
        }
        if (user.getRole() == UserRole.ADMIN) {
            if (requestedTeacherId == null) {
                throw new ApiException("An admin must specify teacherId for this operation");
            }
            return requestedTeacherId;
        }
        throw new AccessDeniedException("Only a teacher or admin may perform this operation");
    }

    /** The current parent must own the given child (ADMIN bypasses). */
    public void assertParentOwnsChild(User user, Integer studentId) {
        if (isAdmin(user)) {
            return;
        }
        Integer parentId = getCurrentParentId(user);
        Student student = studentRepository.findStudentById(studentId);
        if (student == null || student.getParent() == null || !student.getParent().getId().equals(parentId)) {
            throw new AccessDeniedException("That child does not belong to you");
        }
    }

    /** The classroom must be owned by the current teacher (ADMIN bypasses). */
    public void assertTeacherOwnsClassroom(User user, Integer classroomId) {
        if (isAdmin(user)) {
            return;
        }
        Integer teacherId = getCurrentTeacherId(user);
        Classroom classroom = classroomRepository.findClassroomById(classroomId);
        if (classroom == null || classroom.getTeacher() == null || !classroom.getTeacher().getId().equals(teacherId)) {
            throw new AccessDeniedException("That classroom does not belong to you");
        }
    }

    /** The activity must be owned (createdByTeacher) by the current teacher (ADMIN bypasses). */
    public void assertTeacherOwnsActivity(User user, Integer activityId) {
        if (isAdmin(user)) {
            return;
        }
        Integer teacherId = getCurrentTeacherId(user);
        Activity activity = activityRepository.findActivityById(activityId);
        if (activity == null || activity.getCreatedByTeacher() == null
                || !activity.getCreatedByTeacher().getId().equals(teacherId)) {
            throw new AccessDeniedException("That activity does not belong to you");
        }
    }

    /** The submission must belong to one of the current teacher's activities/classrooms (ADMIN bypasses). */
    public void assertTeacherOwnsSubmission(User user, Integer submissionId) {
        if (isAdmin(user)) {
            return;
        }
        Integer teacherId = getCurrentTeacherId(user);
        ActivitySubmission submission = activitySubmissionRepository.findActivitySubmissionById(submissionId);
        if (submission == null || submission.getActivityAssignment() == null) {
            throw new AccessDeniedException("That submission does not belong to you");
        }
        ActivityAssignment assignment = submission.getActivityAssignment();
        Teacher activityOwner = (assignment.getActivity() != null) ? assignment.getActivity().getCreatedByTeacher() : null;
        Teacher classroomTeacher = (assignment.getClassroom() != null) ? assignment.getClassroom().getTeacher() : null;
        boolean owns = teacherMatches(activityOwner, teacherId)
                || teacherMatches(classroomTeacher, teacherId)
                || teacherMatches(assignment.getAssignedByTeacher(), teacherId);
        if (!owns) {
            throw new AccessDeniedException("That submission does not belong to you");
        }
    }

    /** The submission must belong to the current student (ADMIN bypasses). */
    public void assertStudentOwnsSubmission(User user, Integer submissionId) {
        if (isAdmin(user)) {
            return;
        }
        Integer studentId = getCurrentStudentId(user);
        ActivitySubmission submission = activitySubmissionRepository.findActivitySubmissionById(submissionId);
        if (submission == null || submission.getStudent() == null
                || !submission.getStudent().getId().equals(studentId)) {
            throw new AccessDeniedException("That submission does not belong to you");
        }
    }

    /** The answer's submission must belong to the current teacher (ADMIN bypasses). */
    public void assertTeacherOwnsAnswerSubmission(User user, Integer answerId) {
        if (isAdmin(user)) {
            return;
        }
        StudentAnswer answer = studentAnswerRepository.findStudentAnswerById(answerId);
        if (answer == null || answer.getActivitySubmission() == null) {
            throw new AccessDeniedException("That answer does not belong to you");
        }
        assertTeacherOwnsSubmission(user, answer.getActivitySubmission().getId());
    }

    /** The recommendation must belong to the current student (ADMIN bypasses). */
    public void assertStudentOwnsRecommendation(User user, Integer recommendationId) {
        if (isAdmin(user)) {
            return;
        }
        Integer studentId = getCurrentStudentId(user);
        Recommendation recommendation = recommendationRepository.findRecommendationById(recommendationId);
        if (recommendation == null || recommendation.getStudent() == null
                || !recommendation.getStudent().getId().equals(studentId)) {
            throw new AccessDeniedException("That recommendation does not belong to you");
        }
    }

    /** The current teacher may assign only to a student in one of their classrooms (ADMIN bypasses). */
    public void assertTeacherCanAssignToStudent(User user, Integer studentId) {
        if (isAdmin(user)) {
            return;
        }
        Integer teacherId = getCurrentTeacherId(user);
        Student student = studentRepository.findStudentById(studentId);
        if (student == null || student.getClassroom() == null || student.getClassroom().getTeacher() == null
                || !student.getClassroom().getTeacher().getId().equals(teacherId)) {
            throw new AccessDeniedException("You may only assign to students in your own classroom");
        }
    }

    /** The notification's recipient must be the authenticated user (ADMIN bypasses). */
    public void assertUserOwnsNotification(User user, Integer notificationId) {
        if (isAdmin(user)) {
            return;
        }
        Notification notification = notificationRepository.findNotificationById(notificationId);
        if (notification == null || notification.getRecipient() == null
                || !notification.getRecipient().getId().equals(requireUser(user).getId())) {
            throw new AccessDeniedException("That notification does not belong to you");
        }
    }
}
