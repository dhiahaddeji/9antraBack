package com.esprit.springjwt.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.entity.Request;
import com.esprit.springjwt.entity.RequestPath;
import com.esprit.springjwt.entity.RequestStatus;
import com.esprit.springjwt.entity.e_learning.Path;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.repository.RequestPathRepository;
import com.esprit.springjwt.repository.e_learning.IPathRepository;

@Service
public class RequestPathService {

	@Autowired
	RequestPathRepository requestPathRepsitory;
	
	@Autowired
	IPathRepository pathRepository;
	public List<RequestPath> getRequestsByEmail(String email){
		List<RequestPath> requests = requestPathRepsitory.getRequestsByEmail(email);
		return requests;
	}
	
	public RequestPath add(RequestPath request, Long idPath) {

        Path path = pathRepository.findById(idPath).orElseThrow(() -> new RecordNotFoundException("Formation not found"));

        request.setPath(path);
        request.setRequestStatus(RequestStatus.UNPAID);
        return requestPathRepsitory.save(request);
    }

}
