package com.esprit.springjwt.controllers;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.payload.response.MessageResponse;
import com.esprit.springjwt.repository.FormateurRepository;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Session;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.service.SessionService;
import com.esprit.springjwt.service.GoogleMeetService;

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
    public SessionController(SessionService sessionService) {
        this.SessionService = sessionService;
    }





    @GetMapping("/allSession")
    public List<Session> getAllSession() {
        return SessionService.getAllSession();
    }

    @PostMapping("/addSession")
    public ResponseEntity<?> addSession(@RequestBody Session session, @RequestParam("groupIds") List<Long> groupIds) {
        try {
            // Get current authenticated user (coach/formateur)
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
                return ResponseEntity.badRequest().body(new MessageResponse("Not authenticated!"));
            }
            
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            User currentUser = userDetails.getUser();
            
            // Set formateur only if the current user is a formateur; admins can create sessions without one
            Formateur formateur = formateurRepository.findByUserId(currentUser.getId());
            if (formateur != null) {
                session.setFormateur(formateur);
            }
            
            List<Groups> groups = SessionService.getGroupsByIds(groupIds);
            session.setGroups(groups);

            // Auto-generate Google Meet link if not already provided
            if (session.getMeetLink() == null || session.getMeetLink().isBlank()) {
                String meetLink = googleMeetService.createMeetLink(
                    session.getSessionName(),
                    session.getStartDate(),
                    session.getFinishDate()
                );
                if (meetLink != null) session.setMeetLink(meetLink);
            }

            Session savedSession = SessionService.addSession(session);
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
        return SessionService.updateSession(Session);
    }

    @DeleteMapping("/deleteSession/{id}")
    public void deleteSession(@PathVariable("id") Long id) {
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
