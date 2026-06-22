package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.ActivityInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityOutDTO;
import com.example.qubaatisystem.DTO.Out.ActivityReviewOutDTO;
import com.example.qubaatisystem.Enum.ActivityReviewDecision;
import com.example.qubaatisystem.Enum.ActivityStatus;
import com.example.qubaatisystem.Enum.SkillType;
import com.example.qubaatisystem.Model.Activity;
import com.example.qubaatisystem.Model.ActivityReview;
import com.example.qubaatisystem.Model.Skill;
import com.example.qubaatisystem.Model.Teacher;
import com.example.qubaatisystem.Repository.ActivityRepository;
import com.example.qubaatisystem.Repository.ActivityReviewRepository;
import com.example.qubaatisystem.Repository.QuestionRepository;
import com.example.qubaatisystem.Repository.SkillRepository;
import com.example.qubaatisystem.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final TeacherRepository teacherRepository;
    private final ActivityReviewRepository activityReviewRepository;
    private final QuestionRepository questionRepository;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    public List<ActivityOutDTO> getAll() {
        return activityRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public ActivityOutDTO getById(Integer id) {
        Activity activity = activityRepository.findActivityById(id);
        if (activity == null) {
            throw new ApiException("Activity with id " + id + " not found");
        }
        return toOut(activity);
    }

    public void create(ActivityInDTO dto) {
        // Manual scalar mapping (the DTO now carries a teacherId relation id, so ModelMapper's STANDARD
        // matching would ambiguously target setId — see modelmapper-relation-id-mapping convention).
        Activity activity = new Activity();
        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        // The client-supplied status is IGNORED: a manually created activity always enters the review queue as
        // PENDING_REVIEW (there is no public submit-for-review endpoint). It becomes APPROVED only through the
        // review flow (approve/reject/request-revision). This prevents creating an APPROVED activity directly.
        activity.setStatus(ActivityStatus.PENDING_REVIEW);
        activity.setDifficulty(dto.getDifficulty());
        activity.setMaxScore(dto.getMaxScore());
        activity.setCreatedByTeacher(resolveTeacher(dto.getTeacherId()));
        activity.setSkill(resolveActivitySkill(dto.getSkillId(), dto.getSkillType()));
        activity.setId(null);
        activityRepository.save(activity);
    }

    public void update(Integer id, ActivityInDTO dto) {
        Activity activity = requireActivity(id);

        activity.setTitle(dto.getTitle());
        activity.setDescription(dto.getDescription());
        activity.setType(dto.getType());
        // Status is intentionally NOT updated here: a generic edit must not move an activity through the review
        // lifecycle. Status changes only via approve/reject/request-revision (+ AI generation's auto-submit).
        activity.setMaxScore(dto.getMaxScore());
        activity.setDifficulty(dto.getDifficulty());
        // Reassign the owner only when a teacherId is supplied; otherwise keep the existing owner.
        if (dto.getTeacherId() != null) {
            activity.setCreatedByTeacher(requireTeacher(dto.getTeacherId()));
        }
        // Reassign the skill only when a skillId/skillType is supplied; otherwise keep the existing skill.
        if (dto.getSkillId() != null || dto.getSkillType() != null) {
            activity.setSkill(resolveActivitySkill(dto.getSkillId(), dto.getSkillType()));
        }
        activity.setId(id);
        activityRepository.save(activity);
    }

    /**
     * Resolves an EXISTING Skill for an activity: skillId has priority (must exist), else the first existing
     * Skill of skillType, else null (the grading analytics then fall back to a PROBLEM_SOLVING skill). Never
     * creates a Skill.
     */
    public Skill resolveActivitySkill(Integer skillId, SkillType skillType) {
        if (skillId != null) {
            Skill skill = skillRepository.findSkillById(skillId);
            if (skill == null) {
                throw new ApiException("Skill with id " + skillId + " not found");
            }
            return skill;
        }
        if (skillType != null) {
            return skillRepository.findFirstSkillBySkillType(skillType); // may be null -> analytics fallback
        }
        return null;
    }

    /** Optional status filter for the activity list / review queue (null returns all). */
    public List<ActivityOutDTO> getByStatus(ActivityStatus status) {
        List<Activity> activities = (status == null)
                ? activityRepository.findAll()
                : activityRepository.findActivitiesByStatus(status);
        return activities.stream().map(this::toOut).toList();
    }

    /** Teacher-owned activities (Student 1 visibility), optionally filtered by status. */
    public List<ActivityOutDTO> getActivitiesByTeacher(Integer teacherId, ActivityStatus status) {
        requireTeacher(teacherId);
        List<Activity> activities = (status == null)
                ? activityRepository.findActivitiesByCreatedByTeacherId(teacherId)
                : activityRepository.findActivitiesByCreatedByTeacherIdAndStatus(teacherId, status);
        return activities.stream().map(this::toOut).toList();
    }

    public void delete(Integer id) {
        Activity activity = activityRepository.findActivityById(id);
        if (activity == null) {
            throw new ApiException("Activity with id " + id + " not found");
        }
        activityRepository.delete(activity);
    }

    // ====================== REVIEW / APPROVAL FLOW ======================

    public void submitForReview(Integer activityId) {
        Activity activity = requireActivity(activityId);
        if (activity.getStatus() == ActivityStatus.APPROVED) {
            throw new ApiException("Activity is already APPROVED");
        }
        if (activity.getStatus() == ActivityStatus.PENDING_REVIEW) {
            throw new ApiException("Activity is already PENDING_REVIEW");
        }
        if (activity.getStatus() != ActivityStatus.DRAFT && activity.getStatus() != ActivityStatus.REJECTED) {
            throw new ApiException("Only DRAFT or REJECTED activities can be submitted for review");
        }
        if (activity.getTitle() == null || activity.getTitle().isBlank()
                || activity.getDescription() == null || activity.getDescription().isBlank()
                || activity.getType() == null || activity.getDifficulty() == null) {
            throw new ApiException("Activity must have a valid title, description, type and difficulty before review");
        }
        if (questionRepository.findQuestionsByActivityId(activityId).isEmpty()) {
            throw new ApiException("Activity must have at least one question before it can be submitted for review");
        }
        activity.setStatus(ActivityStatus.PENDING_REVIEW);
        activityRepository.save(activity);
    }

    public List<ActivityOutDTO> getReviewQueue() {
        return activityRepository.findActivitiesByStatus(ActivityStatus.PENDING_REVIEW)
                .stream()
                .map(this::toOut)
                .toList();
    }

    public void approveActivity(Integer activityId, Integer teacherId, String reviewComment) {
        Activity activity = requireActivity(activityId);
        Teacher teacher = requireTeacher(teacherId);
        if (activity.getStatus() != ActivityStatus.PENDING_REVIEW) {
            throw new ApiException("Only PENDING_REVIEW activities can be approved");
        }
        String comment = (reviewComment == null || reviewComment.isBlank()) ? "Approved" : reviewComment;
        saveReview(activity, teacher, ActivityReviewDecision.APPROVED, comment);
        activity.setStatus(ActivityStatus.APPROVED);
        activityRepository.save(activity);
    }

    public void rejectActivity(Integer activityId, Integer teacherId, String reviewComment) {
        Activity activity = requireActivity(activityId);
        Teacher teacher = requireTeacher(teacherId);
        if (activity.getStatus() != ActivityStatus.PENDING_REVIEW) {
            throw new ApiException("Only PENDING_REVIEW activities can be rejected");
        }
        String comment = (reviewComment == null || reviewComment.isBlank()) ? "Rejected" : reviewComment;
        saveReview(activity, teacher, ActivityReviewDecision.REJECTED, comment);
        activity.setStatus(ActivityStatus.REJECTED);
        activityRepository.save(activity);
    }

    public void requestRevision(Integer activityId, Integer teacherId, String reviewComment) {
        Activity activity = requireActivity(activityId);
        Teacher teacher = requireTeacher(teacherId);
        if (activity.getStatus() != ActivityStatus.PENDING_REVIEW) {
            throw new ApiException("Only PENDING_REVIEW activities can be sent back for revision");
        }
        String comment = (reviewComment == null || reviewComment.isBlank()) ? "Revision requested" : reviewComment;
        saveReview(activity, teacher, ActivityReviewDecision.REVISION_REQUESTED, comment);
        // Preferred convention: send the activity back to DRAFT so it can be edited/refined again.
        activity.setStatus(ActivityStatus.DRAFT);
        activityRepository.save(activity);
    }

    public List<ActivityReviewOutDTO> getReviewHistory(Integer activityId) {
        requireActivity(activityId);
        return activityReviewRepository.findActivityReviewsByActivityId(activityId)
                .stream()
                .map(this::toReviewOut)
                .toList();
    }

    // ====================== helpers ======================

    private Activity requireActivity(Integer activityId) {
        Activity activity = activityRepository.findActivityById(activityId);
        if (activity == null) {
            throw new ApiException("Activity with id " + activityId + " not found");
        }
        return activity;
    }

    private Teacher requireTeacher(Integer teacherId) {
        Teacher teacher = teacherRepository.findTeacherById(teacherId);
        if (teacher == null) {
            throw new ApiException("Teacher with id " + teacherId + " not found");
        }
        return teacher;
    }

    /** Resolves an optional teacher owner: null id -> no owner; otherwise the teacher must exist. */
    private Teacher resolveTeacher(Integer teacherId) {
        return teacherId == null ? null : requireTeacher(teacherId);
    }

    // Review data is stored in ActivityReview (Activity.reviewedAt/reviewComment were removed).
    private void saveReview(Activity activity, Teacher teacher, ActivityReviewDecision decision, String comment) {
        ActivityReview review = new ActivityReview();
        review.setActivity(activity);
        review.setTeacher(teacher);
        review.setDecision(decision);
        review.setReviewComment(comment);
        review.setReviewedAt(LocalDateTime.now());
        activityReviewRepository.save(review);
    }

    private ActivityReviewOutDTO toReviewOut(ActivityReview review) {
        ActivityReviewOutDTO out = modelMapper.map(review, ActivityReviewOutDTO.class);
        if (review.getActivity() != null) {
            out.setActivityId(review.getActivity().getId());
            out.setActivityTitle(review.getActivity().getTitle());
        }
        if (review.getTeacher() != null) {
            out.setTeacherId(review.getTeacher().getId());
            out.setTeacherName(review.getTeacher().getFullName());
        }
        return out;
    }

    // Manual mapping: Activity now has a createdByTeacher relation; ModelMapper would ambiguously map
    // createdByTeacher.id and activity.id both onto ActivityOutDTO.id.
    private ActivityOutDTO toOut(Activity activity) {
        ActivityOutDTO out = new ActivityOutDTO();
        out.setId(activity.getId());
        out.setTitle(activity.getTitle());
        out.setDescription(activity.getDescription());
        out.setType(activity.getType());
        out.setStatus(activity.getStatus());
        out.setDifficulty(activity.getDifficulty());
        out.setMaxScore(activity.getMaxScore());
        out.setCreatedAt(activity.getCreatedAt());
        if (activity.getCreatedByTeacher() != null) {
            out.setCreatedByTeacherId(activity.getCreatedByTeacher().getId());
            out.setCreatedByTeacherName(activity.getCreatedByTeacher().getFullName());
        }
        if (activity.getSkill() != null) {
            out.setSkillId(activity.getSkill().getId());
            out.setSkillName(activity.getSkill().getName());
        }
        return out;
    }
}
