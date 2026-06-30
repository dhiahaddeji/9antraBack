package com.esprit.springjwt.controllers.e_learning;

import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.entity.e_learning.Event;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.service.e_learning.EventServiceImpl;
import com.esprit.springjwt.service.e_learning.IEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/e-learning/event")
public class EventController {

    @Autowired
    IEventService eventService;
    

    @PostMapping()
    public ResponseEntity<?> addEvent(@RequestParam("file") MultipartFile file, @RequestParam String event) throws JsonProcessingException {

        if(event.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Event e = objectMapper.readValue(event,Event.class);
            return new ResponseEntity<>(eventService.addEvent(file, e), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping()
    public ResponseEntity<?> updateEvent(@RequestParam("file") MultipartFile file, @RequestParam String event) throws Exception {
        if(event.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Event e = objectMapper.readValue(event,Event.class);
            return new ResponseEntity<>(eventService.updateEvent(file, e), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/updateWithOutImage")
    public ResponseEntity<?> updateEvent(@RequestParam String event) throws Exception {
        if(event.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Event e = objectMapper.readValue(event,Event.class);
            return new ResponseEntity<>(eventService.updateEventWithoutImage(e), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/registerToEvent/{eventId}")
    public ResponseEntity<?> registerToEvent(@PathVariable("eventId") Long eventId) {

        Event event = eventService.getEventById(eventId);
        try {
        	  eventService.registerToEvent(event);
              return new ResponseEntity<>(HttpStatus.OK);
        }catch(Exception e) {
        	return new ResponseEntity<String>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
      

    }

    @GetMapping("/getAllEvents")	
    public ResponseEntity<?> getAllEvents() {
    	List<Event> events = new ArrayList<>() ;
    	try {
            events = eventService.getAll();
            return new ResponseEntity<>(events, HttpStatus.OK);
    	}catch(InternalServerError e) {
    		return new ResponseEntity<>(events, HttpStatus.INTERNAL_SERVER_ERROR);
    	}
        
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOne(@PathVariable("id") Long id) {
        Event event = eventService.getEventById(id);

        if(event != null)
            return new ResponseEntity<>(event, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getEventsByUser/{idUser}")
    public ResponseEntity<?> getEventsByUser(@PathVariable("idUser") Long idUser) {
        List<Event> events = eventService.getEventsByUser(idUser);

        if(!events.isEmpty())
            return new ResponseEntity<>(events, HttpStatus.OK);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getUsersByEvent/{idEvent}")
    public ResponseEntity<?> getUsersByEvent(@PathVariable("idEvent") Long idEvent) {

        try {
            List<User> users = eventService.getUsersByEvent(idEvent);
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @GetMapping("/isUserRegisteredToEvent/{idEvent}")
    public ResponseEntity<?> isUserRegisteredToEvent(@PathVariable("idEvent") Long idEvent) {

        try {
            Boolean isRegistered = eventService.isUserRegisteredToEvent(idEvent);
            return new ResponseEntity<>(isRegistered, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEvent(@PathVariable("id") Long id) {
        try {
            eventService.deleteEvent(id);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @DeleteMapping("/deleteEventReservation/{eventId}")
    public ResponseEntity<?> deleteEventReservation(@PathVariable("eventId") Long eventId) {

        try {
            eventService.deleteEventReservation(eventId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
    
    @GetMapping("/getCountEvents/{id}")
    public ResponseEntity<?> getCountEventsByUserId(@PathVariable("id") Long id){
		return ResponseEntity.ok(eventService.getCountEventsByUserId(id));	
    }
}
