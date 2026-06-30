package com.esprit.springjwt.service;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.persistence.NonUniqueResultException;

import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Session;
import com.esprit.springjwt.entity.User;

@Service
public class SessionService {
	@Autowired
	private SessionRepository SessionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private GroupsRepository groupsRepository;
	@Resource
    private IRequestRepository requestRepository;
	@Autowired
	FormationRepository formationRepository;
	
    public SessionService(UserRepository userRepository, GroupsRepository groupRepository) {
        this.userRepository = userRepository;
        this.groupsRepository = groupRepository;
    }
	public Session addSession(Session session) {
	    List<Long> groupIds = session.getGroups().stream()
	            .map(Groups::getId)
	            .collect(Collectors.toList());

	    List<Groups> groups = groupsRepository.findAllById(groupIds);
	    session.setGroups(groups);

	    for (Groups group : groups) {
	        group.getSessions().add(session);
	    }


        session.setGeneratedLink(this.generateRandomWord());

	    return SessionRepository.save(session);
	}
    public List<Session> getAllSession(){
        return SessionRepository.findAll();
    }

    public Session updateSession(Session Session){
        return SessionRepository.save(Session);
    }
    public Session getSessionById(Long id){
        return SessionRepository.findById(id).get();
    }

    public void deleteSession(Long sessionId) {
        Session session = SessionRepository.findById(sessionId).orElse(null);
        if (session != null) {
            SessionRepository.delete(session);
        }
    }
    public List<Session> getSessionsByDate(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate nextDate = localDate.plusDays(1); 

        Date startDate = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(nextDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return SessionRepository.findByStartDateBetween(startDate, endDate);
    }
    public List<Groups> getGroupsByIds(List<Long> groupIds) {
        return groupsRepository.findAllById(groupIds);
    }
    /*public List<Session> getSessionsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return null;
        }
        List<Groups> groups = user.getGroups();
        List<Session> sessions = new ArrayList<>();
        for (Groups group : groups) {
            sessions.addAll(group.getSessions());
        }

        return sessions;
    }*/
    public List<Session> getSessionsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ArrayList<>();
        }

        List<Groups> groups = groupsRepository.findByEtudiantsContaining(user);
        Set<Session> uniqueSessions = new HashSet<>();

        // Get all sessions from all groups the student belongs to
        for (Groups group : groups) {
            if (group.getSessions() != null) {
                uniqueSessions.addAll(group.getSessions());
            }
        }

        return new ArrayList<>(uniqueSessions);
    }
    public List<Session> getSessionsByFormateurId(Long formateurId) {
        return SessionRepository.findByFormateurUserId(formateurId);
    }
    public List<Session> getSessionsByGroupId(Long groupId) {
        Groups group = groupsRepository.findById(groupId).orElse(null);
        if (group == null) {
            return null;
        }

        Set<Session> uniqueSessions = new HashSet<>(group.getSessions());

        return new ArrayList<>(uniqueSessions);
    }
    public Optional<Session> findById(Long id) {
        return SessionRepository.findById(id);
    }
    public void markUserPresence(Long sessionId, Long groupId, Long userId, boolean isPresent) {
        Session session = SessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));

        Groups group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        if (!group.getSessions().contains(session)) {
            throw new IllegalArgumentException("Group does not belong to the specified session.");
        }

        Map<Long, Boolean> userPresenceStatus = group.getUserPresenceStatus();
        userPresenceStatus.put(userId, isPresent);
        group.setUserPresenceStatus(userPresenceStatus);

        groupsRepository.save(group);
    }

  /*  public void markUserPresenced(Long sessionId, Long userId, boolean isPresent) {
        Session session = SessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Check if the user already has attendance record for the session
        Optional<SessionAttendance> attendanceOptional = session.getAttendanceList().stream()
                .filter(attendance -> attendance.getUser().getId().equals(userId))
                .findFirst();

        SessionAttendance attendance;
        if (attendanceOptional.isPresent()) {
            attendance = attendanceOptional.get();
            attendance.setPresent(isPresent);
        } else {
            attendance = new SessionAttendance();
            attendance.setSession(session);
            attendance.setUser(user);
            attendance.setPresent(isPresent);
            session.getAttendanceList().add(attendance);
        }

        SessionRepository.save(session);
    }*/

    public void markUserPresences(Long sessionId, Long groupId, Long userId, boolean isPresent) {
        Session session = SessionRepository.findById(sessionId)
                .orElseThrow(() -> new IllegalArgumentException("Session not found with id: " + sessionId));

        // Ensure that the group is associated with the specified session
        Groups group = session.getGroups().stream()
                .filter(g -> g.getId().equals(groupId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Group not found with id: " + groupId));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        // Check if the user is part of the group
        if (!group.getEtudiants().contains(user)) {
            throw new IllegalArgumentException("User is not assigned to the specified group.");
        }

        // Update the user's presence status in the session
        Map<Long, Boolean> userPresenceStatus = session.getUserPresence();
        userPresenceStatus.put(userId, isPresent);
        session.setUserPresence(userPresenceStatus);

        SessionRepository.save(session);
    }

    public Map<Long, Boolean> getUserPresenceStatusBySessionId(long sessionId) {
        Session session = SessionRepository.findById(sessionId).orElse(null);
        if (session == null) {
            // Session with the given ID not found
            return new HashMap<>();
        }
        return session.getUserPresence();

    }


    public static String generateRandomWord() {
        String characters = "abcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder word = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int randomIndex = random.nextInt(characters.length());
            word.append(characters.charAt(randomIndex));
        }

        return word.toString();
    }


    //getSessionsBy generatedLink
    public Session getSessionsByGeneratedLink(String generatedLink) {
        return SessionRepository.findByGeneratedLink(generatedLink);
    }
   
   
	public List<Session>getSessionsForUserByReques(String id) {
        List<Session> updatedsessions = new ArrayList<>();
        User user = userRepository.findByUsername(id);
        List<Session> sessions = SessionRepository.getSessionsForUserByRequest(id);
        List<Groups> groups = groupsRepository.findByEtudiantsContaining(user);
        for (Groups group : groups) {
            for (Session session : group.getSessions()) {
                if (sessions.contains(session)) {
                    updatedsessions.add(session);
                }
            }
        }
    		return updatedsessions;
    		
    }
	
	public List<Session> getSessionByFromationCoachId(Long id) {
        User formateur = userRepository.findById(id).orElse(null);
        if (formateur == null) {
            return null;
        }

        List<Groups> groups = groupsRepository.findByFormateur(formateur);
        Set<Session> uniqueSessions = new HashSet<>();
        Date currentDate =new Date();
        for (Groups group : groups) {
            for (Session session : group.getSessions()) {
                if (session.getStartDate().after(currentDate)) {
                    uniqueSessions.add(session);
                }
            }
        }

        return new ArrayList<>(uniqueSessions);
    }
}
