package com.youlearn.youlearn.service;

import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.CourseEnrollment;
import com.youlearn.youlearn.model.Token;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.model.UserRole;
import com.youlearn.youlearn.model.dto.CertificationDto;
import com.youlearn.youlearn.repository.CourseEnrollmentRepository;
import com.youlearn.youlearn.repository.CourseRepository;
import com.youlearn.youlearn.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.youlearn.youlearn.utils.Constants.USER_NOT_FOUND_MSG;

@Service
@AllArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final CourseRepository courseRepository;
    
    private final CourseEnrollmentRepository courseEnrollmentRepository;

    private final Logger logger = LogManager.getLogger(UserService.class);

    @Override
    public User loadUserByUsername(String email) throws UsernameNotFoundException {

        logger.info("Loading user by email from database...");
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
        }

        return optionalUser.get();
    }

    public User getUserByEmail(String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isEmpty()) {
            throw new UsernameNotFoundException(String.format(USER_NOT_FOUND_MSG, email));
        }

        return byEmail.get();
    }

    public String signUp(User user) {
        boolean present = userRepository.findByEmail(user.getEmail()).isPresent();

        // TODO: check if user is in database, but he didnt confirmed yet.
        if (present) {
            throw new BadRequestException("Email is already present in database.");
        }
        String password = passwordEncoder.encode(user.getPassword());
        user.setPassword(password);

        userRepository.save(user);

        String tokenString = UUID.randomUUID().toString();
        Token token = new Token(
                tokenString,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30),
                user
        );
        tokenService.saveToken(token);

        return tokenString;
    }

    public void deleteUser(User user) {
        userRepository.delete(user);
        tokenService.deleteTokenByUserId(user.getId());
    }

    public void enableUser(String email) {
        userRepository.enableAppUser(email);
    }

    public UserRole getUserRoleForUser(String email) {
        return checkIfEmailExists(email).getRole();
    }
    
    private User checkIfEmailExists(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return userOptional.get();
        }
        else {
            throw new BadRequestException("User not present in database.");
        }
    }

    public List<CertificationDto> getCertificationsForUser(String email) {
        User user = checkIfEmailExists(email);
        List<CourseEnrollment> completedCoursesForUserId = courseEnrollmentRepository.findCompletedCoursesForUserId(user.getId());

        if (completedCoursesForUserId.isEmpty()) {
            return Collections.emptyList();
        }
        List<CertificationDto> certificationDtos = new ArrayList<>();
        for (var completedCourseForUserId: completedCoursesForUserId) {
            Optional<Course> optionalCourse = courseRepository.findById(completedCourseForUserId.getCourse().getId());
            if (optionalCourse.isEmpty()) {
                throw new BadRequestException(String.format("There is no course with id %s", completedCourseForUserId.getCourse().getId()));
            }

            Course course = optionalCourse.get();

            CertificationDto certificationDto = new CertificationDto();
            certificationDto.setCourseName(course.getCourseName());
            certificationDto.setRegisteredAt(completedCourseForUserId.getRegisteredAt());
            certificationDto.setCompletedAt(completedCourseForUserId.getCompletedAt());
            certificationDto.setInstructorName(course.getNameInstructor());

            certificationDtos.add(certificationDto);
        }

        return certificationDtos;
    }

    public void updateUserProfilePicture(String imageUrl, String email) {
        Optional<User> byEmail = userRepository.findByEmail(email);
        if (byEmail.isPresent()) {
            User user = byEmail.get();
            user.setProfilePicture(imageUrl);

            userRepository.save(user);
        }
        else {
            throw new UsernameNotFoundException("There is no user with this email...");
        }
    }
}
