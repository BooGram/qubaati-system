package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ClassroomRepository extends JpaRepository<Classroom, Integer> {

    Classroom findClassroomById(Integer id);

    // Classrooms owned by a teacher (Student 1 teacher dashboard / listing).
    List<Classroom> findClassroomsByTeacherId(Integer teacherId);

    int countByTeacherId(Integer teacherId);
}
