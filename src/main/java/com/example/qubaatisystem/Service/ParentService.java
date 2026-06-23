package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.ChildCreateInDTO;
import com.example.qubaatisystem.DTO.In.ChildTargetInDTO;
import com.example.qubaatisystem.DTO.In.ChildUpdateProfileInDTO;
import com.example.qubaatisystem.DTO.In.ParentInDTO;
import com.example.qubaatisystem.DTO.In.StudentInDTO;
import com.example.qubaatisystem.DTO.Out.ActivitySubmissionOutDTO;
import com.example.qubaatisystem.DTO.Out.ChildLearningProfileOutDTO;
import com.example.qubaatisystem.DTO.Out.MissionSessionOutDTO;
import com.example.qubaatisystem.DTO.Out.ParentDashboardOutDTO;
import com.example.qubaatisystem.DTO.Out.ParentOutDTO;
import com.example.qubaatisystem.DTO.Out.StudentOutDTO;
import com.example.qubaatisystem.Enum.UserRole;
import com.example.qubaatisystem.Model.Parent;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Repository.ParentRepository;
import com.example.qubaatisystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final StudentService studentService;
    private final ChildLearningProfileService childLearningProfileService;
    private final ActivitySubmissionService activitySubmissionService;
    private final MissionSessionService missionSessionService;
    private final StudentPortfolioPdfService studentPortfolioPdfService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final com.example.qubaatisystem.Config.SecurityOwnershipService security;

    public List<ParentOutDTO> getAll() {
        return parentRepository.findAll()
                .stream()
                .map(this::mapParentToOutDTO)
                .toList();
    }

    public ParentOutDTO getById(Integer id) {
        Parent parent = parentRepository.findParentById(id);
        if (parent == null) {
            throw new ApiException("Parent with id " + id + " not found");
        }
        return mapParentToOutDTO(parent);
    }

    @Transactional
    public ParentOutDTO create(ParentInDTO parentInDTO) {
        // Create the linked User account (the role is assigned internally, never from the DTO).
        User user = new User();
        user.setUsername(parentInDTO.getUsername());
        user.setEmail(parentInDTO.getEmail());
        user.setPassword(passwordEncoder.encode(parentInDTO.getPassword()));
        user.setRole(UserRole.PARENT);
        User savedUser = userRepository.save(user);

        // Create the Parent profile (ModelMapper copies scalar fields only) and link the saved User.
        Parent parent = modelMapper.map(parentInDTO, Parent.class);
        parent.setUser(savedUser);

        parent.setId(null);
        Parent savedParent = parentRepository.save(parent);
        return mapParentToOutDTO(savedParent);
    }

    @Transactional
    public ParentOutDTO update(Integer id, ParentInDTO parentInDTO) {
        Parent parent = parentRepository.findParentById(id);
        if (parent == null) {
            throw new ApiException("Parent with id " + id + " not found");
        }

        // Update Parent profile fields (ModelMapper copies scalar fields only).
        modelMapper.map(parentInDTO, parent);
        parent.setId(id);

        // Update the linked User account fields, keeping the role as PARENT.
        User user = parent.getUser();
        user.setUsername(parentInDTO.getUsername());
        user.setEmail(parentInDTO.getEmail());
        user.setPassword(passwordEncoder.encode(parentInDTO.getPassword()));
        user.setRole(UserRole.PARENT);
        userRepository.save(user);

        Parent savedParent = parentRepository.save(parent);
        return mapParentToOutDTO(savedParent);
    }

    public void delete(Integer id) {
        Parent parent = parentRepository.findParentById(id);
        if (parent == null) {
            throw new ApiException("Parent with id " + id + " not found");
        }
        // If the parent still has children, the FK constraint will reject this deletion (no cascade, no orphaning).
        parentRepository.delete(parent);
    }

    // ========== Child management ==========

    @Transactional
    public StudentOutDTO createChild(Integer parentId, ChildCreateInDTO dto) {
        // Validate that the parent exists before delegating.
        if (parentRepository.findParentById(parentId) == null) {
            throw new ApiException("Parent with id " + parentId + " not found");
        }
        // Build a full StudentInDTO, supplying the parentId from the URL path.
        StudentInDTO studentInDTO = new StudentInDTO();
        studentInDTO.setUsername(dto.getUsername());
        studentInDTO.setEmail(dto.getEmail());
        studentInDTO.setPassword(dto.getPassword());
        studentInDTO.setFullName(dto.getFullName());
        studentInDTO.setAge(dto.getAge());
        studentInDTO.setGrade(dto.getGrade());
        // A parent only creates the child account — the child starts with NO classroom. A teacher enrolls the
        // student later via POST /api/v1/classrooms/students/enroll. The DTO classroomId is ignored here.
        studentInDTO.setParentId(parentId);
        return studentService.create(studentInDTO);
    }

    public List<StudentOutDTO> getChildren(Integer parentId) {
        if (parentRepository.findParentById(parentId) == null) {
            throw new ApiException("Parent with id " + parentId + " not found");
        }
        return studentService.getByParentId(parentId);
    }

    public StudentOutDTO getChildOverview(Integer parentId, Integer studentId) {
        if (parentRepository.findParentById(parentId) == null) {
            throw new ApiException("Parent with id " + parentId + " not found");
        }
        StudentOutDTO student = studentService.getById(studentId);
        if (!parentId.equals(student.getParentId())) {
            throw new ApiException("Student with id " + studentId + " does not belong to parent with id " + parentId);
        }
        return student;
    }

    @Transactional
    public StudentOutDTO updateChildProfile(Integer parentId, Integer studentId, ChildUpdateProfileInDTO dto) {
        if (parentRepository.findParentById(parentId) == null) {
            throw new ApiException("Parent with id " + parentId + " not found");
        }
        StudentOutDTO student = studentService.getById(studentId);
        if (!parentId.equals(student.getParentId())) {
            throw new ApiException("Student with id " + studentId + " does not belong to parent with id " + parentId);
        }
        return studentService.updateProfile(studentId, dto);
    }

    // ========== Dashboard ==========

    public ParentDashboardOutDTO getDashboard(Integer parentId) {
        ParentOutDTO parent = getById(parentId);                     // validates existence
        List<StudentOutDTO> children = studentService.getByParentId(parentId);

        int totalMissions = children.stream()
                .mapToInt(s -> s.getCompletedMissionsCount() != null ? s.getCompletedMissionsCount() : 0)
                .sum();

        // Each card now also carries the child's activity progress (Student 2) and mission progress (Student 3).
        List<ParentDashboardOutDTO.ChildCard> childCards = children.stream()
                .map(childLearningProfileService::buildChildCard)
                .collect(Collectors.toList());

        return new ParentDashboardOutDTO(
                parent.getId(),
                parent.getFullName(),
                children.size(),
                totalMissions,
                childCards
        );
    }

    /**
     * Parent-facing combined learning profile for one child (skills, learning style, activity performance,
     * recent mission insight, recommendations). Validates the parent owns the child.
     */
    public ChildLearningProfileOutDTO getChildLearningProfile(Integer parentId, Integer studentId) {
        if (parentRepository.findParentById(parentId) == null) {
            throw new ApiException("Parent with id " + parentId + " not found");
        }
        return childLearningProfileService.getLearningProfile(parentId, studentId);
    }

    public byte[] generateChildPortfolioPdf(Integer parentId, Integer studentId) {
        if (parentRepository.findParentById(parentId) == null) {
            throw new ApiException("Parent with id " + parentId + " not found");
        }
        return studentPortfolioPdfService.generateChildPortfolio(parentId, studentId);
    }

    // ========== Security-aware wrapper methods (called by ParentController) ==========

    // Creating/listing parents is an admin operation (no public self-registration of parents).
    public ParentOutDTO createForUser(User user, ParentInDTO dto) {
        security.assertAdmin(user);
        return create(dto);
    }

    public List<ParentOutDTO> getAllForUser(User user) {
        security.assertAdmin(user);
        return getAll();
    }

    public ParentOutDTO getByIdForUser(User user, com.example.qubaatisystem.DTO.In.IdInDTO dto) {
        security.assertParent(user);
        return getById(dto.getId());
    }

    public ParentOutDTO updateForUser(User user, ParentInDTO dto) {
        security.assertParent(user);
        return update(dto.getId(), dto);
    }

    public void deleteForUser(User user, com.example.qubaatisystem.DTO.In.IdInDTO dto) {
        security.assertParent(user);
        delete(dto.getId());
    }

    public ParentDashboardOutDTO getMyDashboard(User user) {
        return getDashboard(security.getCurrentParentId(user));
    }

    public StudentOutDTO createMyChild(User user, ChildCreateInDTO dto) {
        return createChild(security.getCurrentParentId(user), dto);
    }

    public List<StudentOutDTO> getMyChildren(User user) {
        return getChildren(security.getCurrentParentId(user));
    }

    public StudentOutDTO getMyChildOverview(User user, com.example.qubaatisystem.DTO.In.ChildTargetInDTO dto) {
        Integer parentId = security.getCurrentParentId(user);
        security.assertParentOwnsChild(user, dto.getStudentId());
        return getChildOverview(parentId, dto.getStudentId());
    }

    public ChildLearningProfileOutDTO getMyChildLearningProfile(User user, com.example.qubaatisystem.DTO.In.ChildTargetInDTO dto) {
        Integer parentId = security.getCurrentParentId(user);
        security.assertParentOwnsChild(user, dto.getStudentId());
        return getChildLearningProfile(parentId, dto.getStudentId());
    }

    public StudentOutDTO updateMyChildProfile(User user, ChildUpdateProfileInDTO dto) {
        Integer parentId = security.getCurrentParentId(user);
        security.assertParentOwnsChild(user, dto.getStudentId());
        return updateChildProfile(parentId, dto.getStudentId(), dto);
    }

    // ========== Child history (parent-safe read-only views) ==========

    // Secure wrappers: the parent comes from Basic Auth and must own the target child (admin bypasses). studentId
    // is a resource target in the body, never the acting profile.
    public List<ActivitySubmissionOutDTO> getChildActivityResults(User user, ChildTargetInDTO dto) {
        security.assertParentOwnsChild(user, dto.getStudentId());
        return getChildActivityResults(dto.getStudentId());
    }

    public List<MissionSessionOutDTO> getChildMissionHistory(User user, ChildTargetInDTO dto) {
        security.assertParentOwnsChild(user, dto.getStudentId());
        return getChildMissionHistory(dto.getStudentId());
    }

    public List<ActivitySubmissionOutDTO> getChildActivityResults(Integer studentId) {
        return activitySubmissionService.getStudentActivityResults(studentId);
    }

    public List<MissionSessionOutDTO> getChildMissionHistory(Integer studentId) {
        return missionSessionService.getMissionHistoryByStudentId(studentId);
    }

    // ---------- helpers ----------

    private ParentOutDTO mapParentToOutDTO(Parent parent) {
        ParentOutDTO dto = modelMapper.map(parent, ParentOutDTO.class);

        if (parent.getUser() != null) {
            dto.setUserId(parent.getUser().getId());
            dto.setUsername(parent.getUser().getUsername());
            dto.setEmail(parent.getUser().getEmail());
            dto.setRole(parent.getUser().getRole());
        }

        return dto;
    }
}
