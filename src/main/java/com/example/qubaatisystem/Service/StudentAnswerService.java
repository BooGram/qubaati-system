package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.StudentAnswerInDTO;
import com.example.qubaatisystem.DTO.Out.StudentAnswerOutDTO;
import com.example.qubaatisystem.Model.ActivitySubmission;
import com.example.qubaatisystem.Model.Question;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.StudentAnswer;
import com.example.qubaatisystem.Repository.ActivitySubmissionRepository;
import com.example.qubaatisystem.Repository.QuestionRepository;
import com.example.qubaatisystem.Repository.StudentAnswerRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAnswerService {

    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;
    private final StudentRepository studentRepository;
    private final ActivitySubmissionRepository activitySubmissionRepository;
    private final ModelMapper modelMapper;

    public List<StudentAnswerOutDTO> getAll() {
        return studentAnswerRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public StudentAnswerOutDTO getById(Integer id) {
        StudentAnswer studentAnswer = studentAnswerRepository.findStudentAnswerById(id);
        if (studentAnswer == null) {
            throw new ApiException("StudentAnswer with id " + id + " not found");
        }
        return toOut(studentAnswer);
    }

    public void create(StudentAnswerInDTO dto) {
        StudentAnswer studentAnswer = modelMapper.map(dto, StudentAnswer.class);

        applyRelationships(studentAnswer, dto);

        studentAnswerRepository.save(studentAnswer);
    }

    public void update(Integer id, StudentAnswerInDTO dto) {
        StudentAnswer studentAnswer = studentAnswerRepository.findStudentAnswerById(id);
        if (studentAnswer == null) {
            throw new ApiException("StudentAnswer with id " + id + " not found");
        }

        // Clear owning relations first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        studentAnswer.setQuestion(null);
        studentAnswer.setStudent(null);
        studentAnswer.setActivitySubmission(null);
        modelMapper.map(dto, studentAnswer);

        applyRelationships(studentAnswer, dto);

        studentAnswerRepository.save(studentAnswer);
    }

    public void delete(Integer id) {
        StudentAnswer studentAnswer = studentAnswerRepository.findStudentAnswerById(id);
        if (studentAnswer == null) {
            throw new ApiException("StudentAnswer with id " + id + " not found");
        }
        studentAnswerRepository.delete(studentAnswer);
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(StudentAnswer studentAnswer, StudentAnswerInDTO dto) {
        Question question = questionRepository.findQuestionById(dto.getQuestionId());
        if (question == null) {
            throw new ApiException("Question with id " + dto.getQuestionId() + " not found");
        }
        studentAnswer.setQuestion(question);

        Student student = studentRepository.findStudentById(dto.getStudentId());
        if (student == null) {
            throw new ApiException("Student with id " + dto.getStudentId() + " not found");
        }
        studentAnswer.setStudent(student);

        ActivitySubmission submission = activitySubmissionRepository.findActivitySubmissionById(dto.getActivitySubmissionId());
        if (submission == null) {
            throw new ApiException("ActivitySubmission with id " + dto.getActivitySubmissionId() + " not found");
        }
        studentAnswer.setActivitySubmission(submission);
    }

    private StudentAnswerOutDTO toOut(StudentAnswer studentAnswer) {
        StudentAnswerOutDTO out = modelMapper.map(studentAnswer, StudentAnswerOutDTO.class);
        if (studentAnswer.getQuestion() != null) {
            out.setQuestionId(studentAnswer.getQuestion().getId());
        }
        if (studentAnswer.getStudent() != null) {
            out.setStudentId(studentAnswer.getStudent().getId());
            out.setStudentName(studentAnswer.getStudent().getFullName());
        }
        if (studentAnswer.getActivitySubmission() != null) {
            out.setActivitySubmissionId(studentAnswer.getActivitySubmission().getId());
        }
        return out;
    }
}
