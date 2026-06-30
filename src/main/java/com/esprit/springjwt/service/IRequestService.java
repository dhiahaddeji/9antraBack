package com.esprit.springjwt.service;

import com.esprit.springjwt.dto.RequestDto;
import com.esprit.springjwt.entity.Request;
import com.esprit.springjwt.entity.RequestStatus;

import java.util.List;

public interface IRequestService {
    Request add(Request request, Long idFormation);

    List<RequestDto> getAll();

    void changeStatus(RequestStatus status, Long idRequest);
    void changePeriod( String period, Long idRequest);
    void changePaytype( String pmaytyp, Long idRequest);
    void changeEducationplace(String eduplace, Long idRequest);
    Request getOneById(Long idRequest);
    
    List<Request> getRequestsByEmail(String email);
   // List<Request> getRequestsByEmailandrequeststatusandpaiementType(String email);

}
