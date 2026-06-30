package com.esprit.springjwt.entity;


import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Groups implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String groupName;
    private Date creationDate;
	private String period;

    @ManyToOne
    @JoinColumn(name = "formateur_id")
    private User formateur;

    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;
	@ElementCollection
	@CollectionTable(name = "user_presence_status",
			joinColumns = @JoinColumn(name = "group_id"))
	@MapKeyColumn(name = "user_id") // Change from @MapKeyJoinColumn to @MapKeyColumn
	@Column(name = "presence_status")
	private Map<Long, Boolean> userPresenceStatus = new HashMap<>();


	@Column(name = "certificates_generated")
	private boolean certificatesGenerated;


	@ManyToMany(mappedBy = "groups")

	private List<Session> sessions = new ArrayList<>();



    @ManyToMany(mappedBy = "groups")
    private List<User> etudiants = new ArrayList<>();

	@OneToMany(mappedBy = "groups")
	private Set<Record> records;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public User getFormateur() {
		return formateur;
	}

	public void setFormateur(User formateur) {
		this.formateur = formateur;
	}

	public Formation getFormation() {
		return formation;
	}

	public void setFormation(Formation formation) {
		this.formation = formation;
	}

	public List<Session> getSessions() {
		return sessions;
	}

	public void setSessions(List<Session> sessions) {
		this.sessions = sessions;
	}

	public List<User> getEtudiants() {
		return etudiants;
	}

	public void setEtudiants(List<User> etudiants) {
		this.etudiants = etudiants;
	}
    
}
