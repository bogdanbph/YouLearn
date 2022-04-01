package com.youlearn.youlearn.repository;

import com.youlearn.youlearn.model.CourseEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;


public interface CourseEnrollmentRepository extends JpaRepository<CourseEnrollment, Long> {
    @Query("SELECT ce FROM CourseEnrollment ce WHERE ce.course.id = ?1 and ce.user.id = ?2")
    Optional<CourseEnrollment> findByUserAndCourse(Long courseId, Long userId);

    @Modifying
    @Query("update CourseEnrollment ce set ce.isCertificationObtained = true, ce.completedAt = current_timestamp where ce.user.id = ?1 and ce.course.id = ?2")
    void completeCourse(Long userId, Long courseId);

}
