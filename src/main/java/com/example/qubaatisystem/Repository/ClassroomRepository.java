package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {

    Classroom findClassroomById(Integer id);
}
