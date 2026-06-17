package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.ActivitySubmission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ActivitySubmissionRepository extends JpaRepository<ActivitySubmission, Integer> {

    ActivitySubmission findActivitySubmissionById(Integer id);
}
