package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.model.dto.AssessmentDto;
import com.youlearn.youlearn.model.dto.SubmissionDto;
import com.youlearn.youlearn.service.AssessmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/assessment")
public class AssessmentController {

    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @PostMapping("/new")
    @ResponseStatus(HttpStatus.CREATED)
    public void addOrUpdateAssessment(@RequestBody AssessmentDto assessmentDto) {
        assessmentService.saveOrUpdateAssessment(assessmentDto);
    }

    @GetMapping
    public ResponseEntity<AssessmentDto> getAssessment(@RequestParam("courseId") String courseId) {
        return new ResponseEntity<>(assessmentService.getAssessment(courseId), HttpStatus.OK);
    }

    @GetMapping("/questions")
    public ResponseEntity<List<String>> retrieveQuestions(@RequestParam("courseId") String courseId) {
        return new ResponseEntity<>(assessmentService.retrieveQuestions(courseId), HttpStatus.OK);
    }

    @PostMapping("/submit")
    @ResponseStatus(HttpStatus.OK)
    public void submitAssessment(@RequestBody SubmissionDto submissionDto) {
        assessmentService.submitAssessment(submissionDto);
    }

    @RequestMapping(path = "/result", method = RequestMethod.GET)
    public ModelAndView gradeAssessment(@RequestParam("status") Boolean status, @RequestParam("userId") Long userId, @RequestParam("courseId") Long courseId) {
        assessmentService.gradeAssessment(status, userId, courseId);
        ModelMap modelMap = new ModelMap();
        return new ModelAndView("redirect:" + "http://localhost:3000", modelMap);
    }

}
