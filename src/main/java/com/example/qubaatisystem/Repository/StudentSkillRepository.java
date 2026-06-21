package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.StudentSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentSkillRepository extends JpaRepository<StudentSkill, Integer> {

    StudentSkill findStudentSkillById(Integer id);

    StudentSkill findStudentSkillByStudentIdAndSkillId(Integer studentId, Integer skillId);

    // Dedupe-safe list variant: returns ALL rows for a (student, skill) pair so the upsert can collapse any
    // pre-existing duplicates (the singular finder above would throw if duplicates exist).
    List<StudentSkill> findStudentSkillsByStudentIdAndSkillId(Integer studentId, Integer skillId);

    List<StudentSkill> findStudentSkillsByStudentId(Integer studentId);
}
