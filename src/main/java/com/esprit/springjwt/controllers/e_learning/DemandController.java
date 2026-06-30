package com.esprit.springjwt.controllers.e_learning;

import com.esprit.springjwt.entity.e_learning.Demand;
import com.esprit.springjwt.entity.e_learning.DemandCategory;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.service.EmailServiceImpl;
import com.esprit.springjwt.service.e_learning.IDemandService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/e-learning/demand")
public class DemandController {

    @Autowired
    IDemandService demandService;

    @Autowired
    EmailServiceImpl emailService;


    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            List<Demand> demands = demandService.getAll();
            return new ResponseEntity<>(demands, HttpStatus.OK);

        } catch(RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/getByCategory")
    public ResponseEntity<?> getByCategory(@RequestParam(name = "category") DemandCategory demandCategory) {
        try {
            List<Demand> demands = demandService.getByCategory(demandCategory);

            return new ResponseEntity<>(demands, HttpStatus.OK);

        } catch(RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping
    public ResponseEntity<?> addDemand(@RequestParam String demand,
                                       @RequestParam(name = "idPath", required = false) Long idPath) {
        try {

            if(demand.length() != 0) {
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.registerModule(new JavaTimeModule());
                Demand d = objectMapper.readValue(demand,Demand.class);

            if ((idPath != null && d.getCategory() != DemandCategory.Path) ||
                    (idPath == null && d.getCategory() == DemandCategory.Path)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            Demand de = demandService.add(d, idPath);
            return new ResponseEntity<>(de, HttpStatus.OK);
            }

            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch(RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (JsonMappingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (JsonProcessingException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{idDemand}")
    public ResponseEntity<?> respondToDemand(@PathVariable("idDemand") Long idDemand,
                                             @RequestBody String message,
                                             @RequestParam("subject") String subject) {
        try {

            Demand d = demandService.getOne(idDemand);

            emailService.sendSimpleMail(d.getEmail(), subject,message);

            demandService.changeToResponded(d);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch(RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
