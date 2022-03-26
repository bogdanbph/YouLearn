package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.model.Course;
import com.youlearn.youlearn.model.dto.CourseDto;
import com.youlearn.youlearn.model.dto.CourseUserDto;
import com.youlearn.youlearn.service.CourseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
