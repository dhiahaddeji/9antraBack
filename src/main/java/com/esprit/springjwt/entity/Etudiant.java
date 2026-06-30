package com.esprit.springjwt.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "Etudiant")
public class Etudiant {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long idEtudiant;
    private String name;
    private String email;
    private int tel;
    private String adresse;
    private String cv;
    private String video;

    public Etudiant(String name, String email, int tel, String adresse, String cv, String video) {
        super();
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.adresse = adresse;
        this.cv = cv;
        this.video = video;

    }


}




