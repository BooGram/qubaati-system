package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.ActivityAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivityAssignmentRepository extends JpaRepository<ActivityAssignment, Integer> {

    ActivityAssignment findActivityAssignmentById(Integer id);
}
