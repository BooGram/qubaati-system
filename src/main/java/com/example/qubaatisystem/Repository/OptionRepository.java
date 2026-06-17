package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Integer> {

    Option findOptionById(Integer id);
}
