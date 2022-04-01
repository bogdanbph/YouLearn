package com.youlearn.youlearn.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class AssessmentDto {

    private String courseId;
    private List<String> questions;
}
