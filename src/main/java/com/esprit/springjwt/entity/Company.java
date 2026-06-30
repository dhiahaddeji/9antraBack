package com.esprit.springjwt.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Company implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private  String nom ;
    private  String image ;
    private  String description ;
    private  String adresse ;
    private  String email ;
    private  int numtel ;
    @Value("#{false}")
    private boolean status;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH-ss-mm")
    private LocalDateTime date = LocalDateTime.now();


    @OneToMany(cascade = CascadeType.ALL, mappedBy="company")
    @JsonIgnore
    public List<Offers> offers;

}
