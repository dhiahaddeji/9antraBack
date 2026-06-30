package com.esprit.springjwt.entity.e_learning;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 50)
    private String title;

    @Column(name = "description", length = 150)
    private String description;

    @Column(name = "image")
    private String image;

    @Column(name = "file")
    private String file;

}
