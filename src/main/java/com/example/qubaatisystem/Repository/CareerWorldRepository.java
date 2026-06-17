package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.CareerWorld;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CareerWorldRepository extends JpaRepository<CareerWorld, Integer> {

    CareerWorld findCareerWorldById(Integer id);
}
