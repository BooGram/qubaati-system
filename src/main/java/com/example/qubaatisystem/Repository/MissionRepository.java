package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Integer> {

    Mission findMissionById(Integer id);

    List<Mission> findMissionsByCareerWorldId(Integer careerWorldId);

    List<Mission> findMissionsByGeneratedForStudentIdAndCareerWorldId(Integer generatedForStudentId, Integer careerWorldId);
}
