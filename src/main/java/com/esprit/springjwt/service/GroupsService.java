package com.esprit.springjwt.service;


import java.util.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.esprit.springjwt.entity.Etudiant;
import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Session;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.SessionRepository;
import com.esprit.springjwt.repository.UserRepository;

@Service
public class GroupsService {
    @Autowired
    private GroupsRepository groupsRepository;
    @Autowired
    private  SessionRepository sessionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    public GroupsService(GroupsRepository groupsRepository) {
        this.groupsRepository = groupsRepository;
    }
    
    @Transactional
    public Groups addGroups(Groups groups) {
        return groupsRepository.save(groups);
    }

    public List<Groups> getAllGroups() {
        return groupsRepository.findAll();
    }

    public Groups updateGroups(Groups groups) {
        return groupsRepository.save(groups);
    }

    public Groups getGroupsById(Long id) {
        Optional<Groups> optionalGroups = groupsRepository.findById(id);
        return optionalGroups.orElse(null);
    }
    @Transactional
    public void deleteGroups(Long groupId) {
        Groups group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        // Remove the relationship between the group and students
        group.getEtudiants().forEach(etudiant -> etudiant.getGroups().remove(group));
        group.getEtudiants().clear();

        // Delete the sessions associated with the group
        group.getSessions().forEach(session -> session.getGroups().remove(group));
        group.getSessions().clear();

        // Delete the group
        groupsRepository.delete(group);
    }

    public List<Groups> getGroupsByFormation(Long Id) {
        return groupsRepository.findByFormationId(Id);
    }
    public List<Groups> getGroupsByIds(List<Long> groupIds) {
        return groupsRepository.findAllById(groupIds);
    }
    public List<Groups> getGroupsBySessionId(Long sessionId) {
        Optional<Session> sessionOptional = sessionRepository.findById(sessionId);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            return session.getGroups();
        }
        return Collections.emptyList();
    }


    @Transactional
    public User removeEtudiantFromGroup(Long groupId, Long etudiantId) {
        Groups group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Etudiant not found"));

        if (!group.getEtudiants().contains(user)) {
            throw new RuntimeException("Etudiant is not a member of the group");
        }

        group.getEtudiants().remove(user);
        user.getGroups().remove(group);

        groupsRepository.save(group);

        return  userRepository.save(user);
    }
    public List<Groups> getGroupsByFormateurId(Long formateurId) {
        return groupsRepository.findByFormateurId(formateurId);
    }



    public boolean checkIfGroupNameExists(String groupName) {
        return groupsRepository.existsByGroupNameIgnoreCase(groupName);
    }


    @Transactional
    public User addEtudiantToGroup(Long groupId, Long etudiantId) {
        Groups group = groupsRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));

        User user = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Etudiant not found"));

        /*if (etudiant.getGroups().size() >= 3) {
            throw new RuntimeException("Etudiant has already reached the maximum number of groups");
        }*/

        if (group.getEtudiants().contains(user)) {
            throw new RuntimeException("Etudiant is already a member of the group");
        }

        user.getGroups().add(group);
        group.getEtudiants().add(user);

        User u=userRepository.save(user);
        groupsRepository.save(group);
        return  u;
    }

//    User user = userRepository.findById(etudiantId)
//            .orElseThrow(() -> new RuntimeException("Etudiant not found"));
//
//        if (!group.getEtudiants().contains(user)) {
//        throw new RuntimeException("Etudiant is not a member of the group");
//    }
//
//        group.getEtudiants().remove(user);
//        user.getGroups().remove(group);
//
//        groupsRepository.save(group);
//        userRepository.save(user);
//}

//    public List<Groups> getGroupsByFormateurId(Long formateurId) {
//        return groupsRepository.findByFormateurId(formateurId);
//    }
    
    public Integer getCountMembersByGroupId(Long id) {
		return groupsRepository.getCountMembersByGroupId(id);	
    }
    
    public List<Groups> getGroupsByStudentId(Long id) throws Exception{
    	List<Groups> groups = groupsRepository.getGroupsByStudentId(id);
    	if(groups.size()==0) {
    		throw new Exception("No groups found");
    	}
    	return groups;
    }
}
