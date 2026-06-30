package com.esprit.springjwt.entity;

import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Candidacy implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom ;
    private String prenom ;
    private int numtel ;
    private String email ;
    private String cv ;
    private String lettreM ;
    private String lettre ;
    private String experience ;
    private String education ;
    private String type ;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH-ss-mm")
    private LocalDateTime date = LocalDateTime.now();









}
