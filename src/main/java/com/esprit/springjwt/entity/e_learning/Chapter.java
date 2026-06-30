package com.esprit.springjwt.entity.e_learning;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Chapter_E_learning")
@ToString
public class Chapter implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 100)
    private String title;

    @ToString.Exclude
    @JsonIgnore
    @OneToMany(mappedBy = "chapter", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Lesson> lessons;

    @ManyToOne()
    @JsonIgnore
    @JoinColumn(name = "course_id")
    private Course course;

}
