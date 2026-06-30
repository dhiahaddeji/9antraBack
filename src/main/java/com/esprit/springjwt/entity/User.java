package com.esprit.springjwt.entity;

import com.esprit.springjwt.entity.e_learning.Course;
import com.esprit.springjwt.entity.e_learning.Event;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;


@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Data
@AllArgsConstructor
@NoArgsConstructor
public  class User {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Size(max = 80)
  @Column(unique = true)
  private String username;



  @Size(max = 120)
  private String password;

  @Size(max=50)
  private  String firstName;
  @Size(max=50)
  private  String lastName;
  @Size(min = 8, max = 40,message="Please enter a valid number")


  private  String numeroTel;

  @Convert(converter = AuthProviderConverter.class)
  @Column(columnDefinition = "VARCHAR(255) DEFAULT 'local'")
  @JsonDeserialize(using = AuthProviderDeserializer.class)
  private AuthProvider provider = AuthProvider.local;

  private String providerId;
@JsonIgnore
  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Note> notes;
  
  @CreationTimestamp
  private Timestamp created_at;
  
  private String about;
  @JsonIgnore
  @OneToMany
  private Set<Feedback> feedbacks;
  @JsonIgnore
  @OneToMany
  private Set<Formation> formations;
  @Size(max=50)
  private  String typeFormation;

  @Temporal(TemporalType.TIMESTAMP)
  @ColumnDefault(value = "null")
  private Date email_verified_at;

  private  String image;



  private String country;

  @Column(columnDefinition = "int default 0")
  private int enabled = 0;
  
  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(  name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id"))
  private Set<Role> roles = new HashSet<>();

  @JsonIgnore
  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
  private Formateur formateur;
  @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
  @JsonIgnore

  private List<Projects> projets;


  @OneToMany(cascade = CascadeType.ALL, mappedBy="user")
  @JsonIgnore
  public List<Certificat> certificats;

  @ManyToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "etudiant_groups",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "groups_id")
  )
  @JsonIgnore
  private List<Groups> groups = new ArrayList<>();
  
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(  name = "user_course",
  joinColumns = @JoinColumn(name = "user_id"),
  inverseJoinColumns = @JoinColumn(name = "course_id"))
  private List<Course> courses;

  public List<Groups> getGroups() {
	return groups;
}
  public List<Certificat> getCertificats() {
    return certificats;
  }



public void setGroups(List<Groups> groups) {
	this.groups = groups;
}
public void setCertificats(List<Certificat> certificats) {
    this.certificats = certificats;
  }


public List<Projects> getProjets() {
    return projets;
  }




  public void setProjets(List<Projects> projets) {
    this.projets = projets;
  }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

  public User(String username, String password, String firstName, String lastName, String numeroTel, String typeFormation, String image, String country, int enabled, Set<Role> roles, Formateur formateur,String about) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.numeroTel = numeroTel;
    this.typeFormation = typeFormation;
    this.image = image;
    this.country = country;
    this.enabled = enabled;
    this.roles = roles;
    this.formateur = formateur;
    this.about=about;

  }

  public User(String username, String password, String firstName, String lastName, String numeroTel, String image, String country, int enabled, Set<Role> roles, Formateur formateur) {
    this.username = username;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.numeroTel = numeroTel;
    this.image = image;
    this.country = country;
    this.enabled = enabled;
    this.roles = roles;
    this.formateur = formateur;
  }





  public void addNote(Note note) {
    notes.add(note);
    note.setUser(this);
  }

  public void removeNote(Note note) {
    notes.remove(note);
    note.setUser(null);
  }
}
