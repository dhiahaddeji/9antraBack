package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.entity.Projects;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.service.AdminProjectsService;
import com.esprit.springjwt.service.ProjectOwnerServices;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.fileupload.disk.DiskFileItem;
import org.apache.commons.io.FileUtils;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/AdminProjects")
@CrossOrigin("*")
public class AdminProjectsController {
    @Autowired
    private AdminProjectsService service;
    @Autowired
    private ProjectOwnerServices Catservice;

    @Value("${files.folder}")
    String filesFolder;

  /*  @PostMapping("/add")
    public AdminProjects create(@RequestParam("file") MultipartFile file,
                                @RequestParam("video") MultipartFile video,
                                @RequestParam("price") Float price,
                                @RequestParam("titre") String titre,
                                @RequestParam("technologies") String technologies,
                                @RequestParam("description") String description,

                                @RequestParam("projectOwnerId") Long projectOwnerId ) {

        try {
            // Vérifier si le fichier vidéo est vide
            if (video.isEmpty()) {
                throw new IllegalArgumentException("Video file is required");
            }

            // Vérifier si le fichier vidéo est au format MP4
            if (!video.getContentType().equals("video/mp4")) {
                throw new IllegalArgumentException("Invalid video file format. Only MP4 files are allowed.");
            }
            AdminProjects food = new AdminProjects();
            food.setTitre(titre);
            food.setDescription(description);
            food.setPrice(price);
            food.setTechnologies(technologies);
// Generate a timestamp for the video filename
            String videoTimestamp = Long.toString(System.currentTimeMillis());

// Set the destination path to save the video file
            String videoDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\";

// Create a new filename using the timestamp and original video filename
            String newVideoFilename = videoTimestamp + "_" + video.getOriginalFilename();

// Save the video file to the disk
            video.transferTo(new File(videoDestinationPath + newVideoFilename));

// Assign the new video filename to the "video" attribute of the AdminProjects object
            food.setVideo(newVideoFilename);

            // Generate a timestamp for the image filename
            String timestamp = Long.toString(System.currentTimeMillis());

            // Set the destination path to save the image
            String destinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" ;

            // Create a new filename using the timestamp and original filename
            String newFilename = timestamp + "_" + file.getOriginalFilename();

            // Save the file to the disk
            file.transferTo(new File(destinationPath + newFilename));
            // Assign the new filename to the "image" attribute of the Food object
            food.setImage(newFilename);
            ProjectOwner category = Catservice.findById(projectOwnerId)
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + projectOwnerId));

            // Set the Category object as the category of the Food object
            food.setProjectOwner(category);

            // Set the CatName attribute with the category name
            food.setName(category.getNom());
            // Save the Food object in the database
            return service.save(food);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return an appropriate error response
        }

    }*/
/*
 @PostMapping("/add")
  public AdminProjects create(@RequestParam("files") MultipartFile[] files,
                              @RequestParam("video") MultipartFile video,
                              @RequestParam("price") Float price,
                              @RequestParam("titre") String titre,
                              @RequestParam("technologies") String technologies,
                              @RequestParam("description") String description,
                              @RequestParam("projectOwnerId") Long projectOwnerId) {

      try {
          // Vérifier si le fichier vidéo est vide
          if (video.isEmpty()) {
              throw new IllegalArgumentException("Video file is required");
          }

          // Vérifier si le fichier vidéo est au format MP4
          if (!video.getContentType().equals("video/mp4")) {
              throw new IllegalArgumentException("Invalid video file format. Only MP4 files are allowed.");
          }

          AdminProjects food = new AdminProjects();
          food.setTitre(titre);
          food.setDescription(description);
          food.setPrice(price);
          food.setTechnologies(technologies);

          // Generate a timestamp for the video filename
          String videoTimestamp = Long.toString(System.currentTimeMillis());

          // Set the destination path to save the video file
          String videoDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\";

          // Create a new filename using the timestamp and original video filename
          String newVideoFilename = videoTimestamp + "_" + video.getOriginalFilename();

          // Save the video file to the disk
          video.transferTo(new File(videoDestinationPath + newVideoFilename));

          // Assign the new video filename to the "video" attribute of the AdminProjects object
          food.setVideo(newVideoFilename);

          // Generate a timestamp for each image filename
          String imageTimestamp = Long.toString(System.currentTimeMillis());

          // Set the destination path to save the image files
          String imageDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\";

          StringBuilder imageFilenames = new StringBuilder();

          // Iterate over the array of image files and save each one
          for (MultipartFile file : files) {
              // Create a new filename using the timestamp and original filename
              String newFilename = imageTimestamp + "_" + file.getOriginalFilename();

              // Save the file to the disk
              file.transferTo(new File(imageDestinationPath + newFilename));

              // Append the new filename to the string of image filenames
              imageFilenames.append(newFilename).append(",");
          }

          // Remove the trailing comma from the string of image filenames
          if (imageFilenames.length() > 0) {
              imageFilenames.deleteCharAt(imageFilenames.length() - 1);
          }

          // Assign the concatenated image filenames to the "image" attribute of the AdminProjects object
          food.setImage(imageFilenames.toString());

          ProjectOwner category = Catservice.findById(projectOwnerId)
                  .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + projectOwnerId));

          // Set the Category object as the category of the AdminProjects object
          food.setProjectOwner(category);

          // Set the CatName attribute with the category name
          food.setName(category.getNom());
          food.setOwnerImage(category.getImage());

          // Save the AdminProjects object in the database
          return service.save(food);
      } catch (IOException e) {
          e.printStackTrace();
          return null; // Return an appropriate error response
      }
  }*/
/* @PostMapping("/add")
  public AdminProjects create(@RequestParam("files") MultipartFile[] files,
                              @RequestParam("video") MultipartFile video,
                              @RequestParam("price") Float price,
                              @RequestParam("titre") String titre,
                              @RequestParam("technologies") String technologies,
                              @RequestParam("description") String description,
                              @RequestParam("projectOwnerId") Long projectOwnerId) {

      try {
          // Vérifier si le fichier vidéo est vide
          if (video.isEmpty()) {
              throw new IllegalArgumentException("Video file is required");
          }

          // Vérifier si le fichier vidéo est au format MP4
          if (!video.getContentType().equals("video/mp4")) {
              throw new IllegalArgumentException("Invalid video file format. Only MP4 files are allowed.");
          }

          AdminProjects food = new AdminProjects();
          food.setTitre(titre);
          food.setDescription(description);
          food.setPrice(price);
          food.setTechnologies(technologies);
          AdminProjects savedFood = service.save(food);

          // Generate a timestamp for the video filename
          String videoTimestamp = Long.toString(System.currentTimeMillis());

          // Set the destination path to save the video file
          String videoDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";

          // Create the directory if it doesn't exist
          File videoDestinationDir = new File(videoDestinationPath);
          videoDestinationDir.mkdirs();

          // Create a new filename using the timestamp and original video filename
          String newVideoFilename = videoTimestamp + "_" + video.getOriginalFilename();

          // Save the video file to the disk
          video.transferTo(new File(videoDestinationPath + newVideoFilename));

          // Assign the new video filename to the "video" attribute of the AdminProjects object
          food.setVideo(newVideoFilename);

          // Generate a timestamp for each image filename
          String imageTimestamp = Long.toString(System.currentTimeMillis());

          // Set the destination path to save the image files
          String imageDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";

          // Create the directory if it doesn't exist
          File imageDestinationDir = new File(imageDestinationPath);
          imageDestinationDir.mkdirs();

          StringBuilder imageFilenames = new StringBuilder();

          // Iterate over the array of image files and save each one
          for (MultipartFile file : files) {
              // Create a new filename using the timestamp and original filename
              String newFilename = imageTimestamp + "_" + file.getOriginalFilename();

              // Save the file to the disk
              file.transferTo(new File(imageDestinationPath + newFilename));

              // Append the new filename to the string of image filenames
              imageFilenames.append(newFilename).append(",");
          }

          // Remove the trailing comma from the string of image filenames
          if (imageFilenames.length() > 0) {
              imageFilenames.deleteCharAt(imageFilenames.length() - 1);
          }

          // Assign the concatenated image filenames to the "image" attribute of the AdminProjects object
          food.setImage(imageFilenames.toString());

          ProjectOwner category = Catservice.findById(projectOwnerId)
                  .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + projectOwnerId));

          // Set the Category object as the category of the AdminProjects object
          food.setProjectOwner(category);

          // Set the CatName attribute with the category name
          food.setName(category.getNom());
//food.setOwnerImage(category.getImage());
          return service.save(food);

// Save the updated AdminProjects object in the database



          // Save the AdminProjects object in the database
      } catch (IOException e) {
          e.printStackTrace();
          return null; // Return an appropriate error response
      }
  }*/


