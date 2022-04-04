package com.youlearn.youlearn.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CertificationDto {
    private LocalDateTime completedAt;
    private LocalDateTime registeredAt;
    private String courseName;
    private String instructorName;
}
