package com.esprit.springjwt.entity;

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
public class OfferClient implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom ;
    private String prenom ;
    private int  numtel ;
    private String email ;
    private String cv ;
    private String lettreM ;
    private String lettre ;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH-ss-mm")
    private LocalDateTime date = LocalDateTime.now();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "offer_client_offers",
            joinColumns = @JoinColumn(name = "offer_client_id"),
            inverseJoinColumns = @JoinColumn(name = "offers_id")
    )
    private List<Offers> offers;

    @Value("#{false}")
    private boolean status;
    public void addOffers(Offers adminProject) {
        if (offers == null) {
            offers = new ArrayList<>();
        }
        offers.add(adminProject);
    }
}
