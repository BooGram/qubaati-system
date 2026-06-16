package com.example.qubaatisystem.Service;

import com.example.qubaatisystem.Api.ApiException;
import com.example.qubaatisystem.DTO.In.StudentInDTO;
import com.example.qubaatisystem.DTO.Out.StudentOutDTO;
import com.example.qubaatisystem.Enum.UserRole;
import com.example.qubaatisystem.Model.Classroom;
import com.example.qubaatisystem.Model.Student;
import com.example.qubaatisystem.Model.User;
import com.example.qubaatisystem.Repository.ClassroomRepository;
import com.example.qubaatisystem.Repository.StudentRepository;
import com.example.qubaatisystem.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final ClassroomRepository classroomRepository;
    private final ModelMapper modelMapper;

    public List<StudentOutDTO> getAll() {
        return studentRepository.findAll()
                .stream()
                .map(this::mapStudentToOutDTO)
                .toList();
    }

    public StudentOutDTO getById(Integer id) {
        List<Student> students = studentRepository.findStudentById(id);
        if (students.isEmpty()) {
            throw new ApiException("Student with id " + id + " not found");
        }
        return mapStudentToOutDTO(students.get(0));
    }

    @Transactional
    public StudentOutDTO create(StudentInDTO studentInDTO) {
        // Create the linked User account (the role is assigned internally, never from the DTO).
        User user = new User();
        user.setUsername(studentInDTO.getUsername());
        user.setEmail(studentInDTO.getEmail());
        user.setPassword(studentInDTO.getPassword());
        user.setRole(UserRole.STUDENT);
        User savedUser = userRepository.save(user);

        // Create the Student profile (ModelMapper copies scalar fields only) and link the saved User.
        Student student = modelMapper.map(studentInDTO, Student.class);
        applyDefaults(student);
        student.setUser(savedUser);
        student.setClassroom(resolveClassroom(studentInDTO.getClassroomId()));

        Student savedStudent = studentRepository.save(student);
        return mapStudentToOutDTO(savedStudent);
    }

    @Transactional
    public StudentOutDTO update(Integer id, StudentInDTO studentInDTO) {
        List<Student> students = studentRepository.findStudentById(id);
        if (students.isEmpty()) {
            throw new ApiException("Student with id " + id + " not found");
        }
        Student student = students.get(0);

        // Update Student profile fields. Clear the classroom first so ModelMapper cannot
        // mutate the id of the currently-managed Classroom while flattening classroomId.
        student.setClassroom(null);
        modelMapper.map(studentInDTO, student);
        applyDefaults(student);

        // Update the linked User account fields, keeping the role as STUDENT.
        User user = student.getUser();
        user.setUsername(studentInDTO.getUsername());
        user.setEmail(studentInDTO.getEmail());
        user.setPassword(studentInDTO.getPassword());
        user.setRole(UserRole.STUDENT);
        userRepository.save(user);

        student.setClassroom(resolveClassroom(studentInDTO.getClassroomId()));

        Student savedStudent = studentRepository.save(student);
        return mapStudentToOutDTO(savedStudent);
    }

    public void delete(Integer id) {
        List<Student> students = studentRepository.findStudentById(id);
        if (students.isEmpty()) {
            throw new ApiException("Student with id " + id + " not found");
        }
        studentRepository.delete(students.get(0));
    }

    // ---------- helpers ----------

    private Classroom resolveClassroom(Integer classroomId) {
        if (classroomId == null) {
            return null;
        }
        List<Classroom> classrooms = classroomRepository.findClassroomById(classroomId);
        if (classrooms.isEmpty()) {
            throw new ApiException("Classroom with id " + classroomId + " not found");
        }
        return classrooms.get(0);
    }

    private void applyDefaults(Student student) {
        if (student.getTotalPoints() == null) {
            student.setTotalPoints(0);
        }
        if (student.getCompletedMissionsCount() == null) {
            student.setCompletedMissionsCount(0);
        }
    }

    private StudentOutDTO mapStudentToOutDTO(Student student) {
        StudentOutDTO dto = modelMapper.map(student, StudentOutDTO.class);

        if (student.getUser() != null) {
            dto.setUserId(student.getUser().getId());
            dto.setUsername(student.getUser().getUsername());
            dto.setEmail(student.getUser().getEmail());
            dto.setRole(student.getUser().getRole());
        }

        if (student.getClassroom() != null) {
            dto.setClassroomId(student.getClassroom().getId());
            dto.setClassroomName(student.getClassroom().getName());
        }

        return dto;
    }
}
