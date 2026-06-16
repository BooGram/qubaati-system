package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.LearningStyleInDTO;
import com.example.qubaatisystem.DTO.Out.LearningStyleOutDTO;
import com.example.qubaatisystem.Model.LearningStyle;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Repository.LearningStyleRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LearningStyleService {

    private final LearningStyleRepository learningStyleRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public List<LearningStyleOutDTO> getAll() {
        return learningStyleRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public LearningStyleOutDTO getById(Integer id) {
        List<LearningStyle> list = learningStyleRepository.findLearningStyleById(id);
        if (list.isEmpty()) {
            throw new ApiException("LearningStyle with id " + id + " not found");
        }
        return toOut(list.get(0));
    }

    public void create(LearningStyleInDTO dto) {
        LearningStyle learningStyle = modelMapper.map(dto, LearningStyle.class);

        applyRelationships(learningStyle, dto);

        learningStyleRepository.save(learningStyle);
    }

    public void update(Integer id, LearningStyleInDTO dto) {
        List<LearningStyle> list = learningStyleRepository.findLearningStyleById(id);
        if (list.isEmpty()) {
            throw new ApiException("LearningStyle with id " + id + " not found");
        }
        LearningStyle learningStyle = list.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        learningStyle.setStudent(null);
        modelMapper.map(dto, learningStyle);

        applyRelationships(learningStyle, dto);

        learningStyleRepository.save(learningStyle);
    }

    public void delete(Integer id) {
        List<LearningStyle> list = learningStyleRepository.findLearningStyleById(id);
        if (list.isEmpty()) {
            throw new ApiException("LearningStyle with id " + id + " not found");
        }
        learningStyleRepository.delete(list.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(LearningStyle learningStyle, LearningStyleInDTO dto) {
        List<Student> students = studentRepository.findStudentById(dto.getStudentId());
        if (students.isEmpty()) {
            throw new ApiException("Student with id " + dto.getStudentId() + " not found");
        }
        learningStyle.setStudent(students.get(0));
    }

    private LearningStyleOutDTO toOut(LearningStyle learningStyle) {
        return modelMapper.map(learningStyle, LearningStyleOutDTO.class);
    }
}
