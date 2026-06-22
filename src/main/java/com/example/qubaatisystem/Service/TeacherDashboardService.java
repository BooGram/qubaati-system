package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardActivitySummaryOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardClassroomOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardMissionSummaryOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardOutDTO;
import com.example.qubaatisystem.DTO.Out.TeacherDashboardStudentOutDTO;
import com.example.qubaatisystem.Enum.ActivityAssignmentStatus;
import com.example.qubaatisystem.Enum.ActivityStatus;
import com.example.qubaatisystem.Enum.ActivitySubmissionStatus;
import com.example.qubaatisystem.Enum.MissionSessionStatus;
import com.example.qubaatisystem.Model.Activity;
import com.example.qubaatisystem.Model.ActivityAssignment;
import com.example.qubaatisystem.Model.ActivitySubmission;
import com.example.qubaatisystem.Model.Classroom;
import com.example.qubaatisystem.Model.Insight;
import com.example.qubaatisystem.Model.MissionSession;
import com.example.qubaatisystem.Model.Recommendation;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentSkill;
import com.example.qubaatisystem.Model.Teacher;
import com.example.qubaatisystem.Repository.ActivityAssignmentRepository;
import com.example.qubaatisystem.Repository.ActivityRepository;
import com.example.qubaatisystem.Repository.ActivitySubmissionRepository;
import com.example.qubaatisystem.Repository.ClassroomRepository;
import com.example.qubaatisystem.Repository.InsightRepository;
import com.example.qubaatisystem.Repository.MissionSessionRepository;
import com.example.qubaatisystem.Repository.RecommendationRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.StudentSkillRepository;
import com.example.qubaatisystem.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Read-only aggregator for the teacher dashboard (Student 1). It joins Student-1 (classrooms/students),
 * Student-2 (activities/assignments/submissions) and Student-3 (missions/insights/recommendations/skills) data
 * for the teacher's own scope, without modifying any of those flows. Not a FlowService — a dashboard query
 * service (mirrors the existing parent dashboard pattern).
 */
@Service
@RequiredArgsConstructor
public class TeacherDashboardService {

    private static final int DUE_SOON_HOURS = 24;
    private static final int RECENT_LIMIT = 5;

    private final TeacherRepository teacherRepository;
    private final ClassroomRepository classroomRepository;
    private final StudentRepository studentRepository;
    private final ActivityRepository activityRepository;
    private final ActivityAssignmentRepository activityAssignmentRepository;
    private final ActivitySubmissionRepository activitySubmissionRepository;
    private final MissionSessionRepository missionSessionRepository;
    private final InsightRepository insightRepository;
    private final RecommendationRepository recommendationRepository;
    private final StudentSkillRepository studentSkillRepository;

    public TeacherDashboardOutDTO getTeacherDashboard(Integer teacherId) {
        Teacher teacher = requireTeacher(teacherId);
        List<Classroom> classrooms = classroomRepository.findClassroomsByTeacherId(teacherId);
        List<Student> students = studentsOf(classrooms);

        TeacherDashboardOutDTO out = new TeacherDashboardOutDTO();
        out.setTeacherId(teacher.getId());
        out.setFullName(teacher.getFullName());
        out.setSpecialization(teacher.getSpecialization());
        if (teacher.getUser() != null) {
            out.setEmail(teacher.getUser().getEmail());
        }
        out.setClassroomCount(classrooms.size());
        out.setTotalStudentCount(students.size());
        out.setClassrooms(classroomRows(classrooms));
        out.setStudents(studentRows(students));
        out.setActivitySummary(activitySummary(teacherId));
        out.setMissionSummary(missionSummary(students));
        return out;
    }

    public List<TeacherDashboardClassroomOutDTO> getTeacherClassrooms(Integer teacherId) {
        requireTeacher(teacherId);
        return classroomRows(classroomRepository.findClassroomsByTeacherId(teacherId));
    }

    public List<TeacherDashboardStudentOutDTO> getTeacherStudents(Integer teacherId) {
        requireTeacher(teacherId);
        return studentRows(studentsOf(classroomRepository.findClassroomsByTeacherId(teacherId)));
    }

    // ---------- internals ----------

    private List<Student> studentsOf(List<Classroom> classrooms) {
        Map<Integer, Student> byId = new LinkedHashMap<>();
        for (Classroom c : classrooms) {
            for (Student s : studentRepository.findByClassroomId(c.getId())) {
                byId.put(s.getId(), s);
            }
        }
        return new ArrayList<>(byId.values());
    }

    private List<TeacherDashboardClassroomOutDTO> classroomRows(List<Classroom> classrooms) {
        List<TeacherDashboardClassroomOutDTO> rows = new ArrayList<>();
        for (Classroom c : classrooms) {
            int count = studentRepository.findByClassroomId(c.getId()).size();
            rows.add(new TeacherDashboardClassroomOutDTO(
                    c.getId(), c.getName(), c.getGradeLevel(), c.getSection(), count));
        }
        return rows;
    }

