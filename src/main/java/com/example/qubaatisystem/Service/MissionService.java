package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.AiMissionChoiceDTO;
import com.example.qubaatisystem.DTO.In.AiMissionDTO;
import com.example.qubaatisystem.DTO.In.AiMissionSkillDTO;
import com.example.qubaatisystem.DTO.In.MissionInDTO;
import com.example.qubaatisystem.DTO.Out.MissionChoiceOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionOutDTO;
import com.example.qubaatisystem.Model.CareerWorld;
import com.example.qubaatisystem.Model.Mission;
import com.example.qubaatisystem.Model.MissionChoice;
import com.example.qubaatisystem.Model.Skill;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Repository.CareerWorldRepository;
import com.example.qubaatisystem.Repository.MissionChoiceRepository;
import com.example.qubaatisystem.Repository.MissionRepository;
import com.example.qubaatisystem.Repository.SkillRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MissionService {

    private final MissionRepository missionRepository;
    private final MissionChoiceRepository missionChoiceRepository;
    private final CareerWorldRepository careerWorldRepository;
    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AiService aiService;

    //BASIC CRUD
    public List<MissionOutDTO> getAll() {
        List<Mission> missions = missionRepository.findAll();
        List<MissionOutDTO> missionOutDTOS = new ArrayList<>();

        for (Mission mission : missions) {
            missionOutDTOS.add(toOut(mission));
        }

        return missionOutDTOS;
    }

    public MissionOutDTO getById(Integer id) {
        Mission mission = checkMission(id);
        return toOut(mission);
    }

    public void create(MissionInDTO dto) {
        Mission mission = modelMapper.map(dto, Mission.class);
        CareerWorld careerWorld = checkCareerWorld(dto.getCareerWorldId());
        mission.setCareerWorld(careerWorld);
        mission.setId(null);
        missionRepository.save(mission);
    }

    public void update(Integer id, MissionInDTO dto) {
        Mission oldMission = checkMission(id);

        oldMission.setCareerWorld(null);
        modelMapper.map(dto, oldMission);

        CareerWorld careerWorld = checkCareerWorld(dto.getCareerWorldId());
        oldMission.setCareerWorld(careerWorld);
        missionRepository.save(oldMission);
    }

    public void delete(Integer id) {
        Mission mission = checkMission(id);
        missionRepository.delete(mission);
    }

    //EXTRA ENDPOINTS
    public List<MissionOutDTO> getAvailableMissions(Integer studentId) {
        checkStudent(studentId);

        List<Mission> missions = missionRepository.findAll();
        List<MissionOutDTO> availableMissions = new ArrayList<>();

        for (Mission mission : missions) {
            availableMissions.add(toOut(mission));
        }

        if (availableMissions.isEmpty()) {
            throw new ApiException("No available missions found");
        }

        return availableMissions;
    }

    @Transactional
    public MissionOutDTO generateMissionForStudent(Integer studentId, Integer careerWorldId) {
        Student student = checkStudent(studentId);
        CareerWorld careerWorld = checkCareerWorld(careerWorldId);

        String aiMissionJson = aiService.generateMissionJson(student, careerWorld);
        //convert the mission to dto
        AiMissionDTO aiMissionDTO = parseAiMissionJson(aiMissionJson);
        //add skill
        Skill skill = getOrCreateSkill(aiMissionDTO.getSkill());
        //build
        Mission mission = buildMissionFromAi(aiMissionDTO, skill, careerWorld, student);
        missionRepository.save(mission);
        saveMissionChoices(aiMissionDTO, mission);

        return toOut(mission);
    }

    //HELPER METHODS
    private Student checkStudent(Integer id) {
        Student student = studentRepository.findStudentById(id);
        if (student == null) {
            throw new ApiException("Student not found");
        }
        return student;
    }

    private Mission checkMission(Integer id) {
        Mission mission = missionRepository.findMissionById(id);
        if (mission == null) {
            throw new ApiException("Mission not found");
        }
        return mission;
    }

    private CareerWorld checkCareerWorld(Integer id) {
        CareerWorld careerWorld = careerWorldRepository.findCareerWorldById(id);
        if (careerWorld == null) {
            throw new ApiException("CareerWorld not found");
        }
        return careerWorld;
    }


    //generateMissionForStudent helpers
    private AiMissionDTO parseAiMissionJson(String aiMissionJson) {
        try {
            AiMissionDTO aiMissionDTO = objectMapper.readValue(aiMissionJson, AiMissionDTO.class);
            validateAiMission(aiMissionDTO);
            return aiMissionDTO;
        } catch (JsonProcessingException exception) {
            throw new ApiException("Invalid AI mission JSON");
        }
    }

    private void validateAiMission(AiMissionDTO aiMissionDTO) {
        if (aiMissionDTO == null
                || aiMissionDTO.getTitle() == null || aiMissionDTO.getTitle().isBlank()
                || aiMissionDTO.getScenario() == null || aiMissionDTO.getScenario().isBlank()
                || aiMissionDTO.getChoices() == null || aiMissionDTO.getChoices().size() != 3
                || aiMissionDTO.getSkill() == null
                || aiMissionDTO.getDifficulty() == null
                || aiMissionDTO.getEstimatedMinutes() == null || aiMissionDTO.getEstimatedMinutes() <= 0
                || aiMissionDTO.getMaxScore() == null || aiMissionDTO.getMaxScore() <= 0) {
            throw new ApiException("Invalid AI mission JSON");
        }

        for (AiMissionChoiceDTO choice : aiMissionDTO.getChoices()) {
            if (choice == null
                    || choice.getKey() == null || choice.getKey().isBlank()
                    || choice.getText() == null || choice.getText().isBlank()
                    || choice.getScoreImpact() == null || choice.getScoreImpact() <= 0) {
                throw new ApiException("Invalid AI mission JSON");
            }
        }
    }

    private Skill getOrCreateSkill(AiMissionSkillDTO aiMissionSkillDTO) {
        if (aiMissionSkillDTO == null || aiMissionSkillDTO.getName() == null || aiMissionSkillDTO.getName().isBlank()) {
            throw new ApiException("Invalid AI skill");
        }

        Skill skill = skillRepository.findSkillByNameIgnoreCase(aiMissionSkillDTO.getName());
        if (skill != null) {
            return skill;
        }

        skill = new Skill();
        skill.setName(aiMissionSkillDTO.getName());
        skill.setDescription(aiMissionSkillDTO.getDescription());

        if (aiMissionSkillDTO.getSkillType() == null) {
            throw new ApiException("Invalid AI skill");
        }

        skill.setSkillType(aiMissionSkillDTO.getSkillType());

        skillRepository.save(skill);
        return skill;
    }

    private Mission buildMissionFromAi(AiMissionDTO aiMissionDTO, Skill skill, CareerWorld careerWorld, Student student) {
        Mission mission = new Mission();

        mission.setTitle(aiMissionDTO.getTitle());
        mission.setScenario(aiMissionDTO.getScenario());
        mission.setSkill(skill);
        mission.setSkillType(skill.getSkillType());
        mission.setDifficulty(aiMissionDTO.getDifficulty());
        mission.setEstimatedMinutes(aiMissionDTO.getEstimatedMinutes());
        mission.setMaxScore(aiMissionDTO.getMaxScore());
        mission.setCareerWorld(careerWorld);
        mission.setGeneratedForStudent(student);

        return mission;
    }

    private void saveMissionChoices(AiMissionDTO aiMissionDTO, Mission mission) {
        Set<MissionChoice> missionChoices = new HashSet<>();

        for (AiMissionChoiceDTO aiMissionChoiceDTO : aiMissionDTO.getChoices()) {
            MissionChoice missionChoice = new MissionChoice();

            missionChoice.setChoiceKey(aiMissionChoiceDTO.getKey());
            missionChoice.setText(aiMissionChoiceDTO.getText());
            missionChoice.setScoreImpact(aiMissionChoiceDTO.getScoreImpact());
            missionChoice.setMission(mission);

            missionChoiceRepository.save(missionChoice);
            missionChoices.add(missionChoice);
        }

        mission.setMissionChoices(missionChoices);
    }

    private MissionOutDTO toOut(Mission mission) {
        MissionOutDTO out = modelMapper.map(mission, MissionOutDTO.class);

        if (mission.getCareerWorld() != null) {
            out.setCareerWorldId(mission.getCareerWorld().getId());
            out.setCareerWorldTitle(mission.getCareerWorld().getTitle());
        }

        if (mission.getGeneratedForStudent() != null) {
            out.setGeneratedForStudentId(mission.getGeneratedForStudent().getId());
            out.setGeneratedForStudentName(mission.getGeneratedForStudent().getFullName());
        }

        if (mission.getSkill() != null) {
            out.setSkillId(mission.getSkill().getId());
            out.setSkillName(mission.getSkill().getName());
        }

        if (mission.getMissionChoices() != null) {
            List<MissionChoiceOutDTO> choices = new ArrayList<>();

            for (MissionChoice missionChoice : mission.getMissionChoices()) {
                MissionChoiceOutDTO choiceOutDTO = new MissionChoiceOutDTO();

                choiceOutDTO.setId(missionChoice.getId());
                choiceOutDTO.setChoiceKey(missionChoice.getChoiceKey());
                choiceOutDTO.setText(missionChoice.getText());
                choiceOutDTO.setScoreImpact(missionChoice.getScoreImpact());

                choices.add(choiceOutDTO);
            }

            out.setChoices(choices);
        }

        return out;
    }
}
