package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.DecisionInDTO;
import com.example.qubaatisystem.DTO.Out.DecisionOutDTO;
import com.example.qubaatisystem.Model.Decision;
import com.example.qubaatisystem.Model.MissionSession;
import com.example.qubaatisystem.Repository.DecisionRepository;
import com.example.qubaatisystem.Repository.MissionSessionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DecisionService {

    private final DecisionRepository decisionRepository;
    private final MissionSessionRepository missionSessionRepository;
    private final ModelMapper modelMapper;

    public List<DecisionOutDTO> getAll() {
        return decisionRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public DecisionOutDTO getById(Integer id) {
        List<Decision> decisions = decisionRepository.findDecisionById(id);
        if (decisions.isEmpty()) {
            throw new ApiException("Decision with id " + id + " not found");
        }
        return toOut(decisions.get(0));
    }

    public void create(DecisionInDTO decisionInDTO) {
        Decision decision = modelMapper.map(decisionInDTO, Decision.class);

        applyRelationships(decision, decisionInDTO);

        decisionRepository.save(decision);
    }

    public void update(Integer id, DecisionInDTO decisionInDTO) {
        List<Decision> decisions = decisionRepository.findDecisionById(id);
        if (decisions.isEmpty()) {
            throw new ApiException("Decision with id " + id + " not found");
        }
        Decision decision = decisions.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the id of the currently-managed related entity).
        decision.setMissionSession(null);
        modelMapper.map(decisionInDTO, decision);

        applyRelationships(decision, decisionInDTO);

        decisionRepository.save(decision);
    }

    public void delete(Integer id) {
        List<Decision> decisions = decisionRepository.findDecisionById(id);
        if (decisions.isEmpty()) {
            throw new ApiException("Decision with id " + id + " not found");
        }
        decisionRepository.delete(decisions.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(Decision decision, DecisionInDTO dto) {
        List<MissionSession> missionSessions = missionSessionRepository.findMissionSessionById(dto.getMissionSessionId());
        if (missionSessions.isEmpty()) {
            throw new ApiException("MissionSession with id " + dto.getMissionSessionId() + " not found");
        }
        decision.setMissionSession(missionSessions.get(0));
    }

    private DecisionOutDTO toOut(Decision decision) {
        DecisionOutDTO out = modelMapper.map(decision, DecisionOutDTO.class);
        // No derived display fields for this model (ModelMapper auto-maps missionSessionId <- missionSession.id).
        return out;
    }
}
