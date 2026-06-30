package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.entity.SpecificProject;

import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.service.SpecificProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;

import java.util.List;

@RestController
@RequestMapping("/api/SpecificProject")
@CrossOrigin("*")
public class SpecificProjectController {
    @Autowired
    private SpecificProjectService service;

    @Value("${files.folder}")
    String filesFolder;

    @PostMapping("/add")
    public SpecificProject create( /**Optional**/ @RequestParam(value ="files", required = false) MultipartFile[] files,
                                   /**Optional**/  @RequestParam(value ="video", required = false) MultipartFile video,
                                  @RequestParam(value ="price", required = false) Float price,
                                  @RequestParam("titre") String titre,
                                  @RequestParam("technologies") String technologies,
                                  @RequestParam("description") String description,

                                  @RequestParam("nom") String nom,
                                  @RequestParam("prenom") String prenom,
                                  @RequestParam("email") String email,
                                  @RequestParam("numtel") int numtel,
                 /**Optional**/   @RequestParam(value ="github", required = false) String github,
                 /**Optional**/   @RequestParam(value ="linkedin", required = false) String linkedin,
                 /**Optional**/   @RequestParam(value ="remark", required = false) String remark

 ) {

        try {
            SpecificProject food = new SpecificProject();




            food.setTitre(titre);
            food.setDescription(description);
            food.setTechnologies(technologies);
            food.setNom(nom);
            food.setPrenom(prenom);
            food.setEmail(email);
            food.setNumtel(numtel);
            food.setStatus(false);        /*   food.setGithub(github);
            food.setLinkedin(linkedin);*/
            //food.setRemark(remark);

            if (price != null) {
                food.setPrice(price);
            }
            if (github != null) {
                food.setGithub(github);
            }
            if (linkedin != null) {
                food.setLinkedin(linkedin);
            }
            if (remark != null) {
                food.setRemark(remark);
            }
            SpecificProject savedFood = service.save(food);


            // Vérifier si le fichier vidéo est vide
            if (video != null && !video.isEmpty()) {
                // Handle the video file
                if (!video.getContentType().equals("video/mp4")) {
                    throw new IllegalArgumentException("Invalid video file format. Only MP4 files are allowed.");
                }

                String videoTimestamp = Long.toString(System.currentTimeMillis());

                // Set the destination path to save the video file
                String videoDestinationPath = filesFolder + "\\SpecificProject\\" + titre + "_" + savedFood.getId() + "\\";

                // Create the directory if it doesn't exist
                File videoDestinationDir = new File(videoDestinationPath);
                videoDestinationDir.mkdirs();

                // Create a new filename using the timestamp and original video filename
                String newVideoFilename = videoTimestamp + "_" + video.getOriginalFilename();

                // Save the video file to the disk
                video.transferTo(new File(videoDestinationPath + newVideoFilename));

                // Assign the new video filename to the "video" attribute of the AdminProjects object
                food.setVideo(newVideoFilename);

            }

            // Generate a timestamp for each image filename
            String imageTimestamp = Long.toString(System.currentTimeMillis());

            // Set the destination path to save the image files
            String imageDestinationPath = filesFolder + "\\SpecificProject\\" + titre + "_" + savedFood.getId() + "\\";

            // Create the directory if it doesn't exist
            File imageDestinationDir = new File(imageDestinationPath);
            imageDestinationDir.mkdirs();

            StringBuilder imageFilenames = new StringBuilder();
            if (files != null && files.length > 0) {

                // Iterate over the array of image files and save each one
                for (MultipartFile file : files) {
                    // Create a new filename using the timestamp and original filename
                    String newFilename = imageTimestamp + "_" + file.getOriginalFilename();

                    // Save the file to the disk
                    file.transferTo(new File(imageDestinationPath + newFilename));

                    // Append the new filename to the string of image filenames
                    imageFilenames.append(newFilename).append(",");
                }
            }
            // Remove the trailing comma from the string of image filenames
            if (imageFilenames.length() > 0) {
                imageFilenames.deleteCharAt(imageFilenames.length() - 1);
            }

            // Assign the concatenated image filenames to the "image" attribute of the AdminProjects object
            food.setImage(imageFilenames.toString());


            // Set the CatName attribute with the category name


            // Save the updated AdminProjects object in the database
            return service.save(food);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // Return an appropriate error response
        }
    }
    @GetMapping("/All")
    @ResponseBody
    public List<SpecificProject> getAll() {
        return service.getAll();
    }
    @PutMapping ("/updateStatus/{id}")

    public ResponseEntity<Void> UpdateComplaintAdmin(@PathVariable Long id, @RequestParam boolean status) {
        service.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getStatus/{status}")
    public ResponseEntity<List<SpecificProject>> getClaimsByStatus(@PathVariable boolean status) {
        List<SpecificProject> claims = service.getClaimsByStatus(status);
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }
    @GetMapping("/getSortedByDate/{order}")
    public ResponseEntity<List<SpecificProject>> getClaimsSortedByDate(@PathVariable String order) {
        List<SpecificProject> claims;
        if (order.equalsIgnoreCase("asc")) {
            claims = service.getAllClaimsSortedByDateAsc();
        } else if (order.equalsIgnoreCase("desc")) {
            claims = service.getAllClaimsSortedByDateDesc();
        } else {
            // Handle invalid order parameter
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }
    @GetMapping("/catId/{id}")
    public ResponseEntity<SpecificProject> getEventsById(@PathVariable("id") Long id) {

        SpecificProject employee = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        return ResponseEntity.ok().body(employee);
    }
}
