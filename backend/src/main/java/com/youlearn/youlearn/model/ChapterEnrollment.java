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
@JsonIgnoreProperties({"user", "chapter"})
public class ChapterEnrollment {

    @Id
    @SequenceGenerator(
            name = "chapter_enrollment_sequence",
            sequenceName = "chapter_enrollment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chapter_enrollment_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;

    private String chapterUrl;

    LocalDateTime completedAt;

}
