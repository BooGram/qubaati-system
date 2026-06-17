package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Integer> {

    StudentAnswer findStudentAnswerById(Integer id);
}
