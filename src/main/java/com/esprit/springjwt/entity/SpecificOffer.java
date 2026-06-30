package com.esprit.springjwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "specific_offer")
public class SpecificOffer implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String poste;
    private String skills;
    private String description;
    private String experience;
    private String type;
    private String education;
    
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime date;
    
    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean status = false;
    
    private String image;
    private String nom;
    private String descriptionC;
    private String adresse;
    private String email;
    private int numtel;

    @PrePersist
    protected void onCreate() {
        if (date == null) {
            date = LocalDateTime.now();
        }
    }
}
