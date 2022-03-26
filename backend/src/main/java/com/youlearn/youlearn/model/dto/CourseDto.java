package com.youlearn.youlearn.model.dto;

import lombok.Data;

@Data
public class CourseDto {

    private String courseName;
    private String emailInstructor;
    private Integer numberOfChapters;
    private String courseYoutubeId;
    private Float price;
    private String description;
}
