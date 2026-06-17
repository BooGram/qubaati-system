package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Mission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MissionRepository extends JpaRepository<Mission, Integer> {

    Mission findMissionById(Integer id);
}