    private List<TeacherDashboardStudentOutDTO> studentRows(List<Student> students) {
        List<TeacherDashboardStudentOutDTO> rows = new ArrayList<>();
        for (Student s : students) {
            Integer classroomId = s.getClassroom() != null ? s.getClassroom().getId() : null;
            String classroomName = s.getClassroom() != null ? s.getClassroom().getName() : null;
            rows.add(new TeacherDashboardStudentOutDTO(
                    s.getId(), s.getFullName(), s.getGrade(), s.getAge(),
                    s.getTotalPoints(), s.getCompletedMissionsCount(), classroomId, classroomName));
        }
        return rows;
    }

    private TeacherDashboardActivitySummaryOutDTO activitySummary(Integer teacherId) {
        List<Activity> owned = activityRepository.findActivitiesByCreatedByTeacherId(teacherId);
        int draft = 0, pending = 0, approved = 0, rejected = 0, archived = 0;
        for (Activity a : owned) {
            if (a.getStatus() == null) {
                continue;
            }
            switch (a.getStatus()) {
                case DRAFT -> draft++;
                case PENDING_REVIEW -> pending++;
                case APPROVED -> approved++;
                case REJECTED -> rejected++;
                case ARCHIVED -> archived++;
                default -> { /* no-op */ }
            }
        }

        List<ActivityAssignment> assignments =
                activityAssignmentRepository.findActivityAssignmentsByAssignedByTeacherId(teacherId);
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dueSoonLimit = now.plusHours(DUE_SOON_HOURS);
        int dueSoon = 0, overdue = 0;
        int submissions = 0, pendingGrading = 0, graded = 0, returned = 0;
        int scoreSum = 0, scoreCount = 0;
        for (ActivityAssignment a : assignments) {
            boolean assignedActive = a.getStatus() == ActivityAssignmentStatus.ASSIGNED;
            boolean past = a.getDueDate() != null && a.getDueDate().isBefore(now);
            if (a.getStatus() == ActivityAssignmentStatus.EXPIRED || (assignedActive && past)) {
                overdue++;
            }
            if (assignedActive && a.getDueDate() != null
                    && a.getDueDate().isAfter(now) && a.getDueDate().isBefore(dueSoonLimit)) {
                dueSoon++;
            }
            for (ActivitySubmission s : activitySubmissionRepository.findActivitySubmissionsByActivityAssignmentId(a.getId())) {
                submissions++;
                if (s.getStatus() == ActivitySubmissionStatus.SUBMITTED) {
                    pendingGrading++;
                } else if (s.getStatus() == ActivitySubmissionStatus.GRADED) {
                    graded++;
                    if (s.getScore() != null) {
                        scoreSum += s.getScore();
                        scoreCount++;
                    }
                } else if (s.getStatus() == ActivitySubmissionStatus.RETURNED) {
                    returned++;
                }
            }
        }
        Double avgScore = scoreCount > 0 ? (double) scoreSum / scoreCount : null;

        return new TeacherDashboardActivitySummaryOutDTO(
                owned.size(), draft, pending, approved, rejected, archived,
                assignments.size(), submissions, pendingGrading, graded, returned, avgScore,
                dueSoon, overdue);
    }

    private TeacherDashboardMissionSummaryOutDTO missionSummary(List<Student> students) {
        List<MissionSession> completed = new ArrayList<>();
        Map<String, Integer> weakSkillCounts = new LinkedHashMap<>();
        List<String> recommendations = new ArrayList<>();

        for (Student s : students) {
            for (MissionSession session : missionSessionRepository.findMissionSessionsByStudentId(s.getId())) {
                if (session.getStatus() == MissionSessionStatus.COMPLETED) {
                    completed.add(session);
                }
            }
            for (StudentSkill ss : studentSkillRepository.findStudentSkillsByStudentId(s.getId())) {
                if (isWeak(ss) && ss.getSkill() != null && ss.getSkill().getName() != null) {
                    weakSkillCounts.merge(ss.getSkill().getName(), 1, Integer::sum);
                }
            }
            for (Recommendation r : recommendationRepository.findRecommendationsByStudentId(s.getId())) {
                if (r.getTitle() != null && recommendations.size() < RECENT_LIMIT) {
                    recommendations.add(r.getTitle());
                }
            }
        }

        completed.sort(Comparator.comparing(MissionSession::getEndTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        List<String> recentInsights = new ArrayList<>();
        for (MissionSession session : completed) {
            if (recentInsights.size() >= RECENT_LIMIT) {
                break;
            }
            Insight insight = insightRepository.findInsightByMissionSessionId(session.getId());
            if (insight != null && insight.getSummary() != null) {
                recentInsights.add(insight.getSummary());
            }
        }

        List<String> commonWeakSkills = weakSkillCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(RECENT_LIMIT)
                .map(Map.Entry::getKey)
                .toList();

        return new TeacherDashboardMissionSummaryOutDTO(
                completed.size(), recentInsights, commonWeakSkills, recommendations);
    }

    private boolean isWeak(StudentSkill ss) {
        return (ss.getLevel() != null && ss.getLevel() <= 2)
                || (ss.getScore() != null && ss.getScore() < 50.0);
    }

    private Teacher requireTeacher(Integer teacherId) {
        Teacher teacher = teacherRepository.findTeacherById(teacherId);
        if (teacher == null) {
            throw new ApiException("Teacher with id " + teacherId + " not found");
        }
        return teacher;
    }
}
