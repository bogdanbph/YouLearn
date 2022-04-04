package com.youlearn.youlearn.service;

import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.exception.NotFoundException;
import com.youlearn.youlearn.model.ChapterEnrollment;
import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.CourseEnrollment;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.model.dto.ChapterEnrollmentDto;
import com.youlearn.youlearn.model.dto.CourseDto;
import com.youlearn.youlearn.repository.ChapterEnrollmentRepository;
import com.youlearn.youlearn.repository.CourseEnrollmentRepository;
import com.youlearn.youlearn.repository.CourseRepository;
import com.youlearn.youlearn.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.transaction.Transactional;
import java.io.StringReader;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final ChapterEnrollmentRepository chapterEnrollmentRepository;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseEnrollmentRepository courseEnrollmentRepository, ChapterEnrollmentRepository chapterEnrollmentRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.chapterEnrollmentRepository = chapterEnrollmentRepository;
    }

    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    public void addNewCourse(CourseDto courseDto) {
        if (userRepository.findByEmail(courseDto.getEmailInstructor()).isEmpty()) {
            throw new NotFoundException("Instructor not present in database!");
        }

        Optional<Course> optionalCourse = courseRepository.findByCourseYoutubeId(courseDto.getCourseYoutubeId());
        if (optionalCourse.isPresent()) {
            throw new BadRequestException("This course is already present in database.");
        }

        Course course = new Course();
        course.setEmailInstructor(courseDto.getEmailInstructor());
        course.setCourseYoutubeId(courseDto.getCourseYoutubeId());
        course.setCourseName(courseDto.getCourseName());
        course.setPrice(courseDto.getPrice());
        course.setDescription(courseDto.getDescription());
        course.setNumberOfChapters(courseDto.getNumberOfChapters());

        courseRepository.save(course);
    }

    public Boolean checkIfUserIsEnrolled(String email, Long courseId) {
        Course course = checkCoursePresent(courseId);
        User user = checkUserPresent(email);


        Optional<CourseEnrollment> byUserAndCourse = courseEnrollmentRepository.findByUserAndCourse(course.getId(), user.getId());
        return byUserAndCourse.isPresent() || user.getEmail().equals(course.getEmailInstructor());
    }

    public void enrollUser(String email, Long courseId) {
        Course course = checkCoursePresent(courseId);
        User user = checkUserPresent(email);

        CourseEnrollment courseEnrollment = new CourseEnrollment();
        courseEnrollment.setUser(user);
        courseEnrollment.setCourse(course);
        courseEnrollment.setCertificationObtained(false);
        courseEnrollment.setRegisteredAt(LocalDateTime.now());

        courseEnrollmentRepository.save(courseEnrollment);
    }

    public Course checkCoursePresent(Long courseId) {
        Optional<Course> courseOptional = courseRepository.findById(courseId);
        if (courseOptional.isEmpty()) {
            throw new BadRequestException("There is no course with this id.");
        }

        return courseOptional.get();
    }

    public User checkUserPresent(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            throw new NotFoundException("There is no user with this email");
        }

        return userOptional.get();
    }

    public Boolean findCourseByCourseYoutubeId(String courseYoutubeId) {
        return courseRepository.findByCourseYoutubeId(courseYoutubeId).isPresent();
    }

    public void handleCompletedChapter(ChapterEnrollmentDto chapterEnrollmentDto) {
        chapterEnrollmentRepository.save(chapterBuilder(chapterEnrollmentDto));
    }

    public Boolean checkIfChapterIsCompleted(ChapterEnrollmentDto chapterEnrollmentDto) {
        User user = checkUserPresent(chapterEnrollmentDto.getEmail());
        Course course = checkIfCoursePresentByUrl(chapterEnrollmentDto.getCourseYoutubeId());

        return chapterEnrollmentRepository.findChapterEnrollmentByChapterUserAndCourse(user.getId(),
                course.getId(),
                chapterEnrollmentDto.getChapterUrl()).isPresent();
    }

    @Transactional
    public void handleIncompleteChapter(ChapterEnrollmentDto chapterEnrollmentDto) {
        User user = checkUserPresent(chapterEnrollmentDto.getEmail());
        Course course = checkIfCoursePresentByUrl(chapterEnrollmentDto.getCourseYoutubeId());

        Optional<ChapterEnrollment> chapterEnrollmentByChapterUserAndCourse = chapterEnrollmentRepository.findChapterEnrollmentByChapterUserAndCourse(user.getId(), course.getId(), chapterEnrollmentDto.getChapterUrl());
        if (chapterEnrollmentByChapterUserAndCourse.isPresent()) {
            chapterEnrollmentRepository.delete(chapterEnrollmentByChapterUserAndCourse.get());
        }
        else {
            throw new BadRequestException("There is no record in database to show that you completed this chapter.");
        }
    }

    private ChapterEnrollment chapterBuilder(ChapterEnrollmentDto chapterEnrollmentDto) {
        User user = checkUserPresent(chapterEnrollmentDto.getEmail());
        Course course = checkIfCoursePresentByUrl(chapterEnrollmentDto.getCourseYoutubeId());

        ChapterEnrollment chapterEnrollment = new ChapterEnrollment();
        chapterEnrollment.setChapterUrl(chapterEnrollmentDto.getChapterUrl());
        chapterEnrollment.setCourse(course);
        chapterEnrollment.setUser(user);
        chapterEnrollment.setCompletedAt(LocalDateTime.now());

        return chapterEnrollment;
    }

    public String retrieveInstructorEmail(String courseId) {
        Course course = checkIfCoursePresentByUrl(courseId);
        return course.getEmailInstructor();
    }

    @Transactional
    public void completeCourse(String courseId, String email) {

        try (JsonReader jsonReader = Json.createReader(new StringReader(email))) {
            JsonObject object = jsonReader.readObject();
            JsonValue jsonEmail = object.get("email");
            String emailValue = jsonEmail.toString().replace("\"", "");

            User user = checkUserPresent(emailValue);
            Course course = checkIfCoursePresentByUrl(courseId);

            courseEnrollmentRepository.completeCourse(user.getId(), course.getId());
        }
    }

    private Course checkIfCoursePresentByUrl(String courseId) {
        Optional<Course> optionalCourse = courseRepository.findByCourseYoutubeId(courseId);

        if (optionalCourse.isEmpty()) {
            throw new BadRequestException(String.format("There is no course with id %s.", courseId));
        }
        return optionalCourse.get();
    }

    public Boolean checkIfCertificationIsObtained(String courseId, String email) {
        String emailValue = deserializeJson(email);

        User user = checkUserPresent(emailValue);
        Course course = checkIfCoursePresentByUrl(courseId);

        Optional<CourseEnrollment> optionalCourseEnrollment = courseEnrollmentRepository.findByUserAndCourse(course.getId(), user.getId());
        if (!user.getEmail().equals(course.getEmailInstructor()) && optionalCourseEnrollment.isEmpty()) {
            throw new BadRequestException(String.format("User %s is not enrolled in course %s.", user.getEmail(), course.getCourseName()));
        }
        if (optionalCourseEnrollment.isPresent()) {
            CourseEnrollment courseEnrollment = optionalCourseEnrollment.get();
            return courseEnrollment.isCertificationObtained();
        }
        return user.getEmail().equals(course.getEmailInstructor());
    }

    public Boolean checkIfAssessmentIsTaken(String courseId, String email) {
        String emailValue = deserializeJson(email);

        User user = checkUserPresent(emailValue);
        Course course = checkIfCoursePresentByUrl(courseId);

        Optional<CourseEnrollment> optionalCourseEnrollment = courseEnrollmentRepository.findByUserAndCourse(course.getId(), user.getId());
        if (optionalCourseEnrollment.isEmpty()) {
            throw new BadRequestException(String.format("User %s is not enrolled in course %s.", user.getEmail(), course.getCourseName()));
        }
        CourseEnrollment courseEnrollment = optionalCourseEnrollment.get();
        return courseEnrollment.isAssessmentTaken();
    }

    private String deserializeJson(String json) {
        try (JsonReader jsonReader = Json.createReader(new StringReader(json))) {
            JsonObject object = jsonReader.readObject();
            JsonValue jsonEmail = object.get("email");
            return jsonEmail.toString().replace("\"", "");
        }
    }
}
