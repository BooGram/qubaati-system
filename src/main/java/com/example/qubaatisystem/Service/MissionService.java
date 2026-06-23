package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.AiMissionChoiceDTO;
import com.example.qubaatisystem.DTO.In.AiMissionDTO;
import com.example.qubaatisystem.DTO.In.AiMissionSkillDTO;
import com.example.qubaatisystem.DTO.In.AiMissionStepChoiceDTO;
import com.example.qubaatisystem.DTO.In.AiMissionStepDTO;
import com.example.qubaatisystem.DTO.In.MissionInDTO;
import com.example.qubaatisystem.DTO.In.MissionStepBatchInDTO;
import com.example.qubaatisystem.DTO.In.MissionStepBatchItemInDTO;
import com.example.qubaatisystem.DTO.In.MissionStepChoiceBatchInDTO;
import com.example.qubaatisystem.DTO.Out.AvailableMissionOutDTO;
import com.example.qubaatisystem.DTO.Out.AvailableMissionsOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionChoiceOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionStepAdminOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionStepChoiceAdminOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionStepsAdminOutDTO;
import com.example.qubaatisystem.Enum.DifficultyLevel;
import com.example.qubaatisystem.Enum.MissionSessionStatus;
import com.example.qubaatisystem.Enum.MissionSource;
import com.example.qubaatisystem.Enum.SkillType;
import com.example.qubaatisystem.Model.CareerWorld;
import com.example.qubaatisystem.Model.Mission;
import com.example.qubaatisystem.Model.MissionChoice;
import com.example.qubaatisystem.Model.MissionSession;
import com.example.qubaatisystem.Model.MissionStep;
import com.example.qubaatisystem.Model.Skill;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Repository.CareerWorldRepository;
import com.example.qubaatisystem.Repository.MissionChoiceRepository;
import com.example.qubaatisystem.Repository.MissionRepository;
import com.example.qubaatisystem.Repository.MissionSessionRepository;
import com.example.qubaatisystem.Repository.MissionStepRepository;
import com.example.qubaatisystem.Repository.SkillRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MissionService {

    private static final int UNLOCK_THRESHOLD = 4;       // completed DEFAULT missions per career world
    private static final int MAX_ACTIVE_GENERATED = 2;   // active uncompleted generated missions per world

    private final MissionRepository missionRepository;
    private final MissionChoiceRepository missionChoiceRepository;
    private final MissionStepRepository missionStepRepository;
    private final MissionSessionRepository missionSessionRepository;
    private final CareerWorldRepository careerWorldRepository;
    private final StudentRepository studentRepository;
    private final SkillRepository skillRepository;
    private final ModelMapper modelMapper;
    private final ObjectMapper objectMapper;
    private final AiService aiService;
    private final com.example.qubaatisystem.Config.SecurityOwnershipService security;

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
        // Manual mapping (NOT ModelMapper): MissionInDTO carries relation-ish fields (careerWorldId,
        // skillType/skillId). ModelMapper would implicitly build a NEW transient Skill from skillType and set
        // it on Mission.skill (Mission has both skillType and a skill relation), causing Hibernate's
        // TransientPropertyValueException on flush. We resolve MANAGED CareerWorld + Skill from the DB instead.
        CareerWorld careerWorld = checkCareerWorld(dto.getCareerWorldId());
        Skill skill = resolveSkill(dto);

        Mission mission = new Mission();
        mission.setTitle(dto.getTitle());
        mission.setScenario(dto.getScenario());
        mission.setSkillType(dto.getSkillType());
        mission.setDifficulty(dto.getDifficulty());
        mission.setEstimatedMinutes(dto.getEstimatedMinutes());
        mission.setMaxScore(dto.getMaxScore());
        mission.setCareerWorld(careerWorld);
        mission.setSkill(skill);
        mission.setSource(MissionSource.DEFAULT);
        mission.setActive(true);
        mission.setId(null);
        missionRepository.save(mission);
    }

    public void update(Integer id, MissionInDTO dto) {
        // Manual mapping (see create()): resolve managed CareerWorld + Skill; never let ModelMapper fabricate a
        // transient Skill. Generated-mission metadata (source/active/generationSlot/generatedForStudent) is
        // preserved because it is not touched here.
        Mission oldMission = checkMission(id);
        CareerWorld careerWorld = checkCareerWorld(dto.getCareerWorldId());
        Skill skill = resolveSkill(dto);

        oldMission.setTitle(dto.getTitle());
        oldMission.setScenario(dto.getScenario());
        oldMission.setSkillType(dto.getSkillType());
        oldMission.setDifficulty(dto.getDifficulty());
        oldMission.setEstimatedMinutes(dto.getEstimatedMinutes());
        oldMission.setMaxScore(dto.getMaxScore());
        oldMission.setCareerWorld(careerWorld);
        oldMission.setSkill(skill);
        oldMission.setId(id);
        missionRepository.save(oldMission);
    }

    /**
     * Resolves an EXISTING, managed Skill for a mission. Priority: skillId, then skillType (the first Skill of
     * that type). Never creates a Skill — mission creation is not a skill-creation endpoint. Throws a clear
     * ApiException when no matching Skill exists.
     */
    private Skill resolveSkill(MissionInDTO dto) {
        if (dto.getSkillId() != null) {
            Skill skill = skillRepository.findSkillById(dto.getSkillId());
            if (skill == null) {
                throw new ApiException("Skill with id " + dto.getSkillId() + " not found");
            }
            return skill;
        }
        Skill skill = skillRepository.findFirstSkillBySkillType(dto.getSkillType());
        if (skill == null) {
            throw new ApiException("No Skill found for skillType " + dto.getSkillType()
                    + ". Create the Skill first (POST /api/v1/skills) before adding a mission for it.");
        }
        return skill;
    }

    public void delete(Integer id) {
        Mission mission = checkMission(id);
        missionRepository.delete(mission);
    }

    //EXTRA ENDPOINTS

    /**
     * LEGACY endpoint backing {@code GET /api/v1/mission/available/{studentId}}. Returns DEFAULT (shared)
     * missions only, across ALL career worlds — no per-career-world unlock logic and no grade filter. It
     * never leaks another student's AI_GENERATED missions. The correct flow endpoint is
     * {@code GET /api/v1/students/{studentId}/missions/available?careerWorldId=...}
     * ({@link #getAvailableMissions(Integer, Integer)}).
     */
    public List<MissionOutDTO> getAvailableMissions(Integer studentId) {
        checkStudent(studentId);
        List<MissionOutDTO> availableMissions = new ArrayList<>();
        for (Mission mission : missionRepository.findAll()) {
            if (isDefaultMission(mission)) {
                availableMissions.add(toOut(mission));
            }
        }
        return availableMissions;
    }

    @Transactional
    public MissionOutDTO generateMissionForStudent(Integer studentId, Integer careerWorldId) {
        Student student = checkStudent(studentId);
        CareerWorld careerWorld = checkCareerWorld(careerWorldId);

        // Legacy endpoint, now rule-compliant: it must obey the SAME Student-3 personalization rules as the
        // available/regenerate flow — locked until UNLOCK_THRESHOLD default completions, and never exceeding
        // MAX_ACTIVE_GENERATED active generated missions per career world. It generates exactly one mission per
        // call (up to the cap), via the shared rule-aware createGeneratedMission (AI + deterministic fallback).
        if (countCompletedDefaultMissions(studentId, careerWorldId) < UNLOCK_THRESHOLD) {
            throw new ApiException("Personalized missions are locked until " + UNLOCK_THRESHOLD
                    + " default missions are completed in this career world.");
        }
        List<Mission> active = activeGeneratedMissions(studentId, careerWorldId);
        if (active.size() >= MAX_ACTIVE_GENERATED) {
            throw new ApiException("You already have " + MAX_ACTIVE_GENERATED
                    + " active personalized missions in this career world; complete one before generating another.");
        }
        Mission mission = createGeneratedMission(student, careerWorld, active.size() + 1);
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
                || aiMissionDTO.getSkill() == null
                || aiMissionDTO.getDifficulty() == null
                || aiMissionDTO.getEstimatedMinutes() == null || aiMissionDTO.getEstimatedMinutes() <= 0
                || aiMissionDTO.getMaxScore() == null || aiMissionDTO.getMaxScore() <= 0) {
            throw new ApiException("Invalid AI mission JSON");
        }

        boolean hasSteps = aiMissionDTO.getSteps() != null && !aiMissionDTO.getSteps().isEmpty();
        if (hasSteps) {
            for (AiMissionStepDTO step : aiMissionDTO.getSteps()) {
                if (step == null
                        || step.getStepOrder() == null
                        || step.getScenario() == null || step.getScenario().isBlank()
                        || step.getChoices() == null || step.getChoices().size() < 2) {
                    throw new ApiException("Invalid AI mission JSON");
                }
                for (AiMissionStepChoiceDTO choice : step.getChoices()) {
                    if (choice == null
                            || choice.getContent() == null || choice.getContent().isBlank()
                            || choice.getScoreImpact() == null) {
                        throw new ApiException("Invalid AI mission JSON");
                    }
                }
            }
            return;
        }

        // Legacy single-step shape (no steps): require the flat choices list.
        if (aiMissionDTO.getScenario() == null || aiMissionDTO.getScenario().isBlank()
                || aiMissionDTO.getChoices() == null || aiMissionDTO.getChoices().size() < 2) {
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
        mission.setScenario(missionScenarioFromAi(aiMissionDTO));
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
                // scoreImpact deliberately NOT copied — MissionChoiceOutDTO is student-safe (use the admin
                // steps endpoint GET /missions/{id}/steps for teacher/admin scoreImpact).

                choices.add(choiceOutDTO);
            }

            out.setChoices(choices);
        }

        return out;
    }

    // ====================== MISSION FLOW (Student 3) ======================

    /**
     * Available missions for a student in ONE career world (the correct flow endpoint). Read-only: shows the
     * shared DEFAULT missions plus, once unlocked, the student's active AI_GENERATED missions. No grade
     * filter. The 4-completion threshold counts only completed DEFAULT missions in THIS career world.
     */
    public AvailableMissionsOutDTO getAvailableMissions(Integer studentId, Integer careerWorldId) {
        checkStudent(studentId);
        checkCareerWorld(careerWorldId);

        int completed = countCompletedDefaultMissions(studentId, careerWorldId);
        boolean unlocked = completed >= UNLOCK_THRESHOLD;
        int remaining = Math.max(0, UNLOCK_THRESHOLD - completed);

        List<AvailableMissionOutDTO> rows = new ArrayList<>();
        for (Mission m : missionRepository.findMissionsByCareerWorldId(careerWorldId)) {
            if (isDefaultMission(m)) {
                rows.add(toAvailableOut(m, studentId, MissionSource.DEFAULT));
            }
        }
        if (unlocked) {
            for (Mission m : missionRepository.findMissionsByGeneratedForStudentIdAndCareerWorldId(studentId, careerWorldId)) {
                if (m.getSource() == MissionSource.AI_GENERATED && Boolean.TRUE.equals(m.getActive())) {
                    rows.add(toAvailableOut(m, studentId, MissionSource.AI_GENERATED));
                }
            }
        }
        return new AvailableMissionsOutDTO(studentId, careerWorldId, completed, unlocked, remaining, rows);
    }

    /** Current-student wrapper: derives the acting student from Basic Auth, then delegates. */
    public AvailableMissionsOutDTO getMyAvailableMissions(com.example.qubaatisystem.Model.User user, Integer careerWorldId) {
        return getAvailableMissions(security.getCurrentStudentId(user), careerWorldId);
    }

    /** Default (shared) missions of a career world only — never personalized/generated missions. */
    public List<AvailableMissionOutDTO> getCareerWorldDefaultMissions(Integer careerWorldId) {
        checkCareerWorld(careerWorldId);
        List<AvailableMissionOutDTO> rows = new ArrayList<>();
        for (Mission m : missionRepository.findMissionsByCareerWorldId(careerWorldId)) {
            if (isDefaultMission(m)) {
                rows.add(new AvailableMissionOutDTO(m.getId(), m.getTitle(), MissionSource.DEFAULT, null, null, true));
            }
        }
        return rows;
    }

    /** Manual regenerate (Rule 3): replaces an uncompleted active AI_GENERATED mission IN PLACE (same row). */
    @Transactional
    public AvailableMissionOutDTO regenerateMission(Integer studentId, Integer missionId, String reason) {
        Student student = checkStudent(studentId);
        Mission mission = checkMission(missionId);
        if (mission.getSource() != MissionSource.AI_GENERATED) {
            throw new ApiException("Only AI_GENERATED missions can be regenerated");
        }
        if (mission.getGeneratedForStudent() == null || !mission.getGeneratedForStudent().getId().equals(studentId)) {
            throw new ApiException("This generated mission does not belong to student " + studentId);
        }
        if (!Boolean.TRUE.equals(mission.getActive())) {
            throw new ApiException("Only an active generated mission can be regenerated");
        }
        if (isCompletedByStudent(missionId, studentId)) {
            throw new ApiException("A completed mission cannot be regenerated");
        }
        CareerWorld careerWorld = mission.getCareerWorld();
        if (careerWorld == null) {
            throw new ApiException("Mission is not linked to a career world");
        }
        if (countCompletedDefaultMissions(studentId, careerWorld.getId()) < UNLOCK_THRESHOLD) {
            throw new ApiException("Personalized missions are not unlocked yet (complete at least "
                    + UNLOCK_THRESHOLD + " default missions in this career world)");
        }
        regenerateInPlace(mission, student, careerWorld);
        return toAvailableOut(mission, studentId, MissionSource.AI_GENERATED);
    }

    /** Current-student wrapper: derives the acting student from Basic Auth, then delegates. */
    @Transactional
    public AvailableMissionOutDTO regenerateMyMission(com.example.qubaatisystem.Model.User user,
                                                      com.example.qubaatisystem.DTO.In.MissionRegenerateInDTO request) {
        return regenerateMission(security.getCurrentStudentId(user), request.getMissionId(), request.getReason());
    }

    /** Whether the student has completed >= 4 DEFAULT missions in this career world. */
    public boolean isUnlocked(Integer studentId, Integer careerWorldId) {
        return countCompletedDefaultMissions(studentId, careerWorldId) >= UNLOCK_THRESHOLD;
    }

    /**
     * Rules 1 & 2: once the student has completed >= 4 DEFAULT missions in this career world, ensure there are
     * up to 2 active AI_GENERATED missions (creating only as many as needed; never duplicating). Returns the
     * number created. Below the threshold it does nothing.
     */
    @Transactional
    public int ensurePersonalizedMissionsUnlocked(Student student, CareerWorld careerWorld) {
        if (countCompletedDefaultMissions(student.getId(), careerWorld.getId()) < UNLOCK_THRESHOLD) {
            return 0;
        }
        int active = activeGeneratedMissions(student.getId(), careerWorld.getId()).size();
        int created = 0;
        int slot = active + 1;
        while (active < MAX_ACTIVE_GENERATED) {
            createGeneratedMission(student, careerWorld, slot);
            active++;
            created++;
            slot++;
        }
        return created;
    }

    /** Rule 4 helper: create one new generated mission if the active cap (2) is not already reached. */
    @Transactional
    public boolean createOneGeneratedMissionIfRoom(Student student, CareerWorld careerWorld) {
        int active = activeGeneratedMissions(student.getId(), careerWorld.getId()).size();
        if (active >= MAX_ACTIVE_GENERATED) {
            return false;
        }
        createGeneratedMission(student, careerWorld, active + 1);
        return true;
    }

    /** Creates one AI_GENERATED mission (AI when available, deterministic fallback otherwise). */
    @Transactional
    public Mission createGeneratedMission(Student student, CareerWorld careerWorld, int slot) {
        Mission mission;
        try {
            AiMissionDTO dto = parseAiMissionJson(aiService.generateMissionJson(student, careerWorld));
            Skill skill = getOrCreateSkill(dto.getSkill());
            mission = buildMissionFromAi(dto, skill, careerWorld, student);
            markGenerated(mission, slot);
            missionRepository.save(mission);
            writeAiContent(mission, dto);
        } catch (Exception e) {
            // AI key missing or call failed -> fallback multi-step mission so the flow still works.
            mission = new Mission();
            mission.setCareerWorld(careerWorld);
            mission.setGeneratedForStudent(student);
            applyFallbackToMission(mission, careerWorld, slot);
            markGenerated(mission, slot);
            missionRepository.save(mission);
            writeFallbackSteps(mission, careerWorld);
        }
        return mission;
    }

    // ---------- flow helpers ----------

    private void markGenerated(Mission mission, int slot) {
        mission.setSource(MissionSource.AI_GENERATED);
        mission.setActive(true);
        mission.setGenerationSlot(slot);
        mission.setGeneratedAt(LocalDateTime.now());
    }

    /** DEFAULT = shared mission: no personalized student and not marked AI_GENERATED (legacy null source ok). */
    private boolean isDefaultMission(Mission m) {
        return m.getGeneratedForStudent() == null && m.getSource() != MissionSource.AI_GENERATED;
    }

    private int countCompletedDefaultMissions(Integer studentId, Integer careerWorldId) {
        return (int) missionSessionRepository.findMissionSessionsByStudentId(studentId).stream()
                .filter(s -> s.getStatus() == MissionSessionStatus.COMPLETED)
                .map(MissionSession::getMission)
                .filter(m -> m != null && m.getCareerWorld() != null
                        && m.getCareerWorld().getId().equals(careerWorldId) && isDefaultMission(m))
                .map(Mission::getId)
                .distinct()
                .count();
    }

    private boolean isCompletedByStudent(Integer missionId, Integer studentId) {
        return missionSessionRepository.findMissionSessionsByStudentId(studentId).stream()
                .anyMatch(s -> s.getStatus() == MissionSessionStatus.COMPLETED
                        && s.getMission() != null && s.getMission().getId().equals(missionId));
    }

    private List<Mission> activeGeneratedMissions(Integer studentId, Integer careerWorldId) {
        return missionRepository.findMissionsByGeneratedForStudentIdAndCareerWorldId(studentId, careerWorldId).stream()
                .filter(m -> m.getSource() == MissionSource.AI_GENERATED && Boolean.TRUE.equals(m.getActive()))
                .collect(Collectors.toList());
    }

    private AvailableMissionOutDTO toAvailableOut(Mission m, Integer studentId, MissionSource source) {
        boolean completed = isCompletedByStudent(m.getId(), studentId);
        boolean active = m.getActive() == null || m.getActive();
        return new AvailableMissionOutDTO(m.getId(), m.getTitle(), source, m.getGenerationSlot(), completed, active);
    }

    private void regenerateInPlace(Mission mission, Student student, CareerWorld careerWorld) {
        try {
            AiMissionDTO dto = parseAiMissionJson(aiService.generateMissionJson(student, careerWorld));
            Skill skill = getOrCreateSkill(dto.getSkill());
            applyAiToMission(mission, dto, skill);
            clearMissionStepsAndChoices(mission);
            writeAiContent(mission, dto);
        } catch (Exception e) {
            applyFallbackToMission(mission, careerWorld, mission.getGenerationSlot());
            clearMissionStepsAndChoices(mission);
            writeFallbackSteps(mission, careerWorld);
        }
        mission.setLastRegeneratedAt(LocalDateTime.now());
        mission.setActive(true);
        missionRepository.save(mission);
    }

    private void applyAiToMission(Mission mission, AiMissionDTO dto, Skill skill) {
        mission.setTitle(dto.getTitle());
        mission.setScenario(missionScenarioFromAi(dto));
        mission.setSkill(skill);
        mission.setSkillType(skill.getSkillType());
        mission.setDifficulty(dto.getDifficulty());
        mission.setEstimatedMinutes(dto.getEstimatedMinutes());
        mission.setMaxScore(dto.getMaxScore());
    }

    private void applyFallbackToMission(Mission mission, CareerWorld careerWorld, Integer slot) {
        mission.setTitle("Personalized " + careerWorld.getTitle() + " Challenge " + (slot != null ? slot : 1));
        mission.setScenario("You face a realistic " + careerWorld.getTitle()
                + " situation and must decide the best next step based on what you know.");
        Skill skill = pickFallbackSkill();
        if (skill != null) {
            mission.setSkill(skill);
            mission.setSkillType(skill.getSkillType());
        } else if (mission.getSkillType() == null) {
            mission.setSkillType(SkillType.PROBLEM_SOLVING);
        }
        mission.setDifficulty(DifficultyLevel.MEDIUM);
        mission.setEstimatedMinutes(5);
        mission.setMaxScore(100);
    }

    private Skill pickFallbackSkill() {
        List<Skill> problemSolving = skillRepository.findSkillsBySkillType(SkillType.PROBLEM_SOLVING);
        if (!problemSolving.isEmpty()) {
            return problemSolving.get(0);
        }
        List<Skill> all = skillRepository.findAll();
        return all.isEmpty() ? null : all.get(0);
    }

    // ====================== MULTI-STEP CONTENT (generation + seeding) ======================

    /** Overall mission scenario: the AI's intro if present, else the first step's scenario. */
    private String missionScenarioFromAi(AiMissionDTO dto) {
        if (dto.getScenario() != null && !dto.getScenario().isBlank()) {
            return dto.getScenario();
        }
        if (dto.getSteps() != null && !dto.getSteps().isEmpty()) {
            return dto.getSteps().get(0).getScenario();
        }
        return null;
    }

    /** Persist AI mission content: multi-step when steps are present, else the legacy flat choices. */
    private void writeAiContent(Mission mission, AiMissionDTO dto) {
        if (dto.getSteps() != null && !dto.getSteps().isEmpty()) {
            writeAiSteps(mission, dto.getSteps());
        } else {
            saveMissionChoices(dto, mission);
        }
    }

    private void writeAiSteps(Mission mission, List<AiMissionStepDTO> steps) {
        List<AiMissionStepDTO> ordered = steps.stream()
                .sorted(Comparator.comparing(AiMissionStepDTO::getStepOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        int maxOrder = ordered.stream()
                .map(AiMissionStepDTO::getStepOrder)
                .filter(o -> o != null)
                .max(Comparator.naturalOrder())
                .orElse(ordered.size());

        Set<MissionChoice> allChoices = new HashSet<>();
        for (AiMissionStepDTO s : ordered) {
            MissionStep step = newStep(mission, s.getStepOrder(),
                    s.getScenario(), s.getStepOrder() != null && s.getStepOrder().equals(maxOrder));
            int i = 0;
            for (AiMissionStepChoiceDTO c : s.getChoices()) {
                allChoices.add(newChoice(mission, step, letterKey(i++),
                        c.getContent(), c.getScoreImpact(), c.getNextStepOrder()));
            }
        }
        mission.setMissionChoices(allChoices);
    }

    /** Deterministic 2-step fallback mission (used when AI is unavailable/invalid). */
    private void writeFallbackSteps(Mission mission, CareerWorld careerWorld) {
        String world = careerWorld != null ? careerWorld.getTitle() : "the mission";
        MissionStep step1 = newStep(mission, 1,
                "You arrive at a realistic " + world + " situation. What is your first move?", false);
        Set<MissionChoice> all = new HashSet<>();
        all.add(newChoice(mission, step1, "A", "Gather information and assess the situation first", 20, 2));
        all.add(newChoice(mission, step1, "B", "Act quickly with the safest obvious option", 10, 2));
        all.add(newChoice(mission, step1, "C", "Ask a mentor or expert for guidance", 15, 2));

        MissionStep step2 = newStep(mission, 2,
                "Now you must make the final call. Which approach do you choose?", true);
        all.add(newChoice(mission, step2, "A", "Choose the most thoughtful, well-reasoned plan", 30, null));
        all.add(newChoice(mission, step2, "B", "Choose a quick but riskier plan", 15, null));
        all.add(newChoice(mission, step2, "C", "Take a little more time to double-check the details", 20, null));

        mission.setMissionChoices(all);
    }

    /** Removes all steps and choices of a mission (choices first to respect the FK to mission_step). */
    private void clearMissionStepsAndChoices(Mission mission) {
        for (MissionChoice c : missionChoiceRepository.findMissionChoicesByMissionId(mission.getId())) {
            missionChoiceRepository.delete(c);
        }
        for (MissionStep s : missionStepRepository.findMissionStepsByMissionIdOrderByStepOrderAsc(mission.getId())) {
            missionStepRepository.delete(s);
        }
        mission.setMissionChoices(new HashSet<>());
    }

    private MissionStep newStep(Mission mission, Integer stepOrder, String scenario, boolean finalStep) {
        MissionStep step = new MissionStep();
        step.setMission(mission);
        step.setStepOrder(stepOrder);
        step.setScenario(scenario);
        step.setFinalStep(finalStep);
        step.setId(null);
        return missionStepRepository.save(step);
    }

    private MissionChoice newChoice(Mission mission, MissionStep step, String key,
                                    String text, Integer scoreImpact, Integer nextStepOrder) {
        MissionChoice choice = new MissionChoice();
        choice.setChoiceKey(key);
        choice.setText(text);
        choice.setScoreImpact(scoreImpact != null ? scoreImpact : 0);
        choice.setNextStepOrder(nextStepOrder);
        choice.setMission(mission);
        choice.setMissionStep(step);
        choice.setId(null);
        return missionChoiceRepository.save(choice);
    }

    private String letterKey(int index) {
        return String.valueOf((char) ('A' + index));
    }

    // ---------- seeding / authoring endpoints ----------

    /** Teacher/admin: replace a mission's steps + choices wholesale (Postman/seed authoring). */
    @Transactional
    public MissionStepsAdminOutDTO replaceSteps(Integer missionId, MissionStepBatchInDTO dto) {
        Mission mission = checkMission(missionId);
        if (dto == null || dto.getSteps() == null || dto.getSteps().isEmpty()) {
            throw new ApiException("steps must not be empty");
        }
        clearMissionStepsAndChoices(mission);

        List<MissionStepBatchItemInDTO> ordered = dto.getSteps().stream()
                .sorted(Comparator.comparing(MissionStepBatchItemInDTO::getStepOrder,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
        int maxOrder = ordered.stream()
                .map(MissionStepBatchItemInDTO::getStepOrder)
                .filter(o -> o != null)
                .max(Comparator.naturalOrder())
                .orElse(ordered.size());

        Set<MissionChoice> allChoices = new HashSet<>();
        for (MissionStepBatchItemInDTO s : ordered) {
            MissionStep step = newStep(mission, s.getStepOrder(),
                    s.getScenario(), s.getStepOrder() != null && s.getStepOrder().equals(maxOrder));
            int i = 0;
            for (MissionStepChoiceBatchInDTO c : s.getChoices()) {
                allChoices.add(newChoice(mission, step, letterKey(i++),
                        c.getContent(), c.getScoreImpact(), c.getNextStepOrder()));
            }
        }
        mission.setMissionChoices(allChoices);
        // Keep the mission scenario aligned with the first step for legacy/summary views.
        if (!ordered.isEmpty() && ordered.get(0).getScenario() != null) {
            mission.setScenario(ordered.get(0).getScenario());
            missionRepository.save(mission);
        }
        return buildAdminView(mission);
    }

    /** Teacher/admin: full ordered steps of a mission. */
    public MissionStepsAdminOutDTO getSteps(Integer missionId) {
        return buildAdminView(checkMission(missionId));
    }

    /** Teacher/admin: delete all steps + choices of a mission. */
    @Transactional
    public void deleteSteps(Integer missionId) {
        clearMissionStepsAndChoices(checkMission(missionId));
    }

    private MissionStepsAdminOutDTO buildAdminView(Mission mission) {
        List<MissionStepAdminOutDTO> stepViews = new ArrayList<>();
        for (MissionStep step : missionStepRepository.findMissionStepsByMissionIdOrderByStepOrderAsc(mission.getId())) {
            List<MissionStepChoiceAdminOutDTO> choiceViews = new ArrayList<>();
            for (MissionChoice c : missionChoiceRepository.findMissionChoicesByMissionStepIdOrderByChoiceKeyAsc(step.getId())) {
                choiceViews.add(new MissionStepChoiceAdminOutDTO(
                        c.getId(), c.getChoiceKey(), c.getText(), c.getScoreImpact(), c.getNextStepOrder()));
            }
            stepViews.add(new MissionStepAdminOutDTO(
                    step.getId(), step.getStepOrder(), step.getScenario(), step.getFinalStep(), choiceViews));
        }
        return new MissionStepsAdminOutDTO(mission.getId(), mission.getTitle(), mission.getSource(), stepViews);
    }
}
