package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.SkillInDTO;
import com.example.qubaatisystem.DTO.Out.SkillOutDTO;
import com.example.qubaatisystem.Model.Skill;
import com.example.qubaatisystem.Repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;

    public List<SkillOutDTO> getAll() {
        return skillRepository.findAll().stream().map(this::toOut).toList();
    }

    public SkillOutDTO getById(Integer id) {
        List<Skill> skills = skillRepository.findSkillById(id);
        if (skills.isEmpty()) {
            throw new ApiException("Skill with id " + id + " not found");
        }
        return toOut(skills.get(0));
    }

    public void create(SkillInDTO dto) {
        Skill skill = modelMapper.map(dto, Skill.class);

        skillRepository.save(skill);
    }

    public void update(Integer id, SkillInDTO dto) {
        List<Skill> skills = skillRepository.findSkillById(id);
        if (skills.isEmpty()) {
            throw new ApiException("Skill with id " + id + " not found");
        }
        Skill skill = skills.get(0);

        modelMapper.map(dto, skill);

        skillRepository.save(skill);
    }

    public void delete(Integer id) {
        List<Skill> skills = skillRepository.findSkillById(id);
        if (skills.isEmpty()) {
            throw new ApiException("Skill with id " + id + " not found");
        }
        skillRepository.delete(skills.get(0));
    }

    // ---------- helpers ----------

    private SkillOutDTO toOut(Skill skill) {
        return modelMapper.map(skill, SkillOutDTO.class);
    }
}
