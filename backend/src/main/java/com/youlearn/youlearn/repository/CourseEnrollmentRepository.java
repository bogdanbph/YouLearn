package com.youlearn.youlearn.repository;

import com.youlearn.youlearn.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.course.id = ?1 and ce.user.id = ?2")
    Optional<CourseEnrollment> findByUserAndCourse(Long courseId, Long userId);

    @Modifying
    @Query("update CourseEnrollment ce set ce.isCertificationObtained = true, ce.completedAt = current_timestamp where ce.user.id = ?1 and ce.course.id = ?2")
    void completeCourse(Long userId, Long courseId);

    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.user.id = ?1 and ce.isCertificationObtained = true")
    List<CourseEnrollment> findCompletedCoursesForUserId(Long userId);

    @Modifying
    @Query("update CourseEnrollment ce set ce.isAssessmentTaken = true where ce.user.id = ?1 and ce.course.id = ?2")
    void takeAssessment(Long userId, Long courseId);

    @Modifying
    @Query("update CourseEnrollment ce set ce.isCertificationObtained = ?1, ce.completedAt = current_timestamp where ce.user.id = ?2 and ce.course.id = ?3")
    void gradeAssessment(Boolean status, Long userId, Long courseId);
}
