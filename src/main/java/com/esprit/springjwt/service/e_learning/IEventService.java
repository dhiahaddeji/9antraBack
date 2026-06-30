package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.entity.e_learning.Event;

import org.springframework.web.client.HttpServerErrorException.InternalServerError;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEventService {

    Event addEvent(MultipartFile file, Event e);

    Event updateEvent(MultipartFile file, Event e) throws Exception;

    List<Event> getAll() throws InternalServerError;

    Event getEventById(Long id);

    void deleteEvent(Long id);

    Event updateEventWithoutImage(Event e);

    void registerToEvent(Event event) throws Exception;

    List<Event> getEventsByUser(Long IdUser);

    void deleteEventReservation(Long eventId);

    List<User> getUsersByEvent(Long idEvent);

    Boolean isUserRegisteredToEvent(Long idEvent);
    
    int getCountEventsByUserId(Long id);
}
