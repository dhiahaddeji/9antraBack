package com.esprit.springjwt.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.util.Date;
@Entity
public class Cours  implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id ;
    private String nomcours ;

    public Cours(Long id, String nomcours, String description, String file, Date date) {
        this.id = id;
        this.nomcours = nomcours;
        this.description = description;
        this.file = file;
        this.date = date;
    }

    private String description ;
    private String file ;
    private Date date ;

    public Cours() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomcours() {
        return nomcours;
    }

    public void setNomcours(String nomcours) {
        this.nomcours = nomcours;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
