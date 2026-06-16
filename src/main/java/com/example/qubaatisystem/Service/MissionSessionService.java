package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.MissionSessionInDTO;
import com.example.qubaatisystem.DTO.Out.MissionSessionOutDTO;
import com.example.qubaatisystem.Model.Mission;
import com.example.qubaatisystem.Model.MissionSession;
import com.example.qubaatisystem.Repository.MissionRepository;
import com.example.qubaatisystem.Repository.MissionSessionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionSessionService {

    private final MissionSessionRepository missionSessionRepository;
    private final MissionRepository missionRepository;
    private final ModelMapper modelMapper;

    public List<MissionSessionOutDTO> getAll() {
        return missionSessionRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public MissionSessionOutDTO getById(Integer id) {
        List<MissionSession> missionSessions = missionSessionRepository.findMissionSessionById(id);
        if (missionSessions.isEmpty()) {
            throw new ApiException("MissionSession with id " + id + " not found");
        }
        return toOut(missionSessions.get(0));
    }

    public void create(MissionSessionInDTO dto) {
        MissionSession missionSession = modelMapper.map(dto, MissionSession.class);

        applyRelationships(missionSession, dto);

        missionSessionRepository.save(missionSession);
    }

    public void update(Integer id, MissionSessionInDTO dto) {
        List<MissionSession> missionSessions = missionSessionRepository.findMissionSessionById(id);
        if (missionSessions.isEmpty()) {
            throw new ApiException("MissionSession with id " + id + " not found");
        }
        MissionSession missionSession = missionSessions.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        missionSession.setMission(null);
        modelMapper.map(dto, missionSession);

        applyRelationships(missionSession, dto);

        missionSessionRepository.save(missionSession);
    }

    public void delete(Integer id) {
        List<MissionSession> missionSessions = missionSessionRepository.findMissionSessionById(id);
        if (missionSessions.isEmpty()) {
            throw new ApiException("MissionSession with id " + id + " not found");
        }
        missionSessionRepository.delete(missionSessions.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(MissionSession missionSession, MissionSessionInDTO dto) {
        List<Mission> missions = missionRepository.findMissionById(dto.getMissionId());
        if (missions.isEmpty()) {
            throw new ApiException("Mission with id " + dto.getMissionId() + " not found");
        }
        missionSession.setMission(missions.get(0));
    }

    private MissionSessionOutDTO toOut(MissionSession missionSession) {
        MissionSessionOutDTO out = modelMapper.map(missionSession, MissionSessionOutDTO.class);
        return out;
    }
}
