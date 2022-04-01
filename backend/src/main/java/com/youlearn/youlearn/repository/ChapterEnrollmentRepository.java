package com.youlearn.youlearn.repository;

import com.youlearn.youlearn.model.ChapterEnrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ChapterEnrollmentRepository extends JpaRepository<ChapterEnrollment, Long> {
    @Query("SELECT ce FROM ChapterEnrollment ce WHERE ce.user.id = ?1 and ce.course.id = ?2 and ce.chapterUrl = ?3")
    Optional<ChapterEnrollment> findChapterEnrollmentByChapterUserAndCourse(Long userId, Long courseId, String chapterUrl);
}
