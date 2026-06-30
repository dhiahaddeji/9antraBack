package com.esprit.springjwt.service;



import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.entity.Projects;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.FormateurRepository;
import com.esprit.springjwt.repository.ProjectsRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

@Service
public class ProjectsService {
    @Autowired
    private ProjectsRepository projectsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FormateurRepository formateurRepository;
    
    @Autowired
    private NotificationService notificationService;

    @Value("${files.folder}")
    String filesFolder;

   /* public Projects addProjects(Projects projects){ return projectsRepository.save(projects);
    }*/

    public List<Projects> getProjectsByUser(User user) {
        return projectsRepository.findByUser(user);
    }

    public List<Projects> getAllProjects() {
        return projectsRepository.findAll();
    }
    public List<Projects> getAllProjectsStudent() {

        Long userIds;
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            userIds = userDetails.getId(); // Assuming you have a method to get the user's ID
        } else {
           userIds = 2L; // Replace with an appropriate default user ID
        }
        // Retrieve the user from the userRepository using the user's ID
        User user = userRepository.findById(userIds).orElse(null);
        if (user == null) {
            // Handle the case where the user does not exist
            throw new IllegalArgumentException("User not found");
        }

        return projectsRepository.findByUser(user);
    }
    public List<Projects> getAllProjectsStudent(Long id) {
 if(id!=null){
        User user = userRepository.findById(id).orElse(null);
        return projectsRepository.findByUser(user);
    }
 return null;
    }

    public Projects addProject(MultipartFile file) {
        // Vérifier si le fichier est de type ZIP, RAR, docx, jpg, jpeg, png, ppt ou pdf
        String contentType = file.getContentType();
        if (contentType != null && (
                contentType.equals("application/zip") ||
                        contentType.equals("application/x-rar-compressed") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("application/vnd.ms-powerpoint") ||
                        contentType.equals("application/pdf")
        )) {
            try {
                // Récupérer le contenu du fichier
                byte[] content = file.getBytes();

                Long userIds;
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication.getPrincipal() instanceof UserDetailsImpl) {
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    userIds = userDetails.getId(); // Assuming you have a method to get the user's ID
                } else {
                    // Use a default value if the principal is not available
                    userIds = 2L; // Replace with an appropriate default user ID
                }

// Retrieve the user from the userRepository using the user's ID
                User user = userRepository.findById(userIds).orElse(null);
                if (user == null) {
                    // Handle the case where the user does not exist
                    throw new IllegalArgumentException("User not found");
                }







                        // Créer un nouveau projet
                Projects project = new Projects();

                // Utiliser le nom du fichier comme base pour le nom de projet
                String fileName = file.getOriginalFilename();

                // Ajouter un timestamp au nom du fichier
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String timestamp = now.format(formatter);
                String timestampedFileName = timestamp + "_" + fileName;

                // Utiliser le nom de fichier avec timestamp comme nom de projet
                //   project.setProjectname(timestampedFileName);

                // Définir le type du projet en fonction de l'extension du fichier
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (fileExtension.equals("zip")) {
                    project.setType("ZIP");
                } else if (fileExtension.equals("rar")) {
                    project.setType("RAR");
                } else if (fileExtension.equals("docx")) {
                    project.setType("DOCX");
                } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
                    project.setType("IMAGE");
                } else if (fileExtension.equals("xls")) {
                    project.setType("XLS");
                } else if (fileExtension.equals("pdf")) {
                    project.setType("PDF");
                } else {
                    // Gérer le cas où l'extension n'est pas autorisée
                    throw new IllegalArgumentException("Invalid file extension");
                }

                // Définir la date du projet comme la date actuelle
                project.setDate(new Date());

                // Définir la taille du projet
                long fileSize = file.getSize();

                // Convertir la taille en un format lisible par l'humain
                String size;
                if (fileSize < 1024) {
                    size = fileSize + " B";
                } else if (fileSize < 1024 * 1024) {
                    size = fileSize / 1024 + " KB";
                } else {
                    size = fileSize / (1024 * 1024) + " MB";
                }
                project.setSize(size);


                // Obtenir l'ID de l'utilisateur
                Long userId = user.getId();

                // Créer le nom du dossier en utilisant le nom et l'ID de l'utilisateur
                String userFolderName = user.getLastName() + "_" + userId;
                String userm = userFolderName +"/"+ timestampedFileName;
                project.setProjectname(userm);
                // Définir le chemin du dossier utilisateur
                //String userFolderPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\projects\\" + userFolderName;
                String userFolderPath = filesFolder + "\\projects\\" + userFolderName;
                //  String userFolderPath = "C:\\Users\\Wale\\Desktop\\Final Design\\bridge\\src\\assets\\Projects\\" + userFolderName;


                // Créer le dossier utilisateur
                File userFolder = new File(userFolderPath);
                if (!userFolder.exists()) {
                    boolean created = userFolder.mkdirs();
                    if (!created) {
                        throw new RuntimeException("Failed to create user folder");
                    }
                }

                // Définir le chemin complet du fichier du projet dans le dossier utilisateur
                String projectFilePath = userFolderPath + "\\" + timestampedFileName;
                Path destinationPath = Paths.get(projectFilePath);

                // Copier le fichier du projet dans le dossier utilisateur
                Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);


                // Assigner l'utilisateur au projet
                project.setUser(user);
                project.setNom(user.getLastName());
                project.setPrenom(user.getFirstName());
                project.setMail(user.getUsername());
                project.setTypeF(user.getTypeFormation());
                project.setImage(user.getImage());

                // Enregistrer le projet
                Projects savedProject = projectsRepository.save(project);
               
                return savedProject;
            } catch (IOException e) {
                // Gérer les erreurs lors de la lecture du fichier
                e.printStackTrace();
                throw new RuntimeException("Error processing file");
            }
        } else {
            // Gérer les erreurs si le fichier n'est pas autorisé
            throw new IllegalArgumentException("Invalid file type");
        }
    }
    public Projects addProject2(MultipartFile file,Long id) {
        // Vérifier si le fichier est de type ZIP, RAR, docx, jpg, jpeg, png, ppt ou pdf
        String contentType = file.getContentType();
        if (contentType != null && (
                contentType.equals("application/zip") ||
                        contentType.equals("application/x-rar-compressed") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("application/vnd.ms-powerpoint") ||
                        contentType.equals("application/pdf")
        )) {
            try {
                // Récupérer le contenu du fichier
                byte[] content = file.getBytes();

// Retrieve the user from the userRepository using the user's ID
                User user = userRepository.findById(id).orElse(null);
                if (user == null) {
                    // Handle the case where the user does not exist
                    throw new IllegalArgumentException("User not found");
                }







                // Créer un nouveau projet
                Projects project = new Projects();

                // Utiliser le nom du fichier comme base pour le nom de projet
                String fileName = file.getOriginalFilename();

                // Ajouter un timestamp au nom du fichier
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String timestamp = now.format(formatter);
                String timestampedFileName = timestamp + "_" + fileName;

                // Utiliser le nom de fichier avec timestamp comme nom de projet
                //   project.setProjectname(timestampedFileName);

                // Définir le type du projet en fonction de l'extension du fichier
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (fileExtension.equals("zip")) {
                    project.setType("ZIP");
                } else if (fileExtension.equals("rar")) {
                    project.setType("RAR");
                } else if (fileExtension.equals("docx")) {
                    project.setType("DOCX");
                } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
                    project.setType("IMAGE");
                } else if (fileExtension.equals("xls")) {
                    project.setType("XLS");
                } else if (fileExtension.equals("pdf")) {
                    project.setType("PDF");
                } else {
                    // Gérer le cas où l'extension n'est pas autorisée
                    throw new IllegalArgumentException("Invalid file extension");
                }

                // Définir la date du projet comme la date actuelle
                project.setDate(new Date());

                // Définir la taille du projet
                long fileSize = file.getSize();

                // Convertir la taille en un format lisible par l'humain
                String size;
                if (fileSize < 1024) {
                    size = fileSize + " B";
                } else if (fileSize < 1024 * 1024) {
                    size = fileSize / 1024 + " KB";
                } else {
                    size = fileSize / (1024 * 1024) + " MB";
                }
                project.setSize(size);


                // Obtenir l'ID de l'utilisateur
                Long userId = user.getId();

                // Créer le nom du dossier en utilisant le nom et l'ID de l'utilisateur
                String userFolderName = user.getLastName() + "_" + userId;
                String userm = userFolderName +"/"+ timestampedFileName;
                project.setProjectname(userm);
                // Définir le chemin du dossier utilisateur
                //String userFolderPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\projects\\" + userFolderName;
                String userFolderPath = filesFolder + "\\projects\\" + userFolderName;
                //  String userFolderPath = "C:\\Users\\Wale\\Desktop\\Final Design\\bridge\\src\\assets\\Projects\\" + userFolderName;


                // Créer le dossier utilisateur
                File userFolder = new File(userFolderPath);
                if (!userFolder.exists()) {
                    boolean created = userFolder.mkdirs();
                    if (!created) {
                        throw new RuntimeException("Failed to create user folder");
                    }
                }

                // Définir le chemin complet du fichier du projet dans le dossier utilisateur
                String projectFilePath = userFolderPath + "\\" + timestampedFileName;
                Path destinationPath = Paths.get(projectFilePath);

                // Copier le fichier du projet dans le dossier utilisateur
                Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);


                // Assigner l'utilisateur au projet
                project.setUser(user);
                project.setNom(user.getLastName());
                project.setPrenom(user.getFirstName());
                project.setMail(user.getUsername());
                project.setTypeF(user.getTypeFormation());
                project.setImage(user.getImage());


                // Enregistrer le projet
                Projects savedProject = projectsRepository.save(project);

                return savedProject;
            } catch (IOException e) {
                // Gérer les erreurs lors de la lecture du fichier
                e.printStackTrace();
                throw new RuntimeException("Error processing file");
            }
        } else {
            // Gérer les erreurs si le fichier n'est pas autorisé
            throw new IllegalArgumentException("Invalid file type");
        }
    }
    public Projects updateProject(Long projectId, MultipartFile file) {
        // Vérifier si le projet existe
        Projects project = projectsRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        // Vérifier si le fichier est de type ZIP, RAR, docx, jpg, jpeg, png, ppt ou pdf
        String contentType = file.getContentType();
        if (contentType != null && (
                contentType.equals("application/zip") ||
                        contentType.equals("application/x-rar-compressed") ||
                        contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/jpg") ||
                        contentType.equals("image/png") ||
                        contentType.equals("application/vnd.ms-powerpoint") ||
                        contentType.equals("application/pdf")
        )) {
            try {
                // Récupérer le contenu du fichier
                byte[] content = file.getBytes();
                Long userIds;
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication.getPrincipal() instanceof UserDetailsImpl) {
                    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
                    userIds = userDetails.getId(); // Assuming you have a method to get the user's ID
                } else {
                    // Use a default value if the principal is not available
                    userIds = 2L; // Replace with an appropriate default user ID
                }

// Retrieve the user from the userRepository using the user's ID
                User user = userRepository.findById(userIds).orElse(null);
                if (user == null) {
                    // Handle the case where the user does not exist
                    throw new IllegalArgumentException("User not found");
                }
                // Utiliser le nom du fichier comme base pour le nom de projet
                String fileName = file.getOriginalFilename();

                // Ajouter un timestamp au nom du fichier
                LocalDateTime now = LocalDateTime.now();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
                String timestamp = now.format(formatter);
                String timestampedFileName = timestamp + "_" + fileName;

                // Définir le type du projet en fonction de l'extension du fichier
                String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                if (fileExtension.equals("zip")) {
                    project.setType("ZIP");
                } else if (fileExtension.equals("rar")) {
                    project.setType("RAR");
                } else if (fileExtension.equals("docx")) {
                    project.setType("DOCX");
                } else if (fileExtension.equals("jpg") || fileExtension.equals("jpeg") || fileExtension.equals("png")) {
                    project.setType("IMAGE");
                } else if (fileExtension.equals("ppt")) {
                    project.setType("PPT");
                } else if (fileExtension.equals("pdf")) {
                    project.setType("PDF");
                } else {
                    // Gérer le cas où l'extension n'est pas autorisée
                    throw new IllegalArgumentException("Invalid file extension");
                }

                // Définir la date du projet comme la date actuelle
                project.setDate(new Date());

                // Définir la taille du projet
                long fileSize = file.getSize();

                // Convertir la taille en un format lisible par l'humain
                String size;
                if (fileSize < 1024) {
                    size = fileSize + " B";
                } else if (fileSize < 1024 * 1024) {
                    size = fileSize / 1024 + " KB";
                } else {
                    size = fileSize / (1024 * 1024) + " MB";
                }
                project.setSize(size);

                // Obtenir l'ID de l'utilisateur
                Long userId = project.getUser().getId();

                // Créer le nom du dossier en utilisant le nom et l'ID de l'utilisateur
                String userFolderName = project.getUser().getLastName() + "_" + userId;

                // Définir le chemin du dossier utilisateur
                //String userFolderPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\projects\\" + userFolderName;
                String userFolderPath = filesFolder + "\\projects\\" + userFolderName;
       //         String userFolderPath = "C:\\Users\\Wale\\Desktop\\Final Design\\bridge\\src\\assets\\Projects\\" + userFolderName;


                // Définir le chemin complet du fichier du projet dans le dossier utilisateur
                String projectFilePath = userFolderPath + "\\" + timestampedFileName;
                Path destinationPath = Paths.get(projectFilePath);

                // Supprimer l'ancien fichier du projet
                String oldFilePath = userFolderPath + "\\" + project.getProjectname();
                Path oldProjectPath = Paths.get(oldFilePath);
                Files.deleteIfExists(oldProjectPath);

                // Copier le nouveau fichier du projet dans le dossier utilisateur
                Files.copy(file.getInputStream(), destinationPath, StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour les informations du projet
                project.setProjectname(timestampedFileName);

                // Enregistrer les modifications du projet
                Projects updatedProject = projectsRepository.save(project);

                return updatedProject;
            } catch (IOException e) {
                // Gérer les erreurs lors de la lecture du fichier
                e.printStackTrace();
                throw new RuntimeException("Error processing file");
            }
        } else {
            // Gérer les erreurs si le fichier n'est pas autorisé
            throw new IllegalArgumentException("Invalid file type");
        }
    }

    public Projects getProjectsById(Long id){return projectsRepository.findById(id).get();
    }
    public void deleteProjects(Long id) {
        Projects project = projectsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        String fileName = project.getProjectname();

        // Supprimer le fichier du dossier utilisateur
        String userFolderName = project.getUser().getLastName() + "_" + project.getUser().getId();
        //String userFolderPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\projects\\" + userFolderName;
        String userFolderPath = filesFolder + "\\projects\\" + userFolderName;
       // String userFolderPath = "C:\\Users\\Wale\\Desktop\\Final Design\\bridge\\src\\assets\\Projects\\" + userFolderName;

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
        projectsRepository.deleteById(id);
    }


}


