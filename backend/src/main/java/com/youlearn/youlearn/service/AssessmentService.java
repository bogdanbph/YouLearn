package com.youlearn.youlearn.service;

import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.Question;
import com.youlearn.youlearn.model.dto.AssessmentDto;
import com.youlearn.youlearn.repository.CourseRepository;
import com.youlearn.youlearn.repository.QuestionRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AssessmentService {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;

    public AssessmentService(QuestionRepository questionRepository, CourseRepository courseRepository) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public void saveOrUpdateAssessment(AssessmentDto assessmentDto) {
        Optional<Course> optionalCourse = courseRepository.findByCourseYoutubeId(assessmentDto.getCourseId());
        if (optionalCourse.isEmpty()) {
            throw new BadRequestException(String.format("There is no course with id %s", assessmentDto.getCourseId()));
        }
        Course course = optionalCourse.get();
        for (var question: assessmentDto.getQuestions()) {
            Optional<Question> optionalQuestion = questionRepository.findByQuestionTextAndCourseId(question, course);
            if (optionalQuestion.isPresent()) {
                questionRepository.updateQuestion(question, course.getId());
            }
            else {
                Question questionEntity = new Question();
                questionEntity.setQuestionText(question);
                questionEntity.setCourseId(course);
                questionRepository.save(questionEntity);
            }
        }
    }

    public AssessmentDto getAssessment(String courseId) {
        Optional<Course> optionalCourse = courseRepository.findByCourseYoutubeId(courseId);
        if (optionalCourse.isPresent()) {
            AssessmentDto assessmentDto = new AssessmentDto();
            assessmentDto.setQuestions(new ArrayList<>());
            assessmentDto.setCourseId(courseId);
            List<Question> questions = questionRepository.findByCourseId(optionalCourse.get());
            for (var question: questions) {
                assessmentDto.getQuestions().add(question.getQuestionText());
            }

            return assessmentDto;
        }
        else {
            throw new BadRequestException(String.format("There is no course with id %s", courseId));
        }
    }
}
