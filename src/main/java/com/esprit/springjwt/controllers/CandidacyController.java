package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.Candidacy;
import com.esprit.springjwt.entity.ProjectClient;
import com.esprit.springjwt.entity.Company;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.service.CandidacyService;
import com.esprit.springjwt.service.CompanyService;
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
@RequestMapping("/api/Candidacy")
@CrossOrigin("*")
public class CandidacyController {
    @Autowired
    CandidacyService candidacyService;

    @Value("${files.folder}")
    String filesFolder;

    @PostMapping("/add")
    public ResponseEntity<?> create(@RequestParam(value ="file", required = false) MultipartFile lettreM,
                            @RequestParam(value ="nom", required = false) String nom,
                            @RequestParam(value ="prenom", required = false) String prenom,
                            @RequestParam(value ="cv", required = false) MultipartFile cv,
                            @RequestParam(value ="numtel", required = false) int numtel,
                            @RequestParam(value ="email", required = false) String email,
                            @RequestParam(value ="experience", required = false) String experience,
                            @RequestParam(value ="lettre", required = false) String lettre,
                            @RequestParam(value ="type", required = false) String type,
                            @RequestParam(value ="education", required = false) String education
    ) {
        try {
            Candidacy employee = new Candidacy();
            employee.setNom(nom);
            employee.setPrenom(prenom);
            employee.setNumtel(numtel);
            employee.setEmail(email);
            employee.setExperience(experience);
            employee.setLettre(lettre);
            employee.setType(type);
            employee.setEducation(education);

            // Validate and save the CV file
            if (cv != null && !cv.isEmpty()) {
                if (!isValidPdfFile(cv)) {
                    return ResponseEntity.badRequest().body("CV file must be in PDF format.");
                }
                String cvFilename = saveFile(cv, "cv");
                employee.setCv(cvFilename);
            }

            // Validate and save the motivation letter file
            if (lettreM != null && !lettreM.isEmpty()) {
                if (!isValidTextOrPdfFile(lettreM)) {
                    return ResponseEntity.badRequest().body("Motivation letter must be either plain text or in PDF format.");
                }
                String lettreFilename = saveFile(lettreM, "lettre");
                employee.setLettreM(lettreFilename);
            }

            // Save the employee in the database
            Candidacy savedCandidacy = candidacyService.save(employee);
            return ResponseEntity.ok(savedCandidacy);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error uploading files: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating candidacy: " + e.getMessage());
        }
    }

    private boolean isValidPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }

    private boolean isValidTextOrPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && (contentType.equals("text/plain") || contentType.equals("application/pdf"));
    }

    private String saveFile(MultipartFile file, String prefix) throws IOException {
        String timestamp = Long.toString(System.currentTimeMillis());
        String destinationPath = filesFolder + "Candidacy" + File.separator;

        // Create directory if it doesn't exist
        File dir = new File(destinationPath);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created && !dir.exists()) {
                throw new IOException("Failed to create directory: " + destinationPath);
            }
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String newFilename = prefix + "_" + timestamp + extension;

        File destFile = new File(destinationPath + newFilename);
        file.transferTo(destFile);

        return newFilename;
    }
    @GetMapping("/All")
    @ResponseBody
    public List<Candidacy> getAll(){
        return candidacyService.getAll();
    }
    @GetMapping("/catId/{id}")
    public ResponseEntity<Candidacy> getEventsById(@PathVariable("id") Long id) {

        Candidacy employee = candidacyService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        return ResponseEntity.ok().body(employee);

    }
    @GetMapping("/getSortedByDate/{order}")
    public ResponseEntity<List<Candidacy>> getClaimsSortedByDate(@PathVariable String order) {
        List<Candidacy> claims;
        if (order.equalsIgnoreCase("asc")) {
            claims = candidacyService.getAllClaimsSortedByDateAsc();
        } else if (order.equalsIgnoreCase("desc")) {
            claims = candidacyService.getAllClaimsSortedByDateDesc();
        } else {
            // Handle invalid order parameter
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }
}
