package com.esprit.springjwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
public class Training implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 /*


    private String name;
    private int nbChapters;
    private int nbProjects;
    private int nbExercices;
    private int nbMeetings;

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    private  String Description;

    public Training(String description) {
        Description = description;
    }

    public Training() {
    }

    public Training(Long id) {
        this.id = id;
    }

    public Training(Long id, Categorie categorie, String name, int nbChapters, int nbProjects, int nbExercices, int nbMeetings) {
        this.id = id;
        this.categorie = categorie;
        this.name = name;
        this.nbChapters = nbChapters;
        this.nbProjects = nbProjects;
        this.nbExercices = nbExercices;
        this.nbMeetings = nbMeetings;
    }

    public Training(Categorie categorie, String name, int nbChapters, int nbProjects, int nbExercices, int nbMeetings) {
        this.categorie = categorie;
        this.name = name;
        this.nbChapters = nbChapters;
        this.nbProjects = nbProjects;
        this.nbExercices = nbExercices;
        this.nbMeetings = nbMeetings;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Categorie getCategorie() {
        return categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbChapters() {
        return nbChapters;
    }

    public void setNbChapters(int nbChapters) {
        this.nbChapters = nbChapters;
    }

    public int getNbProjects() {
        return nbProjects;
    }

    public void setNbProjects(int nbProjects) {
        this.nbProjects = nbProjects;
    }

    public int getNbExercices() {
        return nbExercices;
    }

    public void setNbExercices(int nbExercices) {
        this.nbExercices = nbExercices;
    }

    public int getNbMeetings() {
        return nbMeetings;
    }

    public void setNbMeetings(int nbMeetings) {
        this.nbMeetings = nbMeetings;
    }*/
}
