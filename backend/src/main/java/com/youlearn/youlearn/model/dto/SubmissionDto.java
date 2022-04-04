package com.youlearn.youlearn.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SubmissionDto {

    private String courseId;
    private List<String> questions;
    private String email;
}
