package com.youlearn.youlearn.service;

import com.youlearn.youlearn.exception.BadRequestException;
import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.CourseEnrollment;
import com.youlearn.youlearn.model.Question;
import com.youlearn.youlearn.model.User;
import com.youlearn.youlearn.model.dto.AssessmentDto;
import com.youlearn.youlearn.model.dto.SubmissionDto;
import com.youlearn.youlearn.repository.CourseEnrollmentRepository;
import com.youlearn.youlearn.repository.CourseRepository;
import com.youlearn.youlearn.repository.QuestionRepository;
import com.youlearn.youlearn.repository.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AssessmentService {

    private final QuestionRepository questionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final CourseEnrollmentRepository courseEnrollmentRepository;
    private final EmailService emailService;

    public AssessmentService(QuestionRepository questionRepository, CourseRepository courseRepository, UserRepository userRepository, CourseEnrollmentRepository courseEnrollmentRepository, EmailService emailService) {
        this.questionRepository = questionRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.courseEnrollmentRepository = courseEnrollmentRepository;
        this.emailService = emailService;
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

    public List<String> retrieveQuestions(String courseId) {

        Optional<Course> optionalCourse = courseRepository.findByCourseYoutubeId(courseId);
        if (optionalCourse.isPresent()) {
            List<Question> questions = questionRepository.findByCourseId(optionalCourse.get());
            return questions.stream().map(Question::getQuestionText).collect(Collectors.toList());
        }
        else {
            throw new BadRequestException(String.format("There is no course with id %s", courseId));
        }
    }

    @Transactional
    public void submitAssessment(SubmissionDto submissionDto) {
        Optional<User> optionalUser = userRepository.findByEmail(submissionDto.getEmail());

        if (optionalUser.isEmpty()) {
            throw new BadRequestException(String.format("There is no user with email %s in database.", submissionDto.getEmail()));
        }
        User user = optionalUser.get();
        Optional<Course> optionalCourse = courseRepository.findByCourseYoutubeId(submissionDto.getCourseId());
        if (optionalCourse.isEmpty()) {
            throw new BadRequestException(String.format("There is no course with id %s.", submissionDto.getCourseId()));
        }

        Course course = optionalCourse.get();
        Optional<User> optionalInstructor = userRepository.findByEmail(course.getEmailInstructor());

        if (optionalInstructor.isEmpty()) {
            throw new BadRequestException(String.format("The instructor %s is not anymore in our database.", course.getEmailInstructor()));
        }
        User instructor = optionalInstructor.get();
        if (user.getEmail().equals(instructor.getEmail())) {
            throw new BadRequestException("You cannot take assessment at your own course.");
        }
        try {
            List<String> questions = submissionDto.getQuestions();
            List<String> questionsMapped = questions.stream().map(question -> {
                String questionNo = question.split("##")[0] + ": ";
                String questionText = question.split("##")[1] + "<br/>";
                String answer = question.split("##")[2];

                return "<li><b>" + questionNo + "</b>" + questionText + answer + "</li>";
            }).collect(Collectors.toList());
            StringBuilder questionsString = new StringBuilder();
            for (String questionMapped: questionsMapped) {
                questionsString.append(questionMapped);
            }
            courseEnrollmentRepository.takeAssessment(user.getId(), course.getId());
            final String linkApprove = "http://localhost:8080/api/v1/assessment/result?status=true&userId=" + user.getId() + "&courseId=" + course.getId();
            final String linkDeny = "http://localhost:8080/api/v1/assessment/result?status=false&userId=" + user.getId() + "&courseId=" + course.getId();
            String email = buildEmailInstructor(user.getEmail(),
                    instructor.getFirstName() + " " + instructor.getLastName(),
                    linkApprove,
                    linkDeny,
                    questionsString.toString(),
                    course.getCourseName());
            emailService.send(instructor.getEmail(), email, "Assessment submission for " + course.getCourseName());
        }
        catch (Exception exception) {
            throw new BadRequestException("You have already taken the exam for this course.");
        }


    }

    private String buildEmailInstructor(String userEmail, String instructorName, String linkApprove, String linkDeny, String questionsList, String courseName) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" style=\"background-image:linear-gradient(to bottom right, #ffce00, #FE4880)\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td align=\"center\" style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#000000;text-decoration:none;vertical-align:top;display:inline-block\">Assessment submission for " + userEmail + "</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#000000\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td align=\"center\" style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi <b>" + instructorName + "</b>,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Find below the answers for course: <b>" + courseName + "</b> </p>" +
                " <ul>" +
                questionsList +
                "</ul>" +
                "        \n" +
                "           <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + linkApprove + "\" style=\"font-size: 20px; font-family: Helvetica, Arial, sans-serif; border-radius: 25px; background-color: #FFA73B; text-decoration: none; color: #000000; text-decoration: none; padding: 15px 25px; border-radius: 2px; border: 1px solid #FFA73B; display: inline-block;\">Passed</a> </p>" +
                "           <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c;display:inline-block;\"> <a href=\"" + linkDeny + "\" style=\"font-size: 20px; font-family: Helvetica, Arial, sans-serif; border-radius: 25px; background-color: #FFA73B; text-decoration: none; color: #000000; text-decoration: none; padding: 15px 25px; border-radius: 2px; border: 1px solid #FFA73B; display: inline-block; margin-left: 2%;\">Failed</a> </p>" +
                "        \n" +
                "           <p>See you soon, <br/><b>Team YouLearn</b></p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    private String buildEmailUser(String userName, String grade, String courseName) {
        return "<div style=\"font-family:Helvetica,Arial,sans-serif;font-size:16px;margin:0;color:#0b0c0c\">\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;min-width:100%;width:100%!important\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"100%\" height=\"53\" style=\"background-image:linear-gradient(to bottom right, #ffce00, #FE4880)\">\n" +
                "        \n" +
                "        <table role=\"presentation\" width=\"100%\" style=\"border-collapse:collapse;max-width:580px\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" align=\"center\">\n" +
                "          <tbody><tr>\n" +
                "            <td width=\"70\" valign=\"middle\">\n" +
                "                <table role=\"presentation\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td style=\"padding-left:10px\">\n" +
                "                  \n" +
                "                    </td>\n" +
                "                    <td align=\"center\" style=\"font-size:28px;line-height:1.315789474;Margin-top:4px;padding-left:10px\">\n" +
                "                      <span style=\"font-family:Helvetica,Arial,sans-serif;font-weight:700;color:#000000;text-decoration:none;vertical-align:top;display:inline-block\">Assessment grade for course " + courseName + " arrived!</span>\n" +
                "                    </td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "              </a>\n" +
                "            </td>\n" +
                "          </tr>\n" +
                "        </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td width=\"10\" height=\"10\" valign=\"middle\"></td>\n" +
                "      <td>\n" +
                "        \n" +
                "                <table role=\"presentation\" width=\"100%\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse\">\n" +
                "                  <tbody><tr>\n" +
                "                    <td bgcolor=\"#000000\" width=\"100%\" height=\"10\"></td>\n" +
                "                  </tr>\n" +
                "                </tbody></table>\n" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\" height=\"10\"></td>\n" +
                "    </tr>\n" +
                "  </tbody></table>\n" +
                "\n" +
                "\n" +
                "\n" +
                "  <table role=\"presentation\" class=\"m_-6186904992287805515content\" align=\"center\" cellpadding=\"0\" cellspacing=\"0\" border=\"0\" style=\"border-collapse:collapse;max-width:580px;width:100%!important\" width=\"100%\">\n" +
                "    <tbody><tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "      <td align=\"center\" style=\"font-family:Helvetica,Arial,sans-serif;font-size:19px;line-height:1.315789474;max-width:560px\">\n" +
                "        \n" +
                "            <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\">Hi <b>" + userName + "</b>,</p><p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> Find below the grade for your assessment: " +
                grade +
                "        \n" +
                "           <p style=\"Margin:0 0 20px 0;font-size:19px;line-height:25px;color:#0b0c0c\"> <a href=\"" + "http://localhost:3000" + "\" style=\"font-size: 20px; font-family: Helvetica, Arial, sans-serif; border-radius: 25px; background-color: #FFA73B; text-decoration: none; color: #000000; text-decoration: none; padding: 15px 25px; border-radius: 2px; border: 1px solid #FFA73B; display: inline-block;\">Check out website</a> </p>" +
                "        \n" +
                "           <p>See you soon, <br/><b>Team YouLearn</b></p>" +
                "        \n" +
                "      </td>\n" +
                "      <td width=\"10\" valign=\"middle\"><br></td>\n" +
                "    </tr>\n" +
                "    <tr>\n" +
                "      <td height=\"30\"><br></td>\n" +
                "    </tr>\n" +
                "  </tbody></table><div class=\"yj6qo\"></div><div class=\"adL\">\n" +
                "\n" +
                "</div></div>";
    }

    @Transactional
    public String gradeAssessment(Boolean status, Long userId, Long courseId) {
        Optional<CourseEnrollment> optionalCourseEnrollment = courseEnrollmentRepository.findByUserAndCourse(courseId, userId);
        if (optionalCourseEnrollment.isEmpty()) {
            throw new BadRequestException("The user is not enrolled in this course");
        }

        CourseEnrollment courseEnrollment = optionalCourseEnrollment.get();
        if (courseEnrollment.isGradeSubmitted()) {
            throw new BadRequestException("This assessment has been already submitted.");
        }

        courseEnrollmentRepository.gradeAssessment(status, userId, courseId);
        String grade = "";
        if (Boolean.TRUE.equals(status)) {
            grade = "             <p style=\"color:'green'; font-weight: 'bold'\"> Passed </p>";
        }
        else {
            grade = "             <p style=\"color:'red'; font-weight: 'bold'\"> Failed </p>";
        }

        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new BadRequestException(String.format("There is no user with id %s.", userId));
        }
        User user = optionalUser.get();

        Optional<Course> optionalCourse = courseRepository.findById(courseId);
        if (optionalCourse.isEmpty()) {
            throw new BadRequestException(String.format("There is no course with id %s.", courseId));
        }
        Course course = optionalCourse.get();

        String emailBody = buildEmailUser(user.getFirstName() + " " + user.getLastName(), grade, course.getCourseName());
        emailService.send(user.getEmail(), emailBody, "Grade received for " + course.getCourseName());
        return "http://localhost:3000/";
    }
}
