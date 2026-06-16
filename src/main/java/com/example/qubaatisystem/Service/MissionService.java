package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.MissionInDTO;
import com.example.qubaatisystem.DTO.Out.MissionOutDTO;
import com.example.qubaatisystem.Model.CareerWorld;
import com.example.qubaatisystem.Model.Mission;
import com.example.qubaatisystem.Repository.CareerWorldRepository;
import com.example.qubaatisystem.Repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final CareerWorldRepository careerWorldRepository;
    private final ModelMapper modelMapper;

    public List<MissionOutDTO> getAll() {
        return missionRepository.findAll()
                .stream()
                .map(this::toOut)
                .toList();
    }

    public MissionOutDTO getById(Integer id) {
        List<Mission> missions = missionRepository.findMissionById(id);
        if (missions.isEmpty()) {
            throw new ApiException("Mission with id " + id + " not found");
        }
        return toOut(missions.get(0));
    }

    public void create(MissionInDTO dto) {
        Mission mission = modelMapper.map(dto, Mission.class);

        applyRelationships(mission, dto);

        missionRepository.save(mission);
    }

    public void update(Integer id, MissionInDTO dto) {
        List<Mission> missions = missionRepository.findMissionById(id);
        if (missions.isEmpty()) {
            throw new ApiException("Mission with id " + id + " not found");
        }
        Mission mission = missions.get(0);

        // Clear relationships first so ModelMapper only copies scalar fields
        // (never mutates the ids of the currently-managed related entities).
        mission.setCareerWorld(null);
        modelMapper.map(dto, mission);

        applyRelationships(mission, dto);

        missionRepository.save(mission);
    }

    public void delete(Integer id) {
        List<Mission> missions = missionRepository.findMissionById(id);
        if (missions.isEmpty()) {
            throw new ApiException("Mission with id " + id + " not found");
        }
        missionRepository.delete(missions.get(0));
    }

    // ---------- helpers ----------

    // Relationship IDs from the input DTO are resolved manually (ModelMapper maps scalar fields only).
    private void applyRelationships(Mission mission, MissionInDTO dto) {
        List<CareerWorld> careerWorlds = careerWorldRepository.findCareerWorldById(dto.getCareerWorldId());
        if (careerWorlds.isEmpty()) {
            throw new ApiException("CareerWorld with id " + dto.getCareerWorldId() + " not found");
        }
        mission.setCareerWorld(careerWorlds.get(0));
    }

    private MissionOutDTO toOut(Mission mission) {
        MissionOutDTO out = modelMapper.map(mission, MissionOutDTO.class);
        // All OutDTO fields (including careerWorldId <- careerWorld.id and
        // careerWorldTitle <- careerWorld.title) are auto-mapped by ModelMapper.
        return out;
    }
}
