package com.esprit.springjwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ProjectOwner implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    public String image ;
    private String email ;
    private int numtel ;
    //optional
    private boolean status ;
    private String github ;
    private String linkedin ;



    @OneToMany(cascade = CascadeType.ALL, mappedBy="projectOwner")
    @JsonIgnore
    public List<AdminProjects> adminProjects;
}
