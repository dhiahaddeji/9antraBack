package com.esprit.springjwt.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Feedback implements Serializable  {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    public Feedback(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    private String formation ;
    @Column(columnDefinition = "LONGTEXT")

    private String Title ;
    @Column(columnDefinition = "LONGTEXT")
    private String Comment;
    private int rating; 

    public boolean getPosted() {
        return posted;
    }

    public void setPosted(boolean posted) {
        this.posted = posted;
    }

    private boolean posted =false;
  

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }


    @ManyToOne
    private User user;

    public String getFormation() {
        return formation;
    }

    public void setFormation(String formation) {
        this.formation = formation;
    }

    @Temporal(TemporalType.DATE)
    private Date Date ;

    public Feedback() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }

    public Feedback(Long id) {
        this.id = id;
    }

    public Feedback(Long id, LocalDateTime createdAt, String formation, String title, String comment, int rating, boolean posted, User user, java.util.Date date) {
        this.id = id;
        this.createdAt = createdAt;
        this.formation = formation;
        Title = title;

        Comment = comment;
        this.rating = rating;
        this.posted = posted;
        this.user = user;
        Date = date;
    }

    public Feedback(LocalDateTime createdAt, String formation, String title, String comment, int rating, boolean posted, User user, java.util.Date date) {
        this.createdAt = createdAt;
        this.formation = formation;
        Title = title;

        Comment = comment;
        this.rating = rating;
        this.posted = posted;
        this.user = user;
        Date = date;
    }

    public void setSession(com.esprit.springjwt.entity.Session sessionById) {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

