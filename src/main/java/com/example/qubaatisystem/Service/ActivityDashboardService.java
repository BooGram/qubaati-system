package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.Out.StudentActivityDashboardOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentDashboardAssignmentOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentDashboardSubmissionOutDTO;
import com.example.qubaatisystem.Enum.ActivityAssignmentStatus;
import com.example.qubaatisystem.Enum.ActivitySubmissionStatus;
import com.example.qubaatisystem.Model.Activity;
import com.example.qubaatisystem.Model.ActivityAssignment;
import com.example.qubaatisystem.Model.ActivitySubmission;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Repository.ActivityAssignmentRepository;
import com.example.qubaatisystem.Repository.ActivitySubmissionRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only aggregator for the student activity dashboard. It only queries existing repositories and shapes
 * the result; it never exposes correct answers. Not a flow/FlowService class — a plain dashboard query service
 * (mirrors the existing parent/teacher dashboard services).
 */
@Service
@RequiredArgsConstructor
public class ActivityDashboardService {

    private static final int DUE_SOON_HOURS = 24;
    private static final int RECENT_GRADED_LIMIT = 5;

    private final ActivityAssignmentRepository activityAssignmentRepository;
    private final ActivitySubmissionRepository activitySubmissionRepository;
    private final StudentRepository studentRepository;

    public StudentActivityDashboardOutDTO getStudentActivityDashboard(Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueSoonLimit = now.plusHours(DUE_SOON_HOURS);

        List<ActivityAssignment> assignments = collectAssignments(student);
        List<ActivitySubmission> submissions = activitySubmissionRepository.findActivitySubmissionsByStudentId(studentId);

        int assignedCount = 0;
        int overdueCount = 0;
        int dueSoonCount = 0;
        List<StudentDashboardAssignmentOutDTO> dueSoonAssignments = new ArrayList<>();
        for (ActivityAssignment a : assignments) {
            boolean assignedActive = a.getStatus() == ActivityAssignmentStatus.ASSIGNED;
            if (assignedActive) {
                assignedCount++;
            }
            boolean past = a.getDueDate() != null && a.getDueDate().isBefore(now);
            if (a.getStatus() == ActivityAssignmentStatus.EXPIRED || (assignedActive && past)) {
                overdueCount++;
            }
            if (assignedActive && a.getDueDate() != null
                    && a.getDueDate().isAfter(now) && a.getDueDate().isBefore(dueSoonLimit)) {
                dueSoonCount++;
                dueSoonAssignments.add(toAssignmentDto(a));
            }
        }

        int inProgressCount = 0;
        int submittedCount = 0;
        int gradedCount = 0;
        int returnedCount = 0;
        int scoreSum = 0;
        int scoreCount = 0;
        List<StudentDashboardSubmissionOutDTO> returnedSubmissions = new ArrayList<>();
        List<ActivitySubmission> gradedSubmissions = new ArrayList<>();
        for (ActivitySubmission s : submissions) {
            switch (s.getStatus()) {
                case IN_PROGRESS -> inProgressCount++;
                case SUBMITTED -> submittedCount++;
                case GRADED -> {
                    gradedCount++;
                    gradedSubmissions.add(s);
                    if (s.getScore() != null) {
                        scoreSum += s.getScore();
                        scoreCount++;
                    }
                }
                case RETURNED -> {
                    returnedCount++;
                    returnedSubmissions.add(toSubmissionDto(s));
                }
                default -> { /* NOT_STARTED: not counted */ }
            }
        }

        Double averageScore = scoreCount > 0 ? (double) scoreSum / scoreCount : null;

        gradedSubmissions.sort(Comparator.comparing(
                ActivitySubmission::getSubmittedAt, Comparator.nullsLast(Comparator.reverseOrder())));
        List<StudentDashboardSubmissionOutDTO> recentGraded = new ArrayList<>();
        for (ActivitySubmission s : gradedSubmissions) {
            if (recentGraded.size() >= RECENT_GRADED_LIMIT) {
                break;
            }
            recentGraded.add(toSubmissionDto(s));
        }

        String latestFeedback = latestFeedback(submissions);

        return new StudentActivityDashboardOutDTO(
                studentId,
                assignedCount, inProgressCount, submittedCount, gradedCount, returnedCount,
                overdueCount, dueSoonCount,
                averageScore, latestFeedback,
                dueSoonAssignments, returnedSubmissions, recentGraded);
    }

    // ---------- helpers ----------

    /** Direct assignments + assignments targeting the student's classroom, deduplicated by id. */
    private List<ActivityAssignment> collectAssignments(Student student) {
        Map<Integer, ActivityAssignment> byId = new LinkedHashMap<>();
        for (ActivityAssignment a : activityAssignmentRepository.findActivityAssignmentsByStudentId(student.getId())) {
            byId.put(a.getId(), a);
        }
        if (student.getClassroom() != null) {
            for (ActivityAssignment a : activityAssignmentRepository
                    .findActivityAssignmentsByClassroomId(student.getClassroom().getId())) {
                byId.put(a.getId(), a);
            }
        }
        return new ArrayList<>(byId.values());
    }

    private String latestFeedback(List<ActivitySubmission> submissions) {
        ActivitySubmission latest = null;
        for (ActivitySubmission s : submissions) {
            boolean hasFeedback = (s.getTeacherFeedback() != null && !s.getTeacherFeedback().isBlank())
                    || (s.getAiFeedback() != null && !s.getAiFeedback().isBlank());
            if (!hasFeedback) {
                continue;
            }
            if (latest == null || after(s.getSubmittedAt(), latest.getSubmittedAt())) {
                latest = s;
            }
        }
        if (latest == null) {
            return null;
        }
        return (latest.getTeacherFeedback() != null && !latest.getTeacherFeedback().isBlank())
                ? latest.getTeacherFeedback() : latest.getAiFeedback();
    }

    private boolean after(LocalDateTime a, LocalDateTime b) {
        if (a == null) {
            return false;
        }
        if (b == null) {
            return true;
        }
        return a.isAfter(b);
    }

    private StudentDashboardAssignmentOutDTO toAssignmentDto(ActivityAssignment a) {
        Activity activity = a.getActivity();
        return new StudentDashboardAssignmentOutDTO(
                a.getId(),
                activity != null ? activity.getId() : null,
                activity != null ? activity.getTitle() : null,
                a.getDueDate(),
                a.getStatus());
    }

    private StudentDashboardSubmissionOutDTO toSubmissionDto(ActivitySubmission s) {
        Activity activity = (s.getActivityAssignment() != null) ? s.getActivityAssignment().getActivity() : null;
        return new StudentDashboardSubmissionOutDTO(
                s.getId(),
                activity != null ? activity.getId() : null,
                activity != null ? activity.getTitle() : null,
                s.getStatus(),
                s.getScore(),
                activity != null ? activity.getMaxScore() : null,
                s.getSubmittedAt(),
                s.getTeacherFeedback(),
                s.getAiFeedback());
    }
}
