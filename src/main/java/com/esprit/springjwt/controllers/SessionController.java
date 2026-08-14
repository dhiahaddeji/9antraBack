package com.esprit.springjwt.controllers;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.payload.response.MessageResponse;
import com.esprit.springjwt.repository.FormateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Session;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import com.esprit.springjwt.service.SessionService;
import com.esprit.springjwt.service.GoogleMeetService;
import com.esprit.springjwt.service.EmailService;

@RestController
@RequestMapping("/api/sessions")
@CrossOrigin(origins = "*")
public class SessionController {

    private SessionService SessionService;
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private FormateurRepository formateurRepository;
    @Autowired
    private GoogleMeetService googleMeetService;
    @Autowired
    private EmailService emailService;



    @Autowired
    public SessionController(SessionService sessionService) {
        this.SessionService = sessionService;
    }





    @GetMapping("/allSession")
    public List<Session> getAllSession() {
        return SessionService.getAllSession();
    }

    @PostMapping("/addSession")
    @PreAuthorize("hasAuthority('ADMINISTRATEUR')")
    public ResponseEntity<?> addSession(@RequestBody Session session, @RequestParam("groupIds") List<Long> groupIds) {
        try {
            List<Groups> groups = SessionService.getGroupsByIds(groupIds);
            session.setGroups(groups);

            // Sessions are admin-created now; derive the coach from the assigned group's formateur
            Formateur formateur = null;
            if (!groups.isEmpty() && groups.get(0).getFormateur() != null) {
                formateur = formateurRepository.findByUserId(groups.get(0).getFormateur().getId());
            }
            if (formateur != null) {
                session.setFormateur(formateur);
            }

            // Collect attendee emails: admin creator + coach + all students across all groups
            List<String> attendeeEmails = new ArrayList<>();
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetailsImpl) {
                attendeeEmails.add(((UserDetailsImpl) principal).getUsername());
            }
            if (formateur != null && formateur.getUser() != null) {
                attendeeEmails.add(formateur.getUser().getUsername());
            }
            for (Groups g : groups) {
                if (g.getEtudiants() != null) {
                    g.getEtudiants().stream()
                        .filter(u -> u != null && u.getUsername() != null)
                        .map(u -> u.getUsername())
                        .forEach(attendeeEmails::add);
                }
            }

            // Create Google Calendar event and attendee invites (admin-supplied meet link, if any)
            java.util.Map<String, String> calResult = googleMeetService.createSessionEvent(
                session.getSessionName(),
                session.getDescription(),
                session.getStartDate(),
                session.getFinishDate(),
                attendeeEmails,
                session.getMeetLink()
            );
            if (calResult.containsKey("meetLink") && (session.getMeetLink() == null || session.getMeetLink().isBlank())) {
                session.setMeetLink(calResult.get("meetLink"));
            }
            if (calResult.containsKey("calendarEventId")) {
                session.setCalendarEventId(calResult.get("calendarEventId"));
            }

            Session savedSession = SessionService.addSession(session);

            // Use the final meet link stored on the saved session (Google Calendar or manually entered)
            String finalMeetLink = savedSession.getMeetLink();
            System.out.println("[Session] finalMeetLink for email: " + finalMeetLink);

            // Send ICS calendar invite email to all attendees
            emailService.sendCalendarInvite(
                savedSession.getSessionName(),
                savedSession.getDescription(),
                savedSession.getStartDate(),
                savedSession.getFinishDate(),
                finalMeetLink,
                attendeeEmails
            );

            return ResponseEntity.ok(savedSession);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/getSessionById/{id}")
    public Session getSessionById(@PathVariable("id") Long id) {
        return SessionService.getSessionById(id);
    }

    @PutMapping("/updateSession")
    public Session updateSession(@RequestBody Session Session) {
        if (Session.getCalendarEventId() != null) {
            googleMeetService.updateCalendarEvent(
                Session.getCalendarEventId(),
                Session.getSessionName(),
                Session.getDescription(),
                Session.getStartDate(),
                Session.getFinishDate()
            );
        }
        return SessionService.updateSession(Session);
    }

    @DeleteMapping("/deleteSession/{id}")
    public void deleteSession(@PathVariable("id") Long id) {
        Session session = SessionService.getSessionById(id);
        if (session != null && session.getCalendarEventId() != null) {
            googleMeetService.deleteCalendarEvent(session.getCalendarEventId());
        }
        SessionService.deleteSession(id);
    }

    @PatchMapping("/{id}/meetLink")
    public ResponseEntity<?> updateMeetLink(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String meetLink = body.get("meetLink");
        if (meetLink == null || !meetLink.startsWith("https://meet.google.com/")) {
            return ResponseEntity.badRequest().body("Invalid Google Meet URL");
        }
        return SessionService.getSessionById(id) != null
            ? ResponseEntity.ok(SessionService.updateMeetLink(id, meetLink))
            : ResponseEntity.notFound().build();
    }


    @GetMapping("/{sessionId}/groups/{groupId}/users/{userId}/markPresence")
    public ResponseEntity<String> markUserPresence(
            @PathVariable Long sessionId,
            @PathVariable Long groupId,
            @PathVariable Long userId,
            @RequestParam boolean isPresent
    ) {
        try {
            SessionService.markUserPresence(sessionId, groupId, userId, isPresent);
            return ResponseEntity.ok("User presence status updated successfully.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("An error occurred while updating user presence status.");
        }
    }

   @GetMapping("/{sessionId}/groups/{groupId}/users/{userId}/presence")
   public ResponseEntity<Object> markUserPresences(
           @PathVariable Long sessionId,
           @PathVariable Long groupId,
           @PathVariable Long userId,
           @RequestParam boolean isPresent
   ) {
       try {
           SessionService.markUserPresences(sessionId, groupId, userId, isPresent);
           return ResponseEntity.ok().build(); // Return 200 OK with an empty response body
       } catch (IllegalArgumentException e) {
           Map<String, String> errorResponse = new HashMap<>();
           errorResponse.put("error", e.getMessage());
           return ResponseEntity.badRequest().body(errorResponse);
       }
   }
    @GetMapping("/{sessionId}/userPresenceStatus")
    public Map<Long, Boolean> getUserPresenceStatusBySessionId(@PathVariable Long sessionId) {
        return SessionService.getUserPresenceStatusBySessionId(sessionId);
    }
    @GetMapping("/date/{date}")
    public List<Session> getSessionsByDate(@PathVariable("date") @DateTimeFormat(pattern = "yyyy-MM-dd")Date date) {
        return SessionService.getSessionsByDate(date);
    }
    @GetMapping("/users/{userId}")
    public List<Session> getSessionsByUserId(@PathVariable Long userId) {
        return SessionService.getSessionsByUserId(userId);
    }
    @GetMapping("/formateur/{formateurId}")
    public List<Session> getSessionsByFormateurId(@PathVariable Long formateurId) {
        return SessionService.getSessionsByFormateurId(formateurId);
    }
    @GetMapping("/byGroupId/{groupId}")
    public List<Session> getSessionsByGroupId(@PathVariable Long groupId) {
        return SessionService.getSessionsByGroupId(groupId);
    }

    @GetMapping("/byGeneratedLink/{generatedLink}")
    public Session getSessionByGeneratedLink(@PathVariable String generatedLink) {
        System.out.println("generatedLink = " + generatedLink);
        return SessionService.getSessionsByGeneratedLink(generatedLink);
    }
    
    @GetMapping("/byFormationId/{id}")
    public ResponseEntity<?> getSessionByFormationId(@PathVariable("id") String id){
    	
		return ResponseEntity.ok(SessionService.getSessionsForUserByReques(id));
    	
    }
    
    @GetMapping("/byFormationCoachId/{id}")
    public ResponseEntity<?> getSessionByFromationCoachId(@PathVariable("id") Long id){
    	
		return ResponseEntity.ok(SessionService.getSessionByFromationCoachId(id));
    	
    }
}
