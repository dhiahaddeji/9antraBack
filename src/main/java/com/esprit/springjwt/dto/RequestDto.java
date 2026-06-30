package com.esprit.springjwt.dto;

import com.esprit.springjwt.entity.RequestStatus;
import com.esprit.springjwt.entity.e_learning.PaiementType;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RequestDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String country;
    private String phoneNumber;
    private String email;
    private RequestStatus requestStatus;
    private String formationName;
    private String trainingPeriod;
    private PaiementType paiementType;
    private String educationPlace ;
}
