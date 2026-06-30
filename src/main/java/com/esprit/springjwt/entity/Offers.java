package com.esprit.springjwt.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Offers implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String poste ;
    private String skills ;
    private String description ;
    private String experience ;
    private String type ;
    private String education ;
    @ManyToOne
    @JsonIgnore
    public Company company;
    @DateTimeFormat(pattern = "yyyy-MM-dd HH-ss-mm")
    private LocalDateTime date = LocalDateTime.now();
    @Value("#{false}")
    private boolean status;
    private String image;
    private  String nom ;
    private  String description2 ;
    private  String adresse ;
    private  String email ;
    private  int numtel ;

    @ManyToMany(mappedBy = "offers")
    @JsonIgnore

    private List<OfferClient> offerClients;
    public void addProjectClient(OfferClient projectClient) {
        if (offerClients == null) {
            offerClients = new ArrayList<>();
        }
        offerClients.add(projectClient);
    }
}
