package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.ActivitySubmissionInDTO;
import com.example.qubaatisystem.DTO.Out.ActivitySubmissionOutDTO;
import com.example.qubaatisystem.Model.ActivityAssignment;
import com.example.qubaatisystem.Model.ActivitySubmission;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Repository.ActivityAssignmentRepository;
import com.example.qubaatisystem.Repository.ActivitySubmissionRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivitySubmissionService {

    private final ActivitySubmissionRepository activitySubmissionRepository;
    private final ActivityAssignmentRepository activityAssignmentRepository;
    private final StudentRepository studentRepository;
    private final ModelMapper modelMapper;

    public List<ActivitySubmissionOutDTO> getAll() {
        return activitySubmissionRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public ActivitySubmissionOutDTO getById(Integer id) {
        ActivitySubmission activitySubmission = activitySubmissionRepository.findActivitySubmissionById(id);
        if (activitySubmission == null) {
            throw new ApiException("ActivitySubmission with id " + id + " not found");
        }
        return toOut(activitySubmission);
    }

    public void create(ActivitySubmissionInDTO dto) {
        ActivitySubmission activitySubmission = modelMapper.map(dto, ActivitySubmission.class);

        applyRelationships(activitySubmission, dto);

        activitySubmissionRepository.save(activitySubmission);
    }

    public void update(Integer id, ActivitySubmissionInDTO dto) {
        ActivitySubmission activitySubmission = activitySubmissionRepository.findActivitySubmissionById(id);
        if (activitySubmission == null) {
            throw new ApiException("ActivitySubmission with id " + id + " not found");
        }

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        activitySubmission.setActivityAssignment(null);
        activitySubmission.setStudent(null);
        modelMapper.map(dto, activitySubmission);

        applyRelationships(activitySubmission, dto);

        activitySubmissionRepository.save(activitySubmission);
    }

    public void delete(Integer id) {
        ActivitySubmission activitySubmission = activitySubmissionRepository.findActivitySubmissionById(id);
        if (activitySubmission == null) {
            throw new ApiException("ActivitySubmission with id " + id + " not found");
        }
        activitySubmissionRepository.delete(activitySubmission);
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(ActivitySubmission activitySubmission, ActivitySubmissionInDTO dto) {
        ActivityAssignment activityAssignment = activityAssignmentRepository.findActivityAssignmentById(dto.getActivityAssignmentId());
        if (activityAssignment == null) {
            throw new ApiException("ActivityAssignment with id " + dto.getActivityAssignmentId() + " not found");
        }
        activitySubmission.setActivityAssignment(activityAssignment);

        Student student = studentRepository.findStudentById(dto.getStudentId());
        if (student == null) {
            throw new ApiException("Student with id " + dto.getStudentId() + " not found");
        }
        activitySubmission.setStudent(student);
    }

    private ActivitySubmissionOutDTO toOut(ActivitySubmission activitySubmission) {
        ActivitySubmissionOutDTO out = modelMapper.map(activitySubmission, ActivitySubmissionOutDTO.class);
        // Manually set relation-derived fields (activity comes through the assignment).
        if (activitySubmission.getActivityAssignment() != null) {
            out.setActivityAssignmentId(activitySubmission.getActivityAssignment().getId());
            if (activitySubmission.getActivityAssignment().getActivity() != null) {
                out.setActivityId(activitySubmission.getActivityAssignment().getActivity().getId());
                out.setActivityTitle(activitySubmission.getActivityAssignment().getActivity().getTitle());
            }
        }
        if (activitySubmission.getStudent() != null) {
            out.setStudentId(activitySubmission.getStudent().getId());
            out.setStudentName(activitySubmission.getStudent().getFullName());
        }
        return out;
    }
}
