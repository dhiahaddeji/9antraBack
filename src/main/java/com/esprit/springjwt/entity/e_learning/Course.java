package com.esprit.springjwt.entity.e_learning;

import lombok.*;
import org.hibernate.annotations.Type;

import com.esprit.springjwt.entity.Categorie;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Course implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "title", length = 50)
    private String title;

    @Lob
    @Column(name = "description", columnDefinition = "TEXT CHARACTER SET utf8mb4")
    @Type(type = "org.hibernate.type.TextType")
    private String description;

    @Column(name = "duration")
    private Integer duration;

    @Enumerated(EnumType.STRING)
    @Column(name = "language")
    private Language language;

    @Column(name = "image")
    private String image;

    @Column(name = "trailer")
    private String trailer;
    
    

    @ElementCollection
    private List<Goal> goal;

    @ToString.Exclude
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chapter> chapters;
    
    @ManyToOne
    @JoinColumn(name = "idCategorie")
    private Categorie categorie;
   

}
