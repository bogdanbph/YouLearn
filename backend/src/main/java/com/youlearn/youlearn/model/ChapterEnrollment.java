package com.youlearn.youlearn.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
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
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "course_id")
    private Course course;

    private String chapterUrl;

    LocalDateTime completedAt;

}
