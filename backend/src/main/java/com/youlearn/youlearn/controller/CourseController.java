package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.dto.ChapterEnrollmentDto;
import com.youlearn.youlearn.model.dto.CourseDto;
import com.youlearn.youlearn.model.dto.CourseUserDto;
import com.youlearn.youlearn.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/course")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public ResponseEntity<List<Course>> getCourses() {
        return new ResponseEntity<>(courseService.getCourses(), HttpStatus.OK);
    }

    @GetMapping("/single")
    public ResponseEntity<Course> getCourseByCourseId(@RequestParam("courseId") String courseId) {
        return new ResponseEntity<>(courseService.getCourseByCourseId(courseId), HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewCourse(@RequestBody CourseDto courseDto) {
        courseService.addNewCourse(courseDto);
    }

    @GetMapping
    @RequestMapping("/enrolled")
    public ResponseEntity<Boolean> isUserEnrolled(@RequestParam("courseId") Long courseId, @RequestParam("user") String email) {
        return new ResponseEntity<>(courseService.checkIfUserIsEnrolled(email, courseId), HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/enroll")
    @ResponseStatus(HttpStatus.CREATED)
    public void enrollUser(@RequestBody CourseUserDto courseUserDto) {
        courseService.enrollUser(courseUserDto.getEmail(), courseUserDto.getCourseId());
    }

    @GetMapping
    @RequestMapping("/{courseId}")
    public ResponseEntity<Boolean> findByCourseYoutubeId(@PathVariable("courseId") String courseId) {
        return new ResponseEntity<>(courseService.findCourseByCourseYoutubeId(courseId), HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/chapter/complete")
    public void completeChapter(@RequestBody ChapterEnrollmentDto chapterEnrollmentDto) {
        courseService.handleCompletedChapter(chapterEnrollmentDto);
    }

    @DeleteMapping
    @RequestMapping("/chapter/incomplete")
    public void incompleteChapter(@RequestParam("email") String email,
                                  @RequestParam("chapterUrl") String chapterUrl,
                                  @RequestParam("courseYoutubeId") String courseYoutubeId) {
        ChapterEnrollmentDto chapterEnrollmentDto = new ChapterEnrollmentDto();
        chapterEnrollmentDto.setEmail(email);
        chapterEnrollmentDto.setChapterUrl(chapterUrl);
        chapterEnrollmentDto.setCourseYoutubeId(courseYoutubeId);
        courseService.handleIncompleteChapter(chapterEnrollmentDto);
    }

    @PostMapping
    @RequestMapping("/chapter/completed")
    public ResponseEntity<Boolean> checkIfChapterCompletedForUser(@RequestBody ChapterEnrollmentDto chapterEnrollmentDto) {
        return new ResponseEntity<>(courseService.checkIfChapterIsCompleted(chapterEnrollmentDto), HttpStatus.OK);
    }

    @GetMapping
    @RequestMapping("/instructor")
    public ResponseEntity<String> retrieveInstructorEmail(@RequestParam("courseId") String courseId) {
        return new ResponseEntity<>(courseService.retrieveInstructorEmail(courseId), HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/complete")
    @ResponseStatus(HttpStatus.OK)
    public void completeCourse(@RequestParam("courseId") String courseId, @RequestBody String email) {
        courseService.completeCourse(courseId, email);
    }

    @PostMapping
    @RequestMapping("/certification")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> isCertificationObtained(@RequestParam("courseId") String courseId, @RequestBody String email) {
        return new ResponseEntity<>(courseService.checkIfCertificationIsObtained(courseId, email), HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping("/assessment")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Boolean> isAssessmentTaken(@RequestParam("courseId") String courseId, @RequestBody String email) {
        return new ResponseEntity<>(courseService.checkIfAssessmentIsTaken(courseId, email), HttpStatus.OK);
    }

    @PostMapping
    @RequestMapping(value = "/availability", method = RequestMethod.POST)
    public void setCourseAvailable(@RequestParam("courseId") String courseId, @RequestParam("isAvailable") Boolean isAvailable) {
        courseService.setCourseAvailability(courseId, isAvailable);
    }

    @GetMapping
    @RequestMapping(value = "/availability", method = RequestMethod.GET)
    public ResponseEntity<Boolean> getCourseAvailability(@RequestParam("courseId") String courseId) {
        return new ResponseEntity<>(courseService.getCourseAvailability(courseId), HttpStatus.OK);
    }

    @PutMapping
    @RequestMapping("/edit")
    public void updateCourse(@RequestParam("courseId") String courseId, @RequestBody CourseDto courseDto) {
        courseService.updateCourse(courseId, courseDto);
    }

    @DeleteMapping
    @RequestMapping("/delete")
    public void deleteCourse(@RequestParam("courseId") String courseId) {
        courseService.deleteCourse(courseId);
    }
}
