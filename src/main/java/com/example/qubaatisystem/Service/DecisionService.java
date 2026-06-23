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
        Decision decision = decisionRepository.findDecisionById(id);
        if (decision == null) {
            throw new ApiException("Decision with id " + id + " not found");
        }
        return toOut(decision);
    }

    // Generic mutation of Decision is DISABLED: it would let a client inject/alter a mission decision outside the
    // guarded flow (which validates the choice belongs to the current step and computes the internal scoring).
    // Read endpoints stay.
    public void create(DecisionInDTO decisionInDTO) {
        throw new ApiException("Direct Decision creation is disabled. Submit decisions via "
                + "POST /api/v1/mission-sessions/{sessionId}/decisions.");
    }

    public void update(Integer id, DecisionInDTO decisionInDTO) {
        throw new ApiException("Direct Decision update is disabled (it would bypass mission step validation and scoring).");
    }

    public void delete(Integer id) {
        throw new ApiException("Direct Decision deletion is disabled (decisions are an immutable record of the mission attempt).");
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(Decision decision, DecisionInDTO dto) {
        MissionSession missionSession = missionSessionRepository.findMissionSessionById(dto.getMissionSessionId());
        if (missionSession == null) {
            throw new ApiException("MissionSession with id " + dto.getMissionSessionId() + " not found");
        }
        decision.setMissionSession(missionSession);
    }

    private DecisionOutDTO toOut(Decision decision) {
        DecisionOutDTO out = modelMapper.map(decision, DecisionOutDTO.class);
        // No derived display fields for this model (ModelMapper auto-maps missionSessionId <- missionSession.id).
        return out;
    }
}
