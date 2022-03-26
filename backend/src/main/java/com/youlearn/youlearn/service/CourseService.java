package com.youlearn.youlearn.service;

import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.exception.NotFoundException;
import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.CourseEnrollment;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.model.dto.CourseDto;
import com.youlearn.youlearn.repository.CourseEnrollmentRepository;
import com.youlearn.youlearn.repository.CourseRepository;
import com.youlearn.youlearn.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final DataSource dataSource;

    public CourseService(CourseRepository courseRepository, UserRepository userRepository, CourseEnrollmentRepository courseEnrollmentRepository, DataSource dataSource) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.dataSource = dataSource;
    }

    public List<Course> getCourses() {
        return courseRepository.findAll();
    }

    public void addNewCourse(CourseDto courseDto) {
        if (userRepository.findByEmail(courseDto.getEmailInstructor()).isEmpty()) {
            throw new NotFoundException("Instructor not present in database!");
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
        return byUserAndCourse.isPresent();
    }

    public void enrollUser(String email, Long courseId) {
        Course course = checkCoursePresent(courseId);
        User user = checkUserPresent(email);

        CourseEnrollment courseEnrollment = new CourseEnrollment();
        courseEnrollment.setUser(user);
        courseEnrollment.setCourse(course);
        courseEnrollment.setCertificationObtained(false);
        courseEnrollment.setRegisteredAt(LocalDateTime.now());
        courseEnrollment.setChaptersCompleted(0);
        courseEnrollment.setPassedExams(0);

        courseEnrollmentRepository.save(courseEnrollment);
//        try (Connection connection = dataSource.getConnection();
//             PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO course_enrollment VALUES (?, ?, ?, ?, ?, ?)")) {
//            preparedStatement.setLong(1, course.getId());
//            preparedStatement.setLong(2, user.getId());
//            preparedStatement.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
//            preparedStatement.setInt(4, 0);
//            preparedStatement.setInt(5, 0);
//            preparedStatement.setBoolean(6, false);
//
//            preparedStatement.execute();
//        }
//        catch (SQLException ex) {
//            System.out.println(ex.getErrorCode());
//        }

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
}
