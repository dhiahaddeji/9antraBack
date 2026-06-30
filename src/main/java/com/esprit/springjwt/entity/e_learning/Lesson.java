package com.esprit.springjwt.entity.e_learning;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Lesson implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 100)
    private String title;

    @Column(name = "video_lesson")
    private String videoLesson;

    @Column(name = "thumb_nail")
    private String thumbNail;

    @ManyToOne()
    @JoinColumn(name = "chapter_id")
    @JsonIgnore
    private Chapter chapter;

}
