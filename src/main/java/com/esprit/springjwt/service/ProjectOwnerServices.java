package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.entity.Projects;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.repository.ProjectOwnerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service

public class ProjectOwnerServices {
    @Autowired
    private ProjectOwnerRepository projectOwnerRepository;

    @Value("${files.folder}")
    String filesFolder;

    public ProjectOwner save(ProjectOwner food) {
        return projectOwnerRepository.save(food);
    }
    public List<ProjectOwner> getAllProjectOwners() {
        return projectOwnerRepository.findAll();
    }
    public List<ProjectOwner> getClaimsByStatus(Boolean status) {
        return projectOwnerRepository.findByStatus(status);
    }
    public List<ProjectOwner> getAllActiveProjectOwners() {
        return projectOwnerRepository.findByStatus(true);
    }
    public Optional<ProjectOwner> findById(Long id) {
        return projectOwnerRepository.findById(id);
    }
    public void deleteProjectOwner(Long id) {
        projectOwnerRepository.deleteById(id);
    }
    public void     deleteProjects(Long id) {
        ProjectOwner project = projectOwnerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        String fileName = project.getImage();

        // Supprimer le fichier du dossier utilisateur
        //String userFolderPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\projectOwner\\" ;
        String userFolderPath = filesFolder + "\\projectOwner\\" ;
        String filePath = userFolderPath + "\\" + fileName;
        Path projectPath = Paths.get(filePath);

        try {
            Files.deleteIfExists(projectPath);
        } catch (IOException e) {
            // Gérer les erreurs lors de la suppression du fichier
            e.printStackTrace();
            throw new RuntimeException("Error deleting file");
        }

        // Supprimer l'entrée de la base de données
        projectOwnerRepository.deleteById(id);
    }
    public void updateStatus(Long id, boolean newValue) {
        // Recherche de l'objet MyClass correspondant à l'identifiant "id"
        ProjectOwner myObject = projectOwnerRepository.findById(id).orElse(null);

        // Vérification que l'objet a été trouvé
        if (myObject != null) {
            // Modification de la propriété "property2"
            myObject.setStatus(newValue);
            projectOwnerRepository.save(myObject);
        }

}}
