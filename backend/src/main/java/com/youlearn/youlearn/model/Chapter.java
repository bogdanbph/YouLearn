package com.youlearn.youlearn.model;

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

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Chapter {

    @Id
    @SequenceGenerator(
            name = "chapter_sequence",
            sequenceName = "chapter_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "chapter_sequence"
    )
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
            nullable = false,
            name = "course_id"
    )
    private Course course;

    private String link;
}
