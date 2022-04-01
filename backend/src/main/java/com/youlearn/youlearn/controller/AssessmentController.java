package com.youlearn.youlearn.controller;

import com.youlearn.youlearn.model.dto.AssessmentDto;
import com.youlearn.youlearn.service.AssessmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> retrieveAssessment() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
