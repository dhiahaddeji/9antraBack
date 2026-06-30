package com.esprit.springjwt.service;

import com.esprit.springjwt.dto.RequestDto;
import com.esprit.springjwt.entity.ERole;
import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.entity.Request;
import com.esprit.springjwt.entity.RequestStatus;
import com.esprit.springjwt.entity.Role;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.entity.e_learning.PaiementType;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.exception.StatusErrorException;
import com.esprit.springjwt.mapper.RequestMapper;
import com.esprit.springjwt.payload.response.MessageResponse;
import com.esprit.springjwt.repository.FormationRepository;
import com.esprit.springjwt.repository.IRequestRepository;
import com.esprit.springjwt.repository.RoleRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.payload.response.MessageResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class RequestServiceImpl implements IRequestService{

    @Autowired
    private IRequestRepository requestRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    UserRepository userRepository;
    
    @Autowired
    RoleRepository roleRepository;
    
    @Autowired
    PasswordEncoder encoder;

    @Override
    public Request add(Request request, Long idFormation) {

        Formation f = formationRepository.findById(idFormation).orElseThrow(() -> new RecordNotFoundException("Formation not found"));

        request.setFormation(f);
        request.setRequestStatus(RequestStatus.UNPAID);
        return requestRepository.save(request);
    }

    @Override
    public List<RequestDto> getAll() {
        List<Request> rs = requestRepository.findAll();
        List<RequestDto> requests = new ArrayList<>();

        for (Request r: rs) {
            requests.add(RequestMapper.mapToDto(r, r.getFormation()));
        }
        return requests;
    }

    @Override
    @Transactional
    public void changeStatus(RequestStatus status, Long idRequest) {
        Request r = requestRepository.findById(idRequest).orElseThrow(() -> new RecordNotFoundException("Formation not found"));
        User user = userRepository.findByEmail(r.getEmail());

        if(r.getRequestStatus() == status){
            throw new StatusErrorException();
        }
        requestRepository.changeStatus(status, idRequest);
    }
    @Transactional
    @Override
    public void changePeriod(String period, Long idRequest) {
        Request r = requestRepository.findById(idRequest).orElseThrow(() -> new RecordNotFoundException("Formation not found"));
        User user = userRepository.findByEmail(r.getEmail());

        if(r.getTrainingPeriod() == period){
            throw new StatusErrorException();
        }
        requestRepository.changePeriod(period, idRequest);
    }
    @Transactional
    @Override
    public void changePaytype(String pmaytyp, Long idRequest) {

        Request r = requestRepository.findById(idRequest).orElseThrow(() -> new RecordNotFoundException("Formation not found"));
        User user = userRepository.findByEmail(r.getEmail());

        if(r.getPaiementType()== PaiementType.valueOf(pmaytyp)){
            throw new StatusErrorException();
        }
        requestRepository.changePaytype( PaiementType.valueOf(pmaytyp), idRequest);
    }










    @Transactional
    @Override
    public void changeEducationplace(String educationplace, Long idRequest) {

        Request r = requestRepository.findById(idRequest).orElseThrow(() -> new RecordNotFoundException("Formation not found"));
        User user = userRepository.findByEmail(r.getEmail());

        if(r.getEducationPlace()== educationplace){
            throw new StatusErrorException();
        }
        requestRepository.changeEduPlace(educationplace, idRequest);
    }









    @Override
    public Request getOneById(Long idRequest) {

        Request r = requestRepository.findById(idRequest).orElseThrow(() -> new RecordNotFoundException("Formation not found"));
        return r;
    }

	@Override
	public List<Request> getRequestsByEmail(String email){
		List<Request> requests = requestRepository.getRequestsByEmail(email);
		return requests;
	}



   /* @Override
    public List<Request> getRequestsByEmailandrequeststatusandpaiementType(String email) {
        List<Request> requests = requestRepository.findByEmailAndRequestStatusAndAndPaiementType(email,RequestStatus.UNPAID, PaiementType.ONLINE);
        return requests;
    }*/
}
