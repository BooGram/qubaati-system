package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.SkillProgressHistoryInDTO;
import com.example.qubaatisystem.DTO.Out.SkillProgressHistoryOutDTO;
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
        SkillProgressHistory skillProgressHistory = modelMapper.map(dto, SkillProgressHistory.class);

        applyRelationships(skillProgressHistory, dto);

        skillProgressHistoryRepository.save(skillProgressHistory);
    }

    public void update(Integer id, SkillProgressHistoryInDTO dto) {
        SkillProgressHistory skillProgressHistory = skillProgressHistoryRepository.findSkillProgressHistoryById(id);
        if (skillProgressHistory == null) {
            throw new ApiException("SkillProgressHistory with id " + id + " not found");
        }

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        skillProgressHistory.setStudent(null);
        skillProgressHistory.setSkill(null);
        skillProgressHistory.setStudentSkill(null);
        modelMapper.map(dto, skillProgressHistory);

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
