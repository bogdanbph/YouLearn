package com.youlearn.youlearn.repository;

import com.youlearn.youlearn.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    @Query("SELECT c FROM Course c WHERE c.courseYoutubeId like concat('%', ?1, '%')")
    Optional<Course> findByCourseYoutubeId(String courseYoutubeId);

    @Modifying
    @Query("UPDATE Course c SET c.isVisible = ?2 WHERE c.id = ?1")
    void setCourseAvailability(Long courseId, Boolean availability);
}