    @PostMapping("/add")
    public AdminProjects create(@RequestParam("files") MultipartFile[] files,
                                @RequestParam("video") MultipartFile video,
                                @RequestParam("price") Float price,
                                @RequestParam("titre") String titre,
                                @RequestParam("technologies") String technologies,
                                @RequestParam("description") String description,
                                @RequestParam("projectOwnerId") Long projectOwnerId) {

        try {
            // Vérifier si le fichier vidéo est vide
            if (video.isEmpty()) {
                throw new IllegalArgumentException("Video file is required");
            }

            // Vérifier si le fichier vidéo est au format MP4
            if (!video.getContentType().equals("video/mp4")) {
                throw new IllegalArgumentException("Invalid video file format. Only MP4 files are allowed.");
            }

            AdminProjects food = new AdminProjects();
            food.setTitre(titre);
            food.setDescription(description);
            food.setPrice(price);
            food.setTechnologies(technologies);
            AdminProjects savedFood = service.save(food);

            // Generate a timestamp for the video filename
            String videoTimestamp = Long.toString(System.currentTimeMillis());

            // Set the destination path to save the video file
            //String videoDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";
            String videoDestinationPath = filesFolder + "\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";

            // Create the directory if it doesn't exist
            File videoDestinationDir = new File(videoDestinationPath);
            videoDestinationDir.mkdirs();

            // Create a new filename using the timestamp and original video filename
            String newVideoFilename = videoTimestamp + "_" + video.getOriginalFilename();

            // Save the video file to the disk
            video.transferTo(new File(videoDestinationPath + newVideoFilename));

            // Assign the new video filename to the "video" attribute of the AdminProjects object
            food.setVideo(newVideoFilename);

            // Generate a timestamp for each image filename
            String imageTimestamp = Long.toString(System.currentTimeMillis());

            // Set the destination path to save the image files
            //String imageDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";
            String imageDestinationPath = filesFolder + "\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";

            // Create the directory if it doesn't exist
            File imageDestinationDir = new File(imageDestinationPath);
            imageDestinationDir.mkdirs();

            StringBuilder imageFilenames = new StringBuilder();

            // Iterate over the array of image files and save each one
            for (MultipartFile file : files) {
                // Create a new filename using the timestamp and original filename
                String newFilename = imageTimestamp + "_" + file.getOriginalFilename();

                // Save the file to the disk
                file.transferTo(new File(imageDestinationPath + newFilename));

                // Append the new filename to the string of image filenames
                imageFilenames.append(newFilename).append(",");
            }

            // Remove the trailing comma from the string of image filenames
            if (imageFilenames.length() > 0) {
                imageFilenames.deleteCharAt(imageFilenames.length() - 1);
            }

            // Assign the concatenated image filenames to the "image" attribute of the AdminProjects object
            food.setImage(imageFilenames.toString());

            ProjectOwner projectOwner = Catservice.findById(projectOwnerId)
                    .orElseThrow(() -> new ResourceNotFoundException("ProjectOwner not found with id: " + projectOwnerId));

            String projectOwnerImage = projectOwner.getImage();
            //String projectOwnerImagePath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\projectOwner\\" + projectOwnerImage;
            String projectOwnerImagePath = filesFolder + "\\projectOwner\\" + projectOwnerImage;

            File projectOwnerImageFile = new File(projectOwnerImagePath);
            if (projectOwnerImageFile.exists()) {
                // Generate a timestamp for the ownerImage filename
                String ownerImageTimestamp = Long.toString(System.currentTimeMillis());

                // Set the destination path to save the ownerImage file
                String ownerImageDestinationPath = filesFolder + "\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\ProjectOwner\\";

                // Create the directory if it doesn't exist
                File ownerImageDestinationDir = new File(ownerImageDestinationPath);
                ownerImageDestinationDir.mkdirs();

                // Create a new filename using the timestamp and original ownerImage filename
                String newOwnerImageFilename = ownerImageTimestamp + "_" + projectOwnerImage;

                // Delete the existing ownerImage file
                File existingOwnerImageFile = new File(ownerImageDestinationPath + newOwnerImageFilename);
                existingOwnerImageFile.delete();

                // Copy the ProjectOwner image to the AdminProjects' ownerImage
                try {
                    Path destination = Paths.get(ownerImageDestinationPath + newOwnerImageFilename);
                    Files.copy(projectOwnerImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                    // Update the ownerImage path in AdminProjects
                    String newOwnerImagePath = "adminProjects\\" + titre + "_" + savedFood.getId() + "\\ProjectOwner\\" + newOwnerImageFilename;
                    food.setOwnerImage(newOwnerImagePath);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null; // Return an appropriate error response
                }


                // Assign the new ownerImage filename to the "ownerImage" attribute of the AdminProjects object
                food.setOwnerImage("ProjectOwner/" + newOwnerImageFilename);
            }

            // Set the Category object as the category of the AdminProjects object
            food.setProjectOwner(projectOwner);

            // Set the CatName attribute with the category name
            food.setName(projectOwner.getNom());

            // Save the updated AdminProjects object in the database
            return service.save(food);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return an appropriate error response
        }
    }

    @GetMapping("/All")
    @ResponseBody
    public List<AdminProjects> getAll() {
        return service.getAll();
    }

    @PutMapping("/update/{id}")
    public AdminProjects update(@PathVariable("id") Long id,
                                @RequestParam(value = "files", required = false) MultipartFile[] files,
                                @RequestParam(value = "video", required = false) MultipartFile video,
                                @RequestParam(value = "price", required = false) Float price,
                                @RequestParam(value = "titre", required = false) String titre,
                                @RequestParam(value = "technologies", required = false) String technologies,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "projectOwnerId", required = false) Long projectOwnerId) {
        try {
            AdminProjects existingProject = service.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("AdminProject not found with id: " + id));
            AdminProjects savedFood = service.save(existingProject);
            String existingVideo = existingProject.getVideo();
            String existingImages = existingProject.getImage();
            String existingProjectOwnerImage = existingProject.getOwnerImage();

            // Check if the title has been modified
            boolean isTitleModified = titre != null && !existingProject.getTitre().equals(titre);

            // Rename the directory if the title has been modified
            if (isTitleModified) {
                String oldDirectoryName = existingProject.getTitre() + "_" + savedFood.getId();
                String newDirectoryName = titre + "_" + savedFood.getId();

                //String directoryPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\";
                String directoryPath = filesFolder + "\\adminProjects\\";
                String oldDirectoryPath = directoryPath + oldDirectoryName;
                String newDirectoryPath = directoryPath + newDirectoryName;

                // Use Apache Commons FileUtils to perform the directory rename operation
                FileUtils.moveDirectory(new File(oldDirectoryPath), new File(newDirectoryPath));

                existingProject.setTitre(titre); // Update the titre attribute
            }
            if (description != null) {
                existingProject.setDescription(description);
            }
            if (price != null) {
                existingProject.setPrice(price);
            }
            if (technologies != null) {
                existingProject.setTechnologies(technologies);
            }
            if (projectOwnerId != null) {
                ProjectOwner category = Catservice.findById(projectOwnerId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + projectOwnerId));
                existingProject.setProjectOwner(category);
                existingProject.setName(category.getNom());
            }
// Check if the projectOwnerId has been modified
            boolean isProjectOwnerModified = projectOwnerId != null && !existingProject.getProjectOwner().getId().equals(projectOwnerId);
            if (existingProjectOwnerImage != null) {
                ProjectOwner projectOwner = Catservice.findById(projectOwnerId)
                        .orElseThrow(() -> new ResourceNotFoundException("ProjectOwner not found with id: " + projectOwnerId));

                String projectOwnerImage = projectOwner.getImage();
                if (projectOwnerImage != null) {
                    //String projectOwnerImagePath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\projectOwner\\" + projectOwnerImage;
                    String projectOwnerImagePath = filesFolder + "\\projectOwner\\" + projectOwnerImage;

                    File projectOwnerImageFile = new File(projectOwnerImagePath);
                    if (projectOwnerImageFile.exists()) {
                        // Generate a timestamp for the ownerImage filename
                        String ownerImageTimestamp = Long.toString(System.currentTimeMillis());

                        // Set the destination path to save the ownerImage file
                        //String ownerImageDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\ProjectOwner\\";
                        String ownerImageDestinationPath = filesFolder + "\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\ProjectOwner\\";

                        // Create the directory if it doesn't exist
                        File ownerImageDestinationDir = new File(ownerImageDestinationPath);
                        ownerImageDestinationDir.mkdirs();

                        // Create a new filename using the timestamp and original ownerImage filename
                        String newOwnerImageFilename = ownerImageTimestamp + "_" + projectOwnerImage;

                        // Copy the ProjectOwner image to the AdminProjects' ownerImage
                        try {
                            Path source = projectOwnerImageFile.toPath();
                            Path destination = Paths.get(ownerImageDestinationPath + newOwnerImageFilename);
                            Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);

                            // Update the ownerImage path in AdminProjects
                            String newOwnerImagePath = "ProjectOwner\\" + newOwnerImageFilename;
                            existingProject.setOwnerImage(newOwnerImagePath);

                            // Delete the old image file if it exists
                            String oldOwnerImagePath = ownerImageDestinationPath + existingProjectOwnerImage;
                            File oldOwnerImageFile = new File(oldOwnerImagePath);
                            if (oldOwnerImageFile.exists()) {
                                oldOwnerImageFile.delete();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null; // Return an appropriate error response
                        }
                    }
                }
            }

