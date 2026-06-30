package com.esprit.springjwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Categorie implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max=30)
    @Column(unique = true)
    private String nomCate;


    @JsonIgnore
    @OneToMany(mappedBy = "categorie")
    private List<Formation> formations;
    public Categorie() {
    }

public Categorie(String nomCate) {
        this.nomCate = nomCate;
    }

    public Categorie(Long id, String nomCate) {
        this.id = id;

        this.nomCate = nomCate;
    }

    public Categorie(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomCate() {
        return nomCate;
    }

    public void setNomCate(String nomCate) {
        this.nomCate = nomCate;
    }

}
