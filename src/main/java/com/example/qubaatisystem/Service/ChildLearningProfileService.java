package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.Out.ChildLearningProfileOutDTO;
import com.example.qubaatisystem.DTO.Out.ParentDashboardOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentOutDTO;
import com.example.qubaatisystem.Enum.ActivitySubmissionStatus;
import com.example.qubaatisystem.Enum.MissionSessionStatus;
import com.example.qubaatisystem.Model.ActivitySubmission;
import com.example.qubaatisystem.Model.Insight;
import com.example.qubaatisystem.Model.LearningStyle;
import com.example.qubaatisystem.Model.MissionSession;
import com.example.qubaatisystem.Model.Recommendation;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentSkill;
import com.example.qubaatisystem.Repository.ActivitySubmissionRepository;
import com.example.qubaatisystem.Repository.InsightRepository;
import com.example.qubaatisystem.Repository.LearningStyleRepository;
import com.example.qubaatisystem.Repository.MissionSessionRepository;
import com.example.qubaatisystem.Repository.RecommendationRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.StudentSkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Read-only aggregator for a child's combined learning profile (Student 1 parent-facing view). Joins skills,
 * learning style, Student-2 activity performance and Student-3 mission insight/recommendations. Validates the
 * parent-child relationship in service logic (there is no security layer). Never exposes correct answers.
 */
@Service
@RequiredArgsConstructor
public class ChildLearningProfileService {

    private static final int RECENT_LIMIT = 5;

    private final StudentRepository studentRepository;
    private final LearningStyleRepository learningStyleRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ActivitySubmissionRepository activitySubmissionRepository;
    private final MissionSessionRepository missionSessionRepository;
    private final InsightRepository insightRepository;
    private final RecommendationRepository recommendationRepository;

    public ChildLearningProfileOutDTO getLearningProfile(Integer parentId, Integer studentId) {
        Student student = studentRepository.findStudentById(studentId);
        if (student == null) {
            throw new ApiException("Student with id " + studentId + " not found");
        }
        if (student.getParent() == null || !student.getParent().getId().equals(parentId)) {
            throw new ApiException("Student with id " + studentId + " does not belong to parent with id " + parentId);
        }

        ChildLearningProfileOutDTO out = new ChildLearningProfileOutDTO();
        out.setStudentId(student.getId());
        out.setFullName(student.getFullName());
        out.setGrade(student.getGrade());
        out.setAge(student.getAge());
        out.setTotalPoints(student.getTotalPoints());
        out.setCompletedMissionsCount(student.getCompletedMissionsCount());

        LearningStyle ls = learningStyleRepository.findLearningStyleByStudentId(studentId);
        if (ls != null) {
            out.setLearningStylePrimary(ls.getPrimaryStyle() != null ? ls.getPrimaryStyle().name() : null);
            out.setLearningStyleSecondary(ls.getSecondaryStyle() != null ? ls.getSecondaryStyle().name() : null);
            out.setLearningStyleConfidence(ls.getConfidence());
        }

        List<ChildLearningProfileOutDTO.SkillRow> skillRows = new ArrayList<>();
        List<String> weakSkills = new ArrayList<>();
        for (StudentSkill ss : studentSkillRepository.findStudentSkillsByStudentId(studentId)) {
            if (ss.getSkill() == null) {
                continue;
            }
            String name = ss.getSkill().getName();
            String type = ss.getSkill().getSkillType() != null ? ss.getSkill().getSkillType().name() : null;
            skillRows.add(new ChildLearningProfileOutDTO.SkillRow(name, type, ss.getScore(), ss.getLevel()));
            if (isWeak(ss) && name != null) {
                weakSkills.add(name);
            }
        }
        out.setSkills(skillRows);
        out.setWeakSkills(weakSkills);

        ActivityStats as = activityStats(studentId);
        out.setActivitiesTotal(as.total);
        out.setActivitiesGraded(as.graded);
        out.setActivitiesInProgress(as.inProgress);
        out.setActivitiesReturned(as.returned);
        out.setAverageActivityScore(as.average);

        MissionStats ms = missionStats(studentId);
        out.setCompletedMissionSessionsCount(ms.completed);
        out.setActiveMissionSessionsCount(ms.active);
        out.setLatestMissionInsightSummary(ms.insightSummary);
        out.setLatestMissionInsightRecommendation(ms.insightRecommendation);

        List<String> recs = new ArrayList<>();
        for (Recommendation r : recommendationRepository.findRecommendationsByStudentId(studentId)) {
            if (r.getTitle() != null && recs.size() < RECENT_LIMIT) {
                recs.add(r.getTitle());
            }
        }
        out.setTopRecommendations(recs);
        return out;
    }

