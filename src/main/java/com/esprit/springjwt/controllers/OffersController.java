package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.repository.OffersRepository;
import com.esprit.springjwt.service.AdminProjectsService;
import com.esprit.springjwt.service.CompanyService;
import com.esprit.springjwt.service.OffersService;
import com.esprit.springjwt.service.ProjectOwnerServices;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/Offers")
@CrossOrigin("*")
public class OffersController {
    @Autowired
    private OffersService service;
    @Autowired
    private OffersRepository offersRepository;
    @Autowired
    private CompanyService Catservice;

    @Value("${files.folder}")
    String filesFolder;

    @PostMapping("/add")
    public Offers create(@RequestParam("poste") String poste,
                         @RequestParam("skills") String skills,
                         @RequestParam("description") String description,
                         @RequestParam("experience") String experience,
                         @RequestParam("type") String type,
                         @RequestParam("education") String education,
                         @RequestParam("companyId") Long companyId) {

        try {
            Offers food = new Offers();
            food.setPoste(poste);
            food.setSkills(skills);
            food.setDescription(description);
            food.setExperience(experience);
            food.setType(type);
            food.setStatus(true);
            food.setEducation(education);

            Company projectOwner = Catservice.findById(companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("ProjectOwner not found with id: " + companyId));
            food.setImage(projectOwner.getImage());
            food.setNom(projectOwner.getNom());
            food.setDescription2(projectOwner.getDescription());
            food.setAdresse(projectOwner.getAdresse());
            food.setEmail(projectOwner.getEmail());
            food.setNumtel(projectOwner.getNumtel());
            String projectOwnerImage = projectOwner.getImage();
            String projectOwnerImagePath = filesFolder + "\\Company\\" + projectOwnerImage;

            File projectOwnerImageFile = new File(projectOwnerImagePath);
            if (projectOwnerImageFile.exists()) {
                // Generate a timestamp for the ownerImage filename
                String ownerImageTimestamp = Long.toString(System.currentTimeMillis());

                // Set the destination path to save the ownerImage file
                String ownerImageDestinationPath = filesFolder + "\\Company\\";

                // Create the directory if it doesn't exist
                File ownerImageDestinationDir = new File(ownerImageDestinationPath);
                ownerImageDestinationDir.mkdirs();

                // Create a new filename using the timestamp and original ownerImage filename
                String newOwnerImageFilename = ownerImageTimestamp + "_" + projectOwnerImage;

                // Delete the existing ownerImage file
                File existingOwnerImageFile = new File(ownerImageDestinationPath + newOwnerImageFilename);
                existingOwnerImageFile.delete();

                try {
                    // Copy the ProjectOwner image to the AdminProjects' ownerImage
                    Path destination = Paths.get(ownerImageDestinationPath + newOwnerImageFilename);
                    Files.copy(projectOwnerImageFile.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                    // Update the ownerImage path in AdminProjects
                    String newOwnerImagePath = "Company\\" + newOwnerImageFilename;





                } catch (IOException e) {
                    e.printStackTrace();
                    return null; // Return an appropriate error response
                }
            }

            food.setCompany(projectOwner);
            return service.save(food);
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
            return null; // Return an appropriate error response
        }
    }
    @GetMapping("/All")
    @ResponseBody
    public List<Offers> getAll() {
        return service.getAll();
    }
    @GetMapping("/AllType")
    @ResponseBody
    public List<Offers> getAllType(@RequestParam("type") String type) {
        List<Offers> allOffers = service.getAll();
        List<Offers> filteredOffers = new ArrayList<>();

        for (Offers offer : allOffers) {
            if (offer.getType().equals(type)) {
                filteredOffers.add(offer);
            }
        }

        return filteredOffers;
    }

    @GetMapping("/catId/{id}")
    public ResponseEntity<Offers> getEventsById(@PathVariable("id") Long id) {

        Offers employee = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        return ResponseEntity.ok().body(employee);

    }

    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public void delete(@PathVariable("id")Long id){
        service.delete(id);
    }
    @PutMapping("/update/{id}")
    public Offers update(@PathVariable("id") Long id,
                                @RequestParam(value = "poste", required = false) String poste,
                                @RequestParam(value = "skills", required = false) String skills,
                                @RequestParam(value = "description", required = false) String description,
                                @RequestParam(value = "experience", required = false) String experience,
                                @RequestParam(value = "type", required = false) String type,
                                @RequestParam(value = "education", required = false) String education,
                                @RequestParam(value = "companyId", required = false) Long companyId
                             ) {
        try {
            Offers existingProject = service.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("AdminProject not found with id: " + id));
            Offers savedFood = service.save(existingProject);
            String existingImages = existingProject.getImage();

            // Check if the title has been modified

            // Rename the directory if the title has been modified
            if (poste != null) {

                existingProject.setPoste(poste); // Update the titre attribute
            }
            if (description != null) {
                existingProject.setDescription(description);
            }
            if (skills != null) {
                existingProject.setSkills(skills);
            }
            if (experience != null) {
                existingProject.setExperience(experience);
            }
            if (type != null) {
                existingProject.setType(type);
            }  if (education != null) {
                existingProject.setEducation(education);
            }
            if (companyId != null) {
                Company category = Catservice.findById(companyId)
                        .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + companyId));
                existingProject.setCompany(category);
                existingProject.setNom(category.getNom());
                existingProject.setDescription2(category.getDescription());
                existingProject.setAdresse(category.getAdresse());
                existingProject.setEmail(category.getEmail());
                existingProject.setNumtel(category.getNumtel());
            }
// Check if the projectOwnerId has been modified
            boolean isProjectOwnerModified = companyId != null && !existingProject.getCompany().getId().equals(companyId);
            if (existingImages != null) {
                Company projectOwner = Catservice.findById(companyId)
                        .orElseThrow(() -> new ResourceNotFoundException("ProjectOwner not found with id: " + companyId));

                String projectOwnerImage = projectOwner.getImage();
                if (projectOwnerImage != null) {
                    String projectOwnerImagePath = filesFolder + "\\Company\\" + projectOwnerImage;

                    File projectOwnerImageFile = new File(projectOwnerImagePath);
                    if (projectOwnerImageFile.exists()) {
                        // Generate a timestamp for the ownerImage filename
                        String ownerImageTimestamp = Long.toString(System.currentTimeMillis());

                        // Set the destination path to save the ownerImage file
                        String ownerImageDestinationPath = filesFolder + "\\Company\\";

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
                            String newOwnerImagePath = "Company\\" + newOwnerImageFilename;
                            existingProject.setImage(newOwnerImagePath);

                            // Delete the old image file if it exists
                            String oldOwnerImagePath = ownerImageDestinationPath + existingImages;
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

            // Save the updated AdminProjects object
            return service.save(existingProject);

        } catch (  ResourceNotFoundException e) {
            e.printStackTrace();
            return null; // Return an appropriate error response
        }
    }
    @GetMapping("/getSortedByDate/{order}")
    public ResponseEntity<List<Offers>> getClaimsSortedByDate(@PathVariable String order) {
        List<Offers> claims;
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


}
