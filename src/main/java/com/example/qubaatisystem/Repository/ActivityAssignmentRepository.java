package com.example.qubaatisystem.Repository;

import com.example.qubaatisystem.Enum.ActivityAssignmentStatus;
import com.example.qubaatisystem.Model.ActivityAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ActivityAssignmentRepository extends JpaRepository<ActivityAssignment, Integer> {

    ActivityAssignment findActivityAssignmentById(Integer id);

    List<ActivityAssignment> findActivityAssignmentsByActivityId(Integer activityId);

    // Assignments created by a given teacher (Student 1 teacher dashboard).
    List<ActivityAssignment> findActivityAssignmentsByAssignedByTeacherId(Integer teacherId);

    // Due-soon / overdue automation finders.
    List<ActivityAssignment> findActivityAssignmentsByStatus(ActivityAssignmentStatus status);

    List<ActivityAssignment> findActivityAssignmentsByStatusAndDueDateBefore(ActivityAssignmentStatus status, LocalDateTime cutoff);

    List<ActivityAssignment> findActivityAssignmentsByStatusAndDueDateBetween(ActivityAssignmentStatus status, LocalDateTime from, LocalDateTime to);

    List<ActivityAssignment> findActivityAssignmentsByStudentId(Integer studentId);

    List<ActivityAssignment> findActivityAssignmentsByClassroomId(Integer classroomId);

    List<ActivityAssignment> findActivityAssignmentsByActivityIdAndStudentId(Integer activityId, Integer studentId);

    List<ActivityAssignment> findActivityAssignmentsByActivityIdAndClassroomId(Integer activityId, Integer classroomId);
}
