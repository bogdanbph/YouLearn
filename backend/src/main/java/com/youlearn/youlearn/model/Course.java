package com.youlearn.youlearn.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@Entity
@JsonIgnoreProperties({"enrollments"})
public class Course {

    @Id
    @SequenceGenerator(
            name = "course_sequence",
            sequenceName = "course_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "course_sequence"
    )
    private Long id;

    @OneToMany(mappedBy = "course")
    private Set<CourseEnrollment> enrollments;

    private String courseName;
    private String emailInstructor;
    private Integer numberOfChapters;
    private String courseYoutubeId;
    private Float price;
    private String description;
    private Boolean isVisible;
}
