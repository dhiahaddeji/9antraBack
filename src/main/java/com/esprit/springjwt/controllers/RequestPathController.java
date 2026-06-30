package com.esprit.springjwt.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.esprit.springjwt.entity.Request;
import com.esprit.springjwt.entity.RequestPath;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.service.RequestPathService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@RestController
@RequestMapping("/api/requestPath")
public class RequestPathController {

	@Autowired
	RequestPathService requestPathService;
	
  @GetMapping("/getRequestsByEmail")
    public ResponseEntity<?> getRequestsByEmail(@RequestParam("email") String email) {
    	List<RequestPath> requests = requestPathService.getRequestsByEmail(email);
    	return ResponseEntity.ok(requests);
    }
  
  
  @PostMapping("/{idPath}")
  public ResponseEntity<?> AddRequest(@RequestParam(name = "request") String request, @PathVariable("idPath") Long idPath) {

      try {
          ObjectMapper objectMapper = new ObjectMapper();
          objectMapper.registerModule(new JavaTimeModule());
          RequestPath r = requestPathService.add(objectMapper.readValue(request, RequestPath.class), idPath);
          
          String msj = "Hi " + r.getFirstName() + " " + r.getLastName() + " Welcome to The-Bridge " +
                  "Thank you for your request for information regarding "+ r.getPath().getTitle() + "Training" +
                  "Our team will contact you to finalize your registration to support you in your project, within 48 hours." +
                  "In the meantime, and for any additional information, do not hesitate to contact our team of educational advisers on 20 000 000."

                  ;

          String subject = "Bienvenue sur 9antraTraining";
          return new ResponseEntity<>(r, HttpStatus.OK);
      } catch (RecordNotFoundException e) {
          return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
      } catch (JsonProcessingException e) {
          return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
      }
  }

  
    
}
