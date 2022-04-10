package com.youlearn.youlearn.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"user", "course"})
public class CourseEnrollment {

    @Id
    @SequenceGenerator(
            name = "course_enrollment_sequence",
            sequenceName = "course_enrollment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "course_enrollment_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    LocalDateTime registeredAt;
    LocalDateTime completedAt;
    boolean isCertificationObtained;
    boolean isAssessmentTaken;
    boolean isGradeSubmmited;

}
