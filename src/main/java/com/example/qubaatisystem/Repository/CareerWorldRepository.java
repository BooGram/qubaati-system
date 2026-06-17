package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.CareerWorld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerWorldRepository extends JpaRepository<CareerWorld, Integer> {

    List<CareerWorld> findCareerWorldById(Integer id);
}
