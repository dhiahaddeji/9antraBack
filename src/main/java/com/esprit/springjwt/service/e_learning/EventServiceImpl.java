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
            File eventsDir = new File(filesFolder + "/Events/");
            if (!eventsDir.exists()) {
                boolean dirCreated = eventsDir.mkdirs(); // Use mkdirs() instead of mkdir()
                if (!dirCreated) {
                    throw new RuntimeException("Failed to create Events directory at: " + eventsDir.getAbsolutePath());
                }
                log.info("✓ Created Events directory: {}", eventsDir.getAbsolutePath());
            }

            String fileName = file.getOriginalFilename();
            LocalDateTime now = LocalDateTime.now();
            String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            String timestampedFileName = timestamp + "_" + fileName;
            String filePath = filesFolder + "/Events/" + timestampedFileName;

            try {
                log.info("📁 Saving event image to: {}", filePath);
                FileUtils.writeByteArrayToFile(new File(filePath), file.getBytes());
                log.info("✓ Image uploaded successfully: {}", timestampedFileName);
            } catch (IOException ex) {
                log.error("✗ Error saving image: {}", ex.getMessage());
                ex.printStackTrace();
                throw new RuntimeException("Failed to save event image: " + ex.getMessage());
            }

            e.setImage(timestampedFileName);
            Event savedEvent = eventRepository.save(e);
            log.info("✓ Event saved to database with image: {}", timestampedFileName);
            
            notificationService.sendNotifToAllUsers(
                "Exciting Announcement: New Event Coming Soon! Register Now, Limited Spots Available!", 
                "./events/eventDetails/"+e.getId(), 
                "New event"
            );
            return savedEvent;
        } else {
            throw new IllegalArgumentException("Invalid file type. Only JPEG, JPG, PNG, and AVIF are allowed.");
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
                File eventsDir = new File(filesFolder + "/Events/");
                if (!eventsDir.exists()) {
                    boolean dirCreated = eventsDir.mkdirs(); // Use mkdirs() instead of mkdir()
                    if (!dirCreated) {
                        throw new FileNotFoundException("Failed to create Events directory at: " + eventsDir.getAbsolutePath());
                    }
                }

                // Deleting old image
                log.info("🗑️  Deleting old image...");
                String oldImageName = eventRepository.getImageById(e.getId());
                if (oldImageName != null && !oldImageName.isEmpty()) {
                    File originalImage = new File(filesFolder + "/Events/" + oldImageName);
                    if (originalImage.exists() && originalImage.delete()) {
                        log.info("✓ Old image deleted successfully: {}", oldImageName);
                    } else {
                        log.warn("⚠️  Could not delete old image: {}", oldImageName);
                    }
                }

                String fileName = file.getOriginalFilename();
                LocalDateTime now = LocalDateTime.now();
                String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
                String timestampedFileName = timestamp + "_" + fileName;
                String filePath = filesFolder + "/Events/" + timestampedFileName;

                try {
                    log.info("📁 Saving new event image to: {}", filePath);
                    FileUtils.writeByteArrayToFile(new File(filePath), file.getBytes());
                    log.info("✓ New image uploaded successfully: {}", timestampedFileName);
                } catch (IOException ex) {
                    log.error("✗ Error saving new image: {}", ex.getMessage());
                    ex.printStackTrace();
                    throw new RuntimeException("Failed to save event image: " + ex.getMessage());
                }

                e.setImage(timestampedFileName);
                Event updatedEvent = eventRepository.save(e);
                log.info("✓ Event updated with new image: {}", timestampedFileName);
                return updatedEvent;
            } else {
                throw new IllegalArgumentException("Invalid file type. Only JPEG, JPG, PNG, and AVIF are allowed.");
            }
        } else {
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
