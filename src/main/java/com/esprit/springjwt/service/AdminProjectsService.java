package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.repository.AdminProjectsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service

public class AdminProjectsService {
    @Autowired
    private AdminProjectsRepository adminProjectsRepository;

    @Value("${files.folder}")
    String filesFolder;

    public AdminProjects save(AdminProjects adminProjects) {
        return adminProjectsRepository.save(adminProjects);
    }
    public List<AdminProjects> getAll() {
        return (List<AdminProjects>) adminProjectsRepository.findAll();
    }
    public void delete(Long id) {
        adminProjectsRepository.deleteById(id);
    }
    public AdminProjects Update(AdminProjects E, Long id) {
        E.setId(id);
        return adminProjectsRepository.save(E);
    }
    public Optional<AdminProjects> findById(Long id) {
        return adminProjectsRepository.findById(id);
    }
    public void deleteProjects(Long id) {
        AdminProjects project = adminProjectsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        String fileName = project.getImage();

        // Supprimer le fichier du dossier utilisateur
        //String userFolderPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" ;
        String userFolderPath = filesFolder + "\\adminProjects\\";
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
        adminProjectsRepository.deleteById(id);
    }

}