            // Update the video file if provided
            if (video != null) {
                if (video.isEmpty()) {
                    throw new IllegalArgumentException("Video file is required");
                }
                if (!video.getContentType().equals("video/mp4")) {
                    throw new IllegalArgumentException("Invalid video file format. Only MP4 files are allowed.");
                }

                // Generate a timestamp for the video filename
                String videoTimestamp = Long.toString(System.currentTimeMillis());

                // Set the destination path to save the video file
                //String videoDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";
                String videoDestinationPath = filesFolder + "\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";

                // Create the directory if it doesn't exist
                File videoDestinationDir = new File(videoDestinationPath);
                videoDestinationDir.mkdirs();

                // Create a new filename using the timestamp and original video filename
                String newVideoFilename = videoTimestamp + "_" + video.getOriginalFilename();

                // Save the video file to the disk
                video.transferTo(new File(videoDestinationPath + newVideoFilename));

                // Delete the old video file if it exists
                if (existingVideo != null) {
                    File oldVideoFile = new File(videoDestinationPath + existingVideo);
                    if (oldVideoFile.exists()) {
                        oldVideoFile.delete();
                    }
                }

                // Assign the new video filename to the "video" attribute of the AdminProjects object
                existingProject.setVideo(newVideoFilename);
            } else {
                // Video is not modified, retain the existing value
                existingProject.setVideo(existingVideo);
            }
            if (files != null && files.length > 0) {
                String imageTimestamp = Long.toString(System.currentTimeMillis());

                // Set the destination path to save the image files
                //String imageDestinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";
                String imageDestinationPath = filesFolder + "\\adminProjects\\" + titre + "_" + savedFood.getId() + "\\";

                // Create the directory if it doesn't exist
                File imageDestinationDir = new File(imageDestinationPath);
                imageDestinationDir.mkdirs();

                StringBuilder imageFilenames = new StringBuilder();

                // Iterate over the array of image files and save each one
                for (MultipartFile file : files) {
                    // Create a new filename using the timestamp and original filename
                    String newFilename = imageTimestamp + "_" + file.getOriginalFilename();

                    // Save the file to the disk
                    file.transferTo(new File(imageDestinationPath + newFilename));

                    // Append the filename to the imageFilenames string
                    imageFilenames.append(newFilename).append(",");
                }

                // Remove the trailing comma from the imageFilenames string
                String finalImageFilenames = imageFilenames.substring(0, imageFilenames.length() - 1);

                // Delete the old image files if they exist
                if (existingImages != null) {
                    String[] oldImageFilenames = existingImages.split(",");
                    for (String oldImageFilename : oldImageFilenames) {
                        File oldImageFile = new File(imageDestinationPath + oldImageFilename);
                        if (oldImageFile.exists()) {
                            oldImageFile.delete();
                        }
                    }
                }

                // Assign the new image filenames to the "image" attribute of the AdminProjects object
                existingProject.setImage(finalImageFilenames);
            } else {
                // Image files are not modified, retain the existing value
                existingProject.setImage(existingImages);
            }

