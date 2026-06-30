package com.esprit.springjwt.entity;


import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Entity

public class Formateur implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String Github;


    private String Linkedin;
    private String CV ;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String skills;
    
    @OneToMany(mappedBy = "formateur", cascade = CascadeType.ALL)
    private List<Groups> groups;

    public Formateur() {
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGithub() {
        return Github;
    }

    public void setGithub(String github) {
        Github = github;
    }

    public String getLinkedin() {
        return Linkedin;
    }

    public void setLinkedin(String linkedin) {
        Linkedin = linkedin;
    }

    public String getCV() {
        return CV;
    }

    public void setCV(String CV) {
        this.CV = CV;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }


}

