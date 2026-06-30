package com.esprit.springjwt.entity;

import com.esprit.springjwt.entity.e_learning.PaiementType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.io.Serializable;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Request implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 100)
    private String lastName;

    @Column(name = "country")
    private String country;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Email
    @Column(name = "email")
    private String email;
    @Column(name = "training_period")
    private String trainingPeriod;
    @Column(name = "paiement_type", nullable = true)
    @Enumerated(EnumType.STRING)
    private PaiementType paiementType ;
    @Column(name = "education_place", nullable = true)

    private String educationPlace ;

    @ManyToOne
    @JoinColumn(name = "formation_id")

    private Formation formation;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status")
    private RequestStatus requestStatus;

}