            // Save the updated AdminProjects object
            return service.save(existingProject);

        } catch (IOException | ResourceNotFoundException e) {
            e.printStackTrace();
            return null; // Return an appropriate error response
        }
    }


    @GetMapping("/catId/{id}")
    public ResponseEntity<AdminProjects> getEventsById(@PathVariable("id") Long id) {

        AdminProjects employee = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        return ResponseEntity.ok().body(employee);

    }

    /*@DeleteMapping("/delete/{id}")
       @ResponseBody
       public void delete(@PathVariable("id")Long id){
           service.deleteProjects(id);
       }
   */
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public void delete(@PathVariable("id") Long id) {
        // Get the project by ID
        AdminProjects project = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("AdminProjects not found with ID: " + id));
     AdminProjects savedFood = service.save(project);
        // Delete the project folder
        //String folderPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\adminProjects\\" + project.getTitre() + "_" +savedFood.getId()+ "\\";
        String folderPath = filesFolder + "\\adminProjects\\" + project.getTitre() + "_" +savedFood.getId()+ "\\";
        File folder = new File(folderPath);
        deleteFolder(folder);

        // Delete the project
        service.deleteProjects(id);
    }

    private void deleteFolder(File folder) {
        if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    deleteFolder(file);
                }
            }
        }
        folder.delete();
    }

}