    /** Lightweight per-child card for the parent dashboard (activity + mission progress). */
    public ParentDashboardOutDTO.ChildCard buildChildCard(StudentOutDTO child) {
        ParentDashboardOutDTO.ChildCard card = new ParentDashboardOutDTO.ChildCard();
        card.setStudentId(child.getId());
        card.setFullName(child.getFullName());
        card.setGrade(child.getGrade());
        card.setAge(child.getAge());
        card.setTotalPoints(child.getTotalPoints() != null ? child.getTotalPoints() : 0);
        card.setCompletedMissionsCount(child.getCompletedMissionsCount() != null ? child.getCompletedMissionsCount() : 0);
        card.setClassroomId(child.getClassroomId());
        card.setClassroomName(child.getClassroomName());

        ActivityStats as = activityStats(child.getId());
        card.setGradedActivitiesCount(as.graded);
        card.setAverageActivityScore(as.average);

        MissionStats ms = missionStats(child.getId());
        card.setCompletedMissionSessionsCount(ms.completed);
        card.setLatestInsightSummary(ms.insightSummary);
        return card;
    }

    // ---------- internals ----------

    private ActivityStats activityStats(Integer studentId) {
        int total = 0, graded = 0, inProgress = 0, returned = 0, scoreSum = 0, scoreCount = 0;
        for (ActivitySubmission s : activitySubmissionRepository.findActivitySubmissionsByStudentId(studentId)) {
            total++;
            if (s.getStatus() == ActivitySubmissionStatus.GRADED) {
                graded++;
                if (s.getScore() != null) {
                    scoreSum += s.getScore();
                    scoreCount++;
                }
            } else if (s.getStatus() == ActivitySubmissionStatus.IN_PROGRESS) {
                inProgress++;
            } else if (s.getStatus() == ActivitySubmissionStatus.RETURNED) {
                returned++;
            }
        }
        Double avg = scoreCount > 0 ? (double) scoreSum / scoreCount : null;
        return new ActivityStats(total, graded, inProgress, returned, avg);
    }

    private MissionStats missionStats(Integer studentId) {
        List<MissionSession> sessions = missionSessionRepository.findMissionSessionsByStudentId(studentId);
        int completed = 0, active = 0;
        List<MissionSession> completedSessions = new ArrayList<>();
        for (MissionSession s : sessions) {
            if (s.getStatus() == MissionSessionStatus.COMPLETED) {
                completed++;
                completedSessions.add(s);
            } else if (s.getStatus() == MissionSessionStatus.STARTED || s.getStatus() == MissionSessionStatus.PAUSED) {
                active++;
            }
        }
        completedSessions.sort(Comparator.comparing(MissionSession::getEndTime,
                Comparator.nullsLast(Comparator.reverseOrder())));
        String insightSummary = null;
        String insightRecommendation = null;
        for (MissionSession s : completedSessions) {
            Insight insight = insightRepository.findInsightByMissionSessionId(s.getId());
            if (insight != null) {
                insightSummary = insight.getSummary();
                insightRecommendation = insight.getRecommendation();
                break;
            }
        }
        return new MissionStats(completed, active, insightSummary, insightRecommendation);
    }

    private boolean isWeak(StudentSkill ss) {
        return (ss.getLevel() != null && ss.getLevel() <= 2)
                || (ss.getScore() != null && ss.getScore() < 50.0);
    }

    private record ActivityStats(int total, int graded, int inProgress, int returned, Double average) {
    }

    private record MissionStats(int completed, int active, String insightSummary, String insightRecommendation) {
    }
}
