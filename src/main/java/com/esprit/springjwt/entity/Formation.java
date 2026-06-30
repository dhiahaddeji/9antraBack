package com.esprit.springjwt.entity;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.validation.constraints.Size;

import org.hibernate.annotations.ColumnDefault;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Formation implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @Column(unique = true)
    private String nomFormation;

    @ManyToOne
    @JoinColumn(name = "idCategorie")
    private Categorie categorie;
    @OneToMany(mappedBy = "formation")



    private List<Chapters> chapters;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private int nbChapters;
    private int nbProjects;
    private int nbExercices;
    private int nbMeetings;
    private int workspaces;
    @Size(max = 1024, message = "Description cannot be longer than 1024 characters")
    private String Description;
    private double price;
    private Date startedDate;
    private String thumbnail;
    

	private String posibility;
	
	@ColumnDefault("1")
	private int status;

}
