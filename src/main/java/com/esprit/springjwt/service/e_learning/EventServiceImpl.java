package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.entity.e_learning.Event;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.repository.e_learning.IEventRepository;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import com.esprit.springjwt.service.NotificationService;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class EventServiceImpl implements IEventService{

    @Autowired
    IEventRepository eventRepository;

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    UserRepository userRepository;

    @Value("${files.folder}")
    String filesFolder;

    @Override
    public Event addEvent(MultipartFile file, Event e) {


        String contentType = file.getContentType();
        if (contentType != null && (
                contentType.equals("image/jpeg") ||
                contentType.equals("image/jpg") ||
                contentType.equals("image/png") ||
                contentType.equals("image/avif")
        )) {
            boolean isExit = new File(filesFolder + "/Events/").exists();
            if(!isExit)
                new File (filesFolder + "/Events/").mkdir();

            String fileName = file.getOriginalFilename();

            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String timestampedFileName = timestamp + "_" + fileName;

            try {
                System.out.print(timestampedFileName + "Uploading...\n");
                FileUtils.writeByteArrayToFile(new File(filesFolder + "/Events/" + timestampedFileName),file.getBytes());
                System.out.print("\nUploaded !!!\n");
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            e.setImage(timestampedFileName);
            notificationService.sendNotifToAllUsers("Exciting Announcement: New Event Coming Soon! Register Now, Limited Spots Available!", "./events/eventDetails/"+e.getId(), "New event");
            return eventRepository.save(e);
        } else  {
            throw new IllegalArgumentException("Invalid file type");
        }
    }

    @Override
    public Event updateEvent(MultipartFile file, Event e) throws Exception {
        if(eventRepository.findById(e.getId()).isPresent()) {
            String contentType = file.getContentType();
            if (contentType != null && (
                    contentType.equals("image/jpeg") ||
                            contentType.equals("image/jpg") ||
                            contentType.equals("image/png") ||
                            contentType.equals("image/avif")
            )) {
                boolean isExit = new File(filesFolder + "/Events/").exists();
                if (!isExit)
                    throw new FileNotFoundException("Events File not found !!");


                //Deleting old image
                log.info("Deleting...");
                File originalImage = new File(filesFolder + "/Events/" + eventRepository.getImageById(e.getId()));
                if (originalImage.delete())
                    log.info("image deleted successfully");
                else
                    log.error("Error while deleting old image");

                String fileName = file.getOriginalFilename();

                LocalDateTime now = LocalDateTime.now();
                String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String timestampedFileName = timestamp + "_" + fileName;

                try {
                    System.out.print(timestampedFileName + "Uploading...\n");
                    FileUtils.writeByteArrayToFile(new File(filesFolder + "/Events/" + timestampedFileName), file.getBytes());
                    System.out.print("\nUploaded !!!\n");
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                e.setImage(timestampedFileName);
                return eventRepository.save(e);
            } else {
                throw new IllegalArgumentException("Invalid file type");
            }
        }else {
            throw new Exception("Invalid event");
        }
    }

    @Override
    public List<Event> getAll() {
        return eventRepository.getAllEvents();
    }

    @Override
    public Event getEventById(Long id) {
        return eventRepository.findById(id).get();
    }

    @Override
    public void deleteEvent(Long id)  {
        System.out.println("Deleting......");
        File originalImage = new File( filesFolder + "/Events/" + eventRepository.getImageById(id));
        System.out.println( originalImage.delete());

        eventRepository.deleteById(id);
    }

    @Override
    public Event updateEventWithoutImage(Event e) {

        e.setImage(eventRepository.getImageById(e.getId()));
        return eventRepository.save(e);
    }
    private static final Logger logger = LoggerFactory.getLogger(EventServiceImpl.class);
    @Override
    @Transactional
    public void registerToEvent(Event event) throws Exception {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();
        Event Event = eventRepository.getEvent(event.getId());
        logger.info("User ID: {}", userId);
        logger.info("Event ID from input: {}", event.getId());

        if(Event==null) {
        	 throw new Exception("Event not found.");
        }

        User user = userRepository.findById(userId).orElseThrow(null);

        List<User> users= new ArrayList<>();
        users=Event.getUsers();
        users.add(user);
        Event.setUsers(users);
        eventRepository.save(Event);
    }


    @Override
    public List<Event> getEventsByUser(Long idUser) {

        User user = userRepository.findById(idUser).orElseThrow(() -> new RecordNotFoundException("User not found with id :" + idUser));
        List<Event> events = eventRepository.getEventByUser(idUser);

        //return eventRepository.findEventsByUsers(user);
        return events;
    }

    @Override
    public void deleteEventReservation(Long eventId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        User user = userRepository.findById(userId).orElseThrow(null);

        Event event = eventRepository.findById(eventId).orElseThrow(() -> new RecordNotFoundException("Event not found"));
        event.getUsers().remove(user);
        eventRepository.save(event);
    }

    @Override
    public List<User> getUsersByEvent(Long idEvent) {
        Event event = eventRepository.findById(idEvent).orElseThrow(() -> new RecordNotFoundException("Event not found"));

        if(event.getUsers().isEmpty())
            throw new RecordNotFoundException("No user is attending");

        return new ArrayList<>(event.getUsers());
    }

    @Override
    public Boolean isUserRegisteredToEvent(Long idEvent) {
        Event event = eventRepository.findById(idEvent).orElseThrow(() -> new RecordNotFoundException("Event not found"));

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        Long userId = userDetails.getId();

        if(!event.getUsers().isEmpty()){
            for (User u: event.getUsers()) {
                if(u.getId() == userId)
                    return true;
            }
        }

        return false;
    }
    
    @Override
    public int getCountEventsByUserId(Long id) {
		return eventRepository.getCountEventsByUserId(id);
    }
    
}
