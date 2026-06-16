package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.ClassroomInDTO;
import com.example.qubaatisystem.DTO.Out.ClassroomOutDTO;
import com.example.qubaatisystem.Model.Classroom;
import com.example.qubaatisystem.Model.Teacher;
import com.example.qubaatisystem.Repository.ClassroomRepository;
import com.example.qubaatisystem.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassroomService {

    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    public List<ClassroomOutDTO> getAll() {
        return classroomRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public ClassroomOutDTO getById(Integer id) {
        List<Classroom> classrooms = classroomRepository.findClassroomById(id);
        if (classrooms.isEmpty()) {
            throw new ApiException("Classroom with id " + id + " not found");
        }
        return toOut(classrooms.get(0));
    }

    public void create(ClassroomInDTO classroomInDTO) {
        Classroom classroom = modelMapper.map(classroomInDTO, Classroom.class);

        applyRelationships(classroom, classroomInDTO);

        classroomRepository.save(classroom);
    }

    public void update(Integer id, ClassroomInDTO classroomInDTO) {
        List<Classroom> classrooms = classroomRepository.findClassroomById(id);
        if (classrooms.isEmpty()) {
            throw new ApiException("Classroom with id " + id + " not found");
        }
        Classroom classroom = classrooms.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        classroom.setTeacher(null);
        modelMapper.map(classroomInDTO, classroom);

        applyRelationships(classroom, classroomInDTO);

        classroomRepository.save(classroom);
    }

    public void delete(Integer id) {
        List<Classroom> classrooms = classroomRepository.findClassroomById(id);
        if (classrooms.isEmpty()) {
            throw new ApiException("Classroom with id " + id + " not found");
        }
        classroomRepository.delete(classrooms.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(Classroom classroom, ClassroomInDTO dto) {
        List<Teacher> teachers = teacherRepository.findTeacherById(dto.getTeacherId());
        if (teachers.isEmpty()) {
            throw new ApiException("Teacher with id " + dto.getTeacherId() + " not found");
        }
        classroom.setTeacher(teachers.get(0));
    }

    private ClassroomOutDTO toOut(Classroom classroom) {
        ClassroomOutDTO out = modelMapper.map(classroom, ClassroomOutDTO.class);
        // Derived display field ModelMapper cannot infer from field names (teacherName <- teacher.fullName).
        if (classroom.getTeacher() != null) {
            out.setTeacherName(classroom.getTeacher().getFullName());
        }
        return out;
    }
}
