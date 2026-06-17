package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.StudentSkillInDTO;
import com.example.qubaatisystem.DTO.Out.StudentSkillOutDTO;
import com.example.qubaatisystem.Model.Skill;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentSkill;
import com.example.qubaatisystem.Repository.SkillRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.StudentSkillRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentSkillService {

    private final StudentSkillRepository studentSkillRepository;
    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    public List<StudentSkillOutDTO> getAll() {
        return studentSkillRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public StudentSkillOutDTO getById(Integer id) {
        List<StudentSkill> studentSkills = studentSkillRepository.findStudentSkillById(id);
        if (studentSkills.isEmpty()) {
            throw new ApiException("StudentSkill with id " + id + " not found");
        }
        return toOut(studentSkills.get(0));
    }

    public void create(StudentSkillInDTO dto) {
        StudentSkill studentSkill = modelMapper.map(dto, StudentSkill.class);

        applyRelationships(studentSkill, dto);

        studentSkillRepository.save(studentSkill);
    }

    public void update(Integer id, StudentSkillInDTO dto) {
        List<StudentSkill> studentSkills = studentSkillRepository.findStudentSkillById(id);
        if (studentSkills.isEmpty()) {
            throw new ApiException("StudentSkill with id " + id + " not found");
        }
        StudentSkill studentSkill = studentSkills.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        studentSkill.setStudent(null);
        studentSkill.setSkill(null);
        modelMapper.map(dto, studentSkill);

        applyRelationships(studentSkill, dto);

        studentSkillRepository.save(studentSkill);
    }

    public void delete(Integer id) {
        List<StudentSkill> studentSkills = studentSkillRepository.findStudentSkillById(id);
        if (studentSkills.isEmpty()) {
            throw new ApiException("StudentSkill with id " + id + " not found");
        }
        studentSkillRepository.delete(studentSkills.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(StudentSkill studentSkill, StudentSkillInDTO dto) {
        List<Student> students = studentRepository.findStudentById(dto.getStudentId());
        if (students.isEmpty()) {
            throw new ApiException("Student with id " + dto.getStudentId() + " not found");
        }
        studentSkill.setStudent(students.get(0));

        List<Skill> skills = skillRepository.findSkillById(dto.getSkillId());
        if (skills.isEmpty()) {
            throw new ApiException("Skill with id " + dto.getSkillId() + " not found");
        }
        studentSkill.setSkill(skills.get(0));
    }

    private StudentSkillOutDTO toOut(StudentSkill studentSkill) {
        StudentSkillOutDTO out = modelMapper.map(studentSkill, StudentSkillOutDTO.class);
        // No derived fields to set manually; ModelMapper auto-maps all OutDTO fields
        // (studentId <- student.id, skillId <- skill.id, skillName <- skill.name).
        return out;
    }
}
