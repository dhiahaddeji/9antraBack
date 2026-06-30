package com.esprit.springjwt.mapper;

import com.esprit.springjwt.dto.RequestDto;
import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.entity.Request;
import com.esprit.springjwt.entity.e_learning.PaiementType;

public class RequestMapper {
    public static RequestDto mapToDto(Request request, Formation formation) {
        return RequestDto.builder()
                .id(request.getId())
                .email(request.getEmail())
                .country(request.getCountry())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .requestStatus(request.getRequestStatus())
                .trainingPeriod(request.getTrainingPeriod())
                .formationName(formation.getNomFormation())
                .paiementType(request.getPaiementType())
                .educationPlace(request.getEducationPlace())
                .build();
    }
}
