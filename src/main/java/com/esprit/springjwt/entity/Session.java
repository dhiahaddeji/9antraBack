package com.esprit.springjwt.entity;


import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
@Entity
@Data
public class Session implements Serializable {
    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    private String SessionName ;
    private String Description ;
	@Temporal(TemporalType.TIMESTAMP)
    private Date startDate;

	@Column(unique = true)
	private String GeneratedLink;
	@Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;
    private String GroupSession;

    @ManyToMany
    @JoinTable(
        name = "session_group",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    @JsonIgnore
    private List<Groups> groups = new ArrayList<>();
    @ManyToOne
    @JoinColumn(name = "formation_id")
    private Formation formation;

    @ManyToOne
    @JoinColumn(name = "formateur_id")
    private Formateur formateur;

	@ElementCollection
	@CollectionTable(name = "user_presence_statuss", joinColumns = @JoinColumn(name = "group_id"))
	@MapKeyColumn(name = "user_id")
	@Column(name = "presence_status")
	private Map<Long, Boolean> userPresence = new HashMap<>();



}