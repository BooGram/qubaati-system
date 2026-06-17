package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.ActivityAssignmentInDTO;
import com.example.qubaatisystem.DTO.Out.ActivityAssignmentOutDTO;
import com.example.qubaatisystem.Model.Activity;
import com.example.qubaatisystem.Model.ActivityAssignment;
import com.example.qubaatisystem.Model.Classroom;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.Teacher;
import com.example.qubaatisystem.Repository.ActivityAssignmentRepository;
import com.example.qubaatisystem.Repository.ActivityRepository;
import com.example.qubaatisystem.Repository.ClassroomRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityAssignmentService {

    private final ActivityAssignmentRepository activityAssignmentRepository;
    private final ActivityRepository activityRepository;
    private final StudentRepository studentRepository;
    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final ModelMapper modelMapper;

    public List<ActivityAssignmentOutDTO> getAll() {
        return activityAssignmentRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public ActivityAssignmentOutDTO getById(Integer id) {
        ActivityAssignment activityAssignment = activityAssignmentRepository.findActivityAssignmentById(id);
        if (activityAssignment == null) {
            throw new ApiException("ActivityAssignment with id " + id + " not found");
        }
        return toOut(activityAssignment);
    }

    public void create(ActivityAssignmentInDTO dto) {
        ActivityAssignment activityAssignment = modelMapper.map(dto, ActivityAssignment.class);

        applyRelationships(activityAssignment, dto);

        activityAssignmentRepository.save(activityAssignment);
    }

    public void update(Integer id, ActivityAssignmentInDTO dto) {
        ActivityAssignment activityAssignment = activityAssignmentRepository.findActivityAssignmentById(id);
        if (activityAssignment == null) {
            throw new ApiException("ActivityAssignment with id " + id + " not found");
        }

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        activityAssignment.setActivity(null);
        activityAssignment.setStudent(null);
        activityAssignment.setClassroom(null);
        activityAssignment.setAssignedByTeacher(null);
        modelMapper.map(dto, activityAssignment);

        applyRelationships(activityAssignment, dto);

        activityAssignmentRepository.save(activityAssignment);
    }

    public void delete(Integer id) {
        ActivityAssignment activityAssignment = activityAssignmentRepository.findActivityAssignmentById(id);
        if (activityAssignment == null) {
            throw new ApiException("ActivityAssignment with id " + id + " not found");
        }
        activityAssignmentRepository.delete(activityAssignment);
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(ActivityAssignment activityAssignment, ActivityAssignmentInDTO dto) {
        Activity activity = activityRepository.findActivityById(dto.getActivityId());
        if (activity == null) {
            throw new ApiException("Activity with id " + dto.getActivityId() + " not found");
        }
        activityAssignment.setActivity(activity);

        Teacher teacher = teacherRepository.findTeacherById(dto.getAssignedByTeacherId());
        if (teacher == null) {
            throw new ApiException("Teacher with id " + dto.getAssignedByTeacherId() + " not found");
        }
        activityAssignment.setAssignedByTeacher(teacher);

        if (dto.getStudentId() != null) {
            Student student = studentRepository.findStudentById(dto.getStudentId());
            if (student == null) {
                throw new ApiException("Student with id " + dto.getStudentId() + " not found");
            }
            activityAssignment.setStudent(student);
        } else {
            activityAssignment.setStudent(null);
        }

        if (dto.getClassroomId() != null) {
            Classroom classroom = classroomRepository.findClassroomById(dto.getClassroomId());
            if (classroom == null) {
                throw new ApiException("Classroom with id " + dto.getClassroomId() + " not found");
            }
            activityAssignment.setClassroom(classroom);
        } else {
            activityAssignment.setClassroom(null);
        }
    }

    private ActivityAssignmentOutDTO toOut(ActivityAssignment e) {
        ActivityAssignmentOutDTO out = modelMapper.map(e, ActivityAssignmentOutDTO.class);
        if (e.getActivity() != null) {
            out.setActivityId(e.getActivity().getId());
            out.setActivityTitle(e.getActivity().getTitle());
        }
        if (e.getStudent() != null) {
            out.setStudentId(e.getStudent().getId());
            out.setStudentName(e.getStudent().getFullName());
        }
        if (e.getClassroom() != null) {
            out.setClassroomId(e.getClassroom().getId());
            out.setClassroomName(e.getClassroom().getName());
        }
        if (e.getAssignedByTeacher() != null) {
            out.setAssignedByTeacherId(e.getAssignedByTeacher().getId());
            out.setAssignedByTeacherName(e.getAssignedByTeacher().getFullName());
        }
        return out;
    }
}
