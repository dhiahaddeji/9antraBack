package com.esprit.springjwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
@Entity
public class Motsinterdit  implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Long id ;
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getMots() {
        return mots;
    }
    public void setMots(String mots) {
        this.mots = mots;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Motsinterdit(Long id, String mots) {
        super();
        this.id = id;
        this.mots = mots;
    }
    private String mots ;

    @JsonIgnore
    @ManyToOne
    Feedback Feedback;
}
