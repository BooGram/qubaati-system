package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.SkillProgressHistoryInDTO;
import com.example.qubaatisystem.DTO.Out.SkillProgressHistoryOutDTO;
import com.example.qubaatisystem.DTO.Out.SkillUpdateOutDTO;
import com.example.qubaatisystem.Enum.SkillType;
import com.example.qubaatisystem.Model.Skill;
import com.example.qubaatisystem.Model.SkillProgressHistory;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentSkill;
import com.example.qubaatisystem.Repository.SkillProgressHistoryRepository;
import com.example.qubaatisystem.Repository.SkillRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.StudentSkillRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillProgressHistoryService {

    private final SkillProgressHistoryRepository skillProgressHistoryRepository;
    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final StudentSkillRepository studentSkillRepository;
    private final ModelMapper modelMapper;

    public List<SkillProgressHistoryOutDTO> getAll() {
        return skillProgressHistoryRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public SkillProgressHistoryOutDTO getById(Integer id) {
        SkillProgressHistory skillProgressHistory = skillProgressHistoryRepository.findSkillProgressHistoryById(id);
        if (skillProgressHistory == null) {
            throw new ApiException("SkillProgressHistory with id " + id + " not found");
        }
        return toOut(skillProgressHistory);
    }

    public void create(SkillProgressHistoryInDTO dto) {
        // Manual scalar mapping (DTO has multiple *Id relation fields that would make
        // ModelMapper ambiguous on setId()); relations are resolved by applyRelationships.
        SkillProgressHistory skillProgressHistory = new SkillProgressHistory();
        skillProgressHistory.setPreviousScore(dto.getPreviousScore());
        skillProgressHistory.setNewScore(dto.getNewScore());
        skillProgressHistory.setPreviousLevel(dto.getPreviousLevel());
        skillProgressHistory.setNewLevel(dto.getNewLevel());
        skillProgressHistory.setReason(dto.getReason());
        // changedAt is server-assigned business data, never taken from the client (any DTO value is ignored).
        skillProgressHistory.setChangedAt(LocalDateTime.now());

        applyRelationships(skillProgressHistory, dto);

        skillProgressHistory.setId(null);
        skillProgressHistoryRepository.save(skillProgressHistory);
    }

    public void update(Integer id, SkillProgressHistoryInDTO dto) {
        SkillProgressHistory skillProgressHistory = skillProgressHistoryRepository.findSkillProgressHistoryById(id);
        if (skillProgressHistory == null) {
            throw new ApiException("SkillProgressHistory with id " + id + " not found");
        }

        // Manual scalar mapping (relations are re-resolved by applyRelationships).
        skillProgressHistory.setPreviousScore(dto.getPreviousScore());
        skillProgressHistory.setNewScore(dto.getNewScore());
        skillProgressHistory.setPreviousLevel(dto.getPreviousLevel());
        skillProgressHistory.setNewLevel(dto.getNewLevel());
        skillProgressHistory.setReason(dto.getReason());
        // changedAt is server-assigned business data, never taken from the client (any DTO value is ignored).
        skillProgressHistory.setChangedAt(LocalDateTime.now());
        skillProgressHistory.setId(id);

        applyRelationships(skillProgressHistory, dto);

        skillProgressHistoryRepository.save(skillProgressHistory);
    }

    public void delete(Integer id) {
        SkillProgressHistory skillProgressHistory = skillProgressHistoryRepository.findSkillProgressHistoryById(id);
        if (skillProgressHistory == null) {
            throw new ApiException("SkillProgressHistory with id " + id + " not found");
        }
        skillProgressHistoryRepository.delete(skillProgressHistory);
    }

    // ---------- automatic progress (called after an activity is graded) ----------

    /**
     * After an activity is graded, record skill progress against the activity's REAL skill (passed in). When the
     * activity has no mapped skill, fall back (last resort) to the first {@link SkillType#PROBLEM_SOLVING} skill;
     * if none exists, skip (no fake data). New score is the activity percentage on a 0–100 scale, level in 1–5
     * bands. The StudentSkill upsert is duplicate-safe.
     */
    public void recordAutomaticSkillProgress(Student student, Skill activitySkill, int score, int maxScore, String activityTitle) {
        if (student == null) {
            return;
        }
        Skill skill = activitySkill;
        if (skill == null) {
            List<Skill> fallback = skillRepository.findSkillsBySkillType(SkillType.PROBLEM_SOLVING);
            if (fallback.isEmpty()) {
                return; // No activity skill and no PROBLEM_SOLVING skill to map onto -> skip (documented).
            }
            skill = fallback.get(0);
        }
        double newScore = toScore0to100(score, maxScore);
        int newLevel = computeLevel(newScore);

        SkillUpsert upsert = upsertStudentSkill(student, skill, newScore, newLevel);
        writeSkillHistory(student, skill, upsert, newScore, newLevel,
                "Updated automatically after completing activity: " + activityTitle);
    }

    /**
     * Records skill progress against a SPECIFIC skill after a mission. Duplicate-safe upsert. Returns the change
     * for the completion response, or null if student/skill is null.
     */
    public SkillUpdateOutDTO recordMissionSkillProgress(Student student, Skill skill, int score, int maxScore, String missionTitle) {
        if (student == null || skill == null) {
            return null;
        }
        double newScore = toScore0to100(score, maxScore);
        int newLevel = computeLevel(newScore);

        SkillUpsert upsert = upsertStudentSkill(student, skill, newScore, newLevel);
        writeSkillHistory(student, skill, upsert, newScore, newLevel,
                "Updated automatically after completing mission: " + missionTitle);
        return new SkillUpdateOutDTO(skill.getName(), upsert.previousScore(), newScore);
    }

    private double toScore0to100(int score, int maxScore) {
        double percentage = maxScore > 0 ? (double) score / maxScore : 0.0;
        return Math.round(percentage * 100.0);
    }

    /**
     * Duplicate-safe upsert of the (student, skill) StudentSkill row: creates one if none, updates the canonical
     * (first) row, and deletes any pre-existing duplicate rows via repository.delete (the new unique constraint
     * prevents future duplicates). Returns the saved row + its previous score/level for the history entry.
     */
    private SkillUpsert upsertStudentSkill(Student student, Skill skill, double newScore, int newLevel) {
        List<StudentSkill> rows = studentSkillRepository
                .findStudentSkillsByStudentIdAndSkillId(student.getId(), skill.getId());
        StudentSkill canonical;
        Double previousScore = null;
        Integer previousLevel = null;
        if (rows.isEmpty()) {
            canonical = new StudentSkill();
            canonical.setStudent(student);
            canonical.setSkill(skill);
        } else {
            canonical = rows.get(0);
            previousScore = canonical.getScore();
            previousLevel = canonical.getLevel();
            for (int i = 1; i < rows.size(); i++) {
                studentSkillRepository.delete(rows.get(i)); // collapse pre-existing duplicates (no deleteById)
            }
        }
        canonical.setScore(newScore);
        canonical.setLevel(newLevel);
        StudentSkill saved = studentSkillRepository.save(canonical);
        return new SkillUpsert(saved, previousScore, previousLevel);
    }

    private void writeSkillHistory(Student student, Skill skill, SkillUpsert upsert,
                                   double newScore, int newLevel, String reason) {
        SkillProgressHistory history = new SkillProgressHistory();
        history.setPreviousScore(upsert.previousScore());
        history.setNewScore(newScore);
        history.setPreviousLevel(upsert.previousLevel());
        history.setNewLevel(newLevel);
        history.setReason(reason);
        history.setChangedAt(LocalDateTime.now());
        history.setStudent(student);
        history.setSkill(skill);
        history.setStudentSkill(upsert.studentSkill());
        skillProgressHistoryRepository.save(history);
    }

    private record SkillUpsert(StudentSkill studentSkill, Double previousScore, Integer previousLevel) {
    }

    private int computeLevel(double score0to100) {
        if (score0to100 >= 90) return 5;
        if (score0to100 >= 75) return 4;
        if (score0to100 >= 50) return 3;
        if (score0to100 >= 25) return 2;
        return 1;
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(SkillProgressHistory skillProgressHistory, SkillProgressHistoryInDTO dto) {
        Student student = studentRepository.findStudentById(dto.getStudentId());
        if (student == null) {
            throw new ApiException("Student with id " + dto.getStudentId() + " not found");
        }
        skillProgressHistory.setStudent(student);

        Skill skill = skillRepository.findSkillById(dto.getSkillId());
        if (skill == null) {
            throw new ApiException("Skill with id " + dto.getSkillId() + " not found");
        }
        skillProgressHistory.setSkill(skill);

        if (dto.getStudentSkillId() != null) {
            StudentSkill studentSkill = studentSkillRepository.findStudentSkillById(dto.getStudentSkillId());
            if (studentSkill == null) {
                throw new ApiException("StudentSkill with id " + dto.getStudentSkillId() + " not found");
            }
            skillProgressHistory.setStudentSkill(studentSkill);
        } else {
            skillProgressHistory.setStudentSkill(null);
        }
    }

    private SkillProgressHistoryOutDTO toOut(SkillProgressHistory skillProgressHistory) {
        SkillProgressHistoryOutDTO out = modelMapper.map(skillProgressHistory, SkillProgressHistoryOutDTO.class);
        if (skillProgressHistory.getStudent() != null) {
            out.setStudentId(skillProgressHistory.getStudent().getId());
            out.setStudentName(skillProgressHistory.getStudent().getFullName());
        }
        if (skillProgressHistory.getSkill() != null) {
            out.setSkillId(skillProgressHistory.getSkill().getId());
            out.setSkillName(skillProgressHistory.getSkill().getName());
        }
        if (skillProgressHistory.getStudentSkill() != null) {
            out.setStudentSkillId(skillProgressHistory.getStudentSkill().getId());
        }
        return out;
    }
}
