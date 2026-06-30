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
public class SpecificProject implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    public String image ;
    private String email ;
    private int numtel ;
    private String github ;
    private String linkedin ;
    private String remark ;
    ////////////////////////
    private String titre;
    private String description;
    private String technologies ;
    private String video ;
    private String imageP ;
    private float price  ;

    @Value("#{false}")
    private boolean status;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH-ss-mm")

    private LocalDateTime date = LocalDateTime.now();

}
