package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.OptionInDTO;
import com.example.qubaatisystem.DTO.Out.OptionOutDTO;
import com.example.qubaatisystem.Model.Option;
import com.example.qubaatisystem.Model.Question;
import com.example.qubaatisystem.Repository.OptionRepository;
import com.example.qubaatisystem.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OptionService {

    private final OptionRepository optionRepository;
    private final QuestionRepository questionRepository;
    private final ModelMapper modelMapper;

    public List<OptionOutDTO> getAll() {
        return optionRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public OptionOutDTO getById(Integer id) {
        List<Option> options = optionRepository.findOptionById(id);
        if (options.isEmpty()) {
            throw new ApiException("Option with id " + id + " not found");
        }
        return toOut(options.get(0));
    }

    public void create(OptionInDTO optionInDTO) {
        Option option = modelMapper.map(optionInDTO, Option.class);

        applyRelationships(option, optionInDTO);

        optionRepository.save(option);
    }

    public void update(Integer id, OptionInDTO optionInDTO) {
        List<Option> options = optionRepository.findOptionById(id);
        if (options.isEmpty()) {
            throw new ApiException("Option with id " + id + " not found");
        }
        Option option = options.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        option.setQuestion(null);
        modelMapper.map(optionInDTO, option);

        applyRelationships(option, optionInDTO);

        optionRepository.save(option);
    }

    public void delete(Integer id) {
        List<Option> options = optionRepository.findOptionById(id);
        if (options.isEmpty()) {
            throw new ApiException("Option with id " + id + " not found");
        }
        optionRepository.delete(options.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(Option option, OptionInDTO dto) {
        List<Question> questions = questionRepository.findQuestionById(dto.getQuestionId());
        if (questions.isEmpty()) {
            throw new ApiException("Question with id " + dto.getQuestionId() + " not found");
        }
        option.setQuestion(questions.get(0));
    }

    private OptionOutDTO toOut(Option option) {
        OptionOutDTO out = modelMapper.map(option, OptionOutDTO.class);
        return out;
    }
}
