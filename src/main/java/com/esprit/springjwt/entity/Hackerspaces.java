package com.esprit.springjwt.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Hackerspaces implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String Region;
    @Column(columnDefinition = "LONGTEXT")
    private String Location;
    private Integer Phone;
    private String Email;
    private String photo;
    private String Description;
    private String Adresse;



    public Hackerspaces(String region, String location, Integer phone, String email, String photo, String description, String adresse) {
        Region = region;
        Location = location;
        Phone = phone;
        Email = email;
        this.photo = photo;
        Description = description;
        Adresse = adresse;
    }
}
