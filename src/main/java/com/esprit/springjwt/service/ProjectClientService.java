package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.repository.AdminProjectsRepository;
import com.esprit.springjwt.repository.ProjectClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service

public class ProjectClientService {
    @Autowired
    private ProjectClientRepository projectClientRepository;
    @Autowired
    private AdminProjectsRepository adminProjectsRepository;
    public ProjectClient save(ProjectClient projectClient) {
        return projectClientRepository.save(projectClient);
    }
    public Optional<ProjectClient> findById(Long id) {
        return projectClientRepository.findById(id);
    }
    public void delete(ProjectClient projectClient) {
        projectClientRepository.delete(projectClient);
    }

    public List<ProjectClient> getAll() {
     return    projectClientRepository.findAll();
    }
    public List<ProjectClient> findByAdminProjectId(Long adminProjectId) {

        return projectClientRepository.findByAdminProjectsId(adminProjectId);
    }

    public void updateStatus(Long id, boolean newValue) {
        // Recherche de l'objet MyClass correspondant à l'identifiant "id"
        ProjectClient myObject = projectClientRepository.findById(id).orElse(null);
        // Vérification que l'objet a été trouvé
        if (myObject != null) {
            // Modification de la propriété "property2"
            myObject.setStatus(newValue);
            projectClientRepository.save(myObject);
        }


    }
    public List<ProjectClient> getClaimsByStatus(Boolean status) {
        return projectClientRepository.findByStatus(status);
    }
    public List<ProjectClient> getAllClaimsSortedByDateAsc() {
        return projectClientRepository.findAllByOrderByDateAsc();
    }
    public List<ProjectClient> getAllClaimsSortedByDateDesc() {
        return projectClientRepository.findAllByOrderByDateDesc();
    }

}
