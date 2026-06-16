package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.StudentAnswerInDTO;
import com.example.qubaatisystem.DTO.Out.StudentAnswerOutDTO;
import com.example.qubaatisystem.Model.Question;
import com.example.qubaatisystem.Model.StudentAnswer;
import com.example.qubaatisystem.Repository.QuestionRepository;
import com.example.qubaatisystem.Repository.StudentAnswerRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentAnswerService {

    private final StudentAnswerRepository studentAnswerRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    public List<StudentAnswerOutDTO> getAll() {
        return studentAnswerRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public StudentAnswerOutDTO getById(Integer id) {
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findStudentAnswerById(id);
        if (studentAnswers.isEmpty()) {
            throw new ApiException("StudentAnswer with id " + id + " not found");
        }
        return toOut(studentAnswers.get(0));
    }

    public void create(StudentAnswerInDTO studentAnswerInDTO) {
        StudentAnswer studentAnswer = modelMapper.map(studentAnswerInDTO, StudentAnswer.class);

        applyRelationships(studentAnswer, studentAnswerInDTO);

        studentAnswerRepository.save(studentAnswer);
    }

    public void update(Integer id, StudentAnswerInDTO studentAnswerInDTO) {
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findStudentAnswerById(id);
        if (studentAnswers.isEmpty()) {
            throw new ApiException("StudentAnswer with id " + id + " not found");
        }
        StudentAnswer studentAnswer = studentAnswers.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        studentAnswer.setQuestion(null);
        modelMapper.map(studentAnswerInDTO, studentAnswer);

        applyRelationships(studentAnswer, studentAnswerInDTO);

        studentAnswerRepository.save(studentAnswer);
    }

    public void delete(Integer id) {
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findStudentAnswerById(id);
        if (studentAnswers.isEmpty()) {
            throw new ApiException("StudentAnswer with id " + id + " not found");
        }
        studentAnswerRepository.delete(studentAnswers.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(StudentAnswer studentAnswer, StudentAnswerInDTO dto) {
        List<Question> questions = questionRepository.findQuestionById(dto.getQuestionId());
        if (questions.isEmpty()) {
            throw new ApiException("Question with id " + dto.getQuestionId() + " not found");
        }
        studentAnswer.setQuestion(questions.get(0));
    }

    private StudentAnswerOutDTO toOut(StudentAnswer studentAnswer) {
        StudentAnswerOutDTO out = modelMapper.map(studentAnswer, StudentAnswerOutDTO.class);
        // No derived display fields to set manually (questionId is auto-flattened by ModelMapper).
        return out;
    }
}
