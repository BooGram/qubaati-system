package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.QuestionInDTO;
import com.example.qubaatisystem.DTO.Out.QuestionOutDTO;
import com.example.qubaatisystem.Model.Activity;
import com.example.qubaatisystem.Model.Question;
import com.example.qubaatisystem.Repository.ActivityRepository;
import com.example.qubaatisystem.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final ActivityRepository activityRepository;
    private final ModelMapper modelMapper;

    public List<QuestionOutDTO> getAll() {
        return questionRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public QuestionOutDTO getById(Integer id) {
        List<Question> questions = questionRepository.findQuestionById(id);
        if (questions.isEmpty()) {
            throw new ApiException("Question with id " + id + " not found");
        }
        return toOut(questions.get(0));
    }

    public void create(QuestionInDTO dto) {
        Question question = modelMapper.map(dto, Question.class);

        applyRelationships(question, dto);

        questionRepository.save(question);
    }

    public void update(Integer id, QuestionInDTO dto) {
        List<Question> questions = questionRepository.findQuestionById(id);
        if (questions.isEmpty()) {
            throw new ApiException("Question with id " + id + " not found");
        }
        Question question = questions.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        question.setActivity(null);
        modelMapper.map(dto, question);

        applyRelationships(question, dto);

        questionRepository.save(question);
    }

    public void delete(Integer id) {
        List<Question> questions = questionRepository.findQuestionById(id);
        if (questions.isEmpty()) {
            throw new ApiException("Question with id " + id + " not found");
        }
        questionRepository.delete(questions.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(Question question, QuestionInDTO dto) {
        List<Activity> activities = activityRepository.findActivityById(dto.getActivityId());
        if (activities.isEmpty()) {
            throw new ApiException("Activity with id " + dto.getActivityId() + " not found");
        }
        question.setActivity(activities.get(0));
    }

    private QuestionOutDTO toOut(Question question) {
        return modelMapper.map(question, QuestionOutDTO.class);
    }
}
