package com.example.qubaatisystem.Security;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.Enum.UserRole;
import com.example.qubaatisystem.Model.Parent;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.Teacher;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Repository.ParentRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.TeacherRepository;
import com.example.qubaatisystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // ---- current principal ----

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal() == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) {
            return user;
        }
        // Fallback (e.g. principal stored as username) — resolve via the repository.
        User user = userRepository.findUserByUsername(auth.getName());
        if (user == null) {
            throw new AccessDeniedException("Not authenticated");
        }
        return user;
    }

    public boolean isAdmin() {
        return getCurrentUser().getRole() == UserRole.ADMIN;
    }

    public Integer getCurrentParentId() {
        Parent parent = parentRepository.findParentByUserId(getCurrentUser().getId());
        if (parent == null) {
            throw new AccessDeniedException("Current user is not a parent");
        }
        return parent.getId();
    }

    public Integer getCurrentTeacherId() {
        Teacher teacher = teacherRepository.findTeacherByUserId(getCurrentUser().getId());
        if (teacher == null) {
            throw new AccessDeniedException("Current user is not a teacher");
        }
        return teacher.getId();
    }

    public Integer getCurrentStudentId() {
        Student student = studentRepository.findStudentByUserId(getCurrentUser().getId());
        if (student == null) {
            throw new AccessDeniedException("Current user is not a student");
        }
        return student.getId();
    }

    // ---- assertions (ADMIN always allowed) ----

    public void assertAdmin() {
        if (!isAdmin()) {
            throw new AccessDeniedException("Admin privileges are required for this operation");
        }
    }

    /**
     * Resolves the owning teacher for a teacher-owned resource (e.g. a classroom or activity) from the
     * authenticated principal: a TEACHER always owns it themselves (any {@code requestedTeacherId} in the body is
     * ignored — they can never set another teacher); an ADMIN may act on a teacher's behalf but must supply the
     * teacherId. Any other role is forbidden.
     */
    public Integer resolveOwningTeacherId(Integer requestedTeacherId) {
        User current = getCurrentUser();
        if (current.getRole() == UserRole.TEACHER) {
            return getCurrentTeacherId();
        }
        if (current.getRole() == UserRole.ADMIN) {
            if (requestedTeacherId == null) {
                throw new ApiException("An admin must specify teacherId for this operation");
            }
            return requestedTeacherId;
        }
        throw new AccessDeniedException("Only a teacher or admin may perform this operation");
    }

    public void assertCurrentParentOrAdmin(Integer parentId) {
        if (isAdmin()) {
            return;
        }
        Parent parent = parentRepository.findParentByUserId(getCurrentUser().getId());
        if (parent == null || parentId == null || !parent.getId().equals(parentId)) {
            throw new AccessDeniedException("You may only access your own parent data");
        }
    }

    public void assertCurrentTeacherOrAdmin(Integer teacherId) {
        if (isAdmin()) {
            return;
        }
        Teacher teacher = teacherRepository.findTeacherByUserId(getCurrentUser().getId());
        if (teacher == null || teacherId == null || !teacher.getId().equals(teacherId)) {
            throw new AccessDeniedException("You may only access your own teacher data");
        }
    }

    public void assertCurrentStudentOrAdmin(Integer studentId) {
        if (isAdmin()) {
            return;
        }
        Student student = studentRepository.findStudentByUserId(getCurrentUser().getId());
        if (student == null || studentId == null || !student.getId().equals(studentId)) {
            throw new AccessDeniedException("You may only access your own student data");
        }
    }

    /** A parent may only act on a child that belongs to them (ADMIN bypasses). */
    public void assertParentOwnsStudentOrAdmin(Integer parentId, Integer studentId) {
        assertCurrentParentOrAdmin(parentId);
        if (isAdmin()) {
            return;
        }
        Student student = studentRepository.findStudentById(studentId);
        if (student == null || student.getParent() == null || !student.getParent().getId().equals(parentId)) {
            throw new AccessDeniedException("That child does not belong to this parent");
        }
    }
}
