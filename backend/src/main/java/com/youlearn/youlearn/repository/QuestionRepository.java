package com.youlearn.youlearn.repository;

import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    Optional<Question> findByQuestionTextAndCourseId(String questionText, Course course);

    List<Question> findByCourseId(Course course);

    @Modifying
    @Query("update Question q set q.questionText = ?1 where q.questionText = ?1 and q.courseId.id = ?2")
    void updateQuestion(String questionText, Long courseId);
}
