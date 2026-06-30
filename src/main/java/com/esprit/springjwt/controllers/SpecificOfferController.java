package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.service.SpecificOfferSerivce;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@RestController
@RequestMapping("/api/SpecificOffer")
@CrossOrigin("*")
@Transactional
public class SpecificOfferController {
    @Autowired
    private SpecificOfferSerivce service;

    @Value("${files.folder}")
    String filesFolder;

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestParam(value = "poste", required = false) String poste,
                         @RequestParam(value = "skills", required = false) String skills,
                         @RequestParam(value = "description", required = false) String description,
                         @RequestParam(value = "experience", required = false) String experience,
                         @RequestParam(value = "type", required = false) String type,
                         @RequestParam(value = "education", required = false) String education,
                         @RequestParam(value = "file", required = false) MultipartFile file,
                         @RequestParam(value = "nom", required = false) String nom,
                         @RequestParam(value = "numtel", required = false) Integer numtel,
                         @RequestParam(value = "email", required = false) String email,
                         @RequestParam(value = "descriptionC", required = false) String descriptionC,
                         @RequestParam(value = "adresse", required = false) String adresse) {
        try {
            System.out.println("=== SpecificOffer Create Started ===");
            System.out.println("Poste: " + poste);
            System.out.println("Email: " + email);
            System.out.println("File: " + (file != null ? file.getOriginalFilename() : "NULL"));
            
            // Validation
            if (file == null || file.isEmpty()) {
                System.err.println("ERROR: File is required");
                return ResponseEntity.badRequest().body("File is required");
            }
            if (poste == null || poste.trim().isEmpty()) {
                System.err.println("ERROR: Poste is required");
                return ResponseEntity.badRequest().body("Poste is required");
            }
            if (email == null || email.trim().isEmpty()) {
                System.err.println("ERROR: Email is required");
                return ResponseEntity.badRequest().body("Email is required");
            }
            
            SpecificOffer offer = new SpecificOffer();
            offer.setPoste(poste);
            offer.setSkills(skills != null ? skills : "");
            offer.setDescription(description != null ? description : "");
            offer.setExperience(experience != null ? experience : "");
            offer.setType(type != null ? type : "");
            offer.setStatus(false);
            offer.setEducation(education != null ? education : "");
            offer.setNom(nom != null ? nom : "");
            offer.setNumtel(numtel != null ? numtel : 0);
            offer.setEmail(email);
            offer.setDescriptionC(descriptionC != null ? descriptionC : "");
            offer.setAdresse(adresse != null ? adresse : "");

            System.out.println("Object created successfully");
            
            // Generate a timestamp for the image filename
            String timestamp = Long.toString(System.currentTimeMillis());

            // Use a proper directory path - create in temp or project root
            String destinationPath = filesFolder != null && !filesFolder.isEmpty() 
                ? filesFolder 
                : System.getProperty("user.dir") + File.separator + "uploads" + File.separator + "SpecificCompany" + File.separator;
            
            System.out.println("Destination path: " + destinationPath);
            
            // Create directory if it doesn't exist
            File dir = new File(destinationPath);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                System.out.println("Directory created: " + created);
                if (!created && !dir.exists()) {
                    System.err.println("ERROR: Failed to create directory");
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body("Failed to create upload directory: " + destinationPath);
                }
            } else {
                System.out.println("Directory already exists");
            }

            // Create a new filename using the timestamp and original filename
            String newFilename = timestamp + "_" + file.getOriginalFilename();
            System.out.println("New filename: " + newFilename);

            // Save the file to the disk
            File destFile = new File(destinationPath, newFilename);
            System.out.println("Saving to: " + destFile.getAbsolutePath());
            file.transferTo(destFile);
            System.out.println("File saved successfully: " + destFile.getAbsolutePath());
            
            // Assign the new filename to the image attribute
            offer.setImage(newFilename);

            SpecificOffer savedOffer = service.save(offer);
            System.out.println("Offer saved to database with ID: " + (savedOffer != null ? savedOffer.getId() : "NULL"));
            
            if (savedOffer != null && savedOffer.getId() != null) {
                System.out.println("SUCCESS: Offer created with ID " + savedOffer.getId());
                return ResponseEntity.ok(savedOffer);
            } else {
                System.err.println("ERROR: Save returned null or no ID");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Failed to save offer to database");
            }
            
        } catch (IOException e) {
            System.err.println("IOException: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unexpected error: " + e.getMessage());
        }
    }
    @GetMapping("/All")
    @ResponseBody
    public List<SpecificOffer> getAll(){
        return service.getAll();
    }
    @GetMapping("/catId/{id}")
    public ResponseEntity<SpecificOffer> getEventsById(@PathVariable("id") Long id) {

        SpecificOffer employee = service.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        return ResponseEntity.ok().body(employee);
    }
    @GetMapping("/getSortedByDate/{order}")
    public ResponseEntity<List<SpecificOffer>> getClaimsSortedByDate(@PathVariable String order) {
        List<SpecificOffer> claims;
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
