package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.service.AdminProjectsService;
import com.esprit.springjwt.service.OfferClientService;
import com.esprit.springjwt.service.OffersService;
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
@RequestMapping("/api/OfferClient")
@CrossOrigin("*")
public class OfferClientController {
    @Autowired
    OffersService offersService;
    @Autowired
    OfferClientService offerClientService;

    @Value("${files.folder}")
    String filesFolder;

    @PostMapping("/add/{adminProjectId}")
    public OfferClient add(
                           @RequestParam(value ="nom", required = false) String nom,
                               @RequestParam(value ="cv", required = false) MultipartFile cv,
                           @RequestParam(name = "numtel", required = false) Integer numtel
,                           @RequestParam(value ="email", required = false) String email,
                           @RequestParam(value ="lettre", required = false) String lettre
                       , @PathVariable("adminProjectId") Long adminProjectId) {
        Offers adminProject = offersService.findById(adminProjectId)
                .orElseThrow(() -> new ResourceNotFoundException("AdminProjects not found with ID: " + adminProjectId));
        try {
            OfferClient employee = new OfferClient();
            employee.setNom(nom);
            employee.setNumtel(numtel);
            employee.setEmail(email);
            employee.setLettre(lettre);
            employee.setStatus(false);



            // Validate and save the CV file
            if (cv != null && !cv.isEmpty()) {
                if (!isValidPdfFile(cv)) {
                    throw new IllegalArgumentException("CV file must be in PDF format.");
                }
                String cvFilename = saveFile(cv, "cv");
                employee.setCv(cvFilename);
            }     // Save the employee in the database
            employee.addOffers(adminProject);
            employee = offerClientService.save(employee);

            adminProject.addProjectClient(employee);
            offersService.save(adminProject);

            return employee;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors while saving the files or the employee object
            return null; // Return an appropriate error response
        }

    }
    private boolean isValidPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.equals("application/pdf");
    }
    private String saveFile(MultipartFile file, String prefix) throws IOException {
        String timestamp = Long.toString(System.currentTimeMillis());
        String destinationPath = filesFolder + "\\OfferClient\\";

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String newFilename = prefix + "_" + timestamp + extension;

        file.transferTo(new File(destinationPath + newFilename));

        return newFilename;
    }
    @PutMapping ("/updateStatus/{id}")

    public ResponseEntity<Void> UpdateComplaintAdmin(@PathVariable Long id, @RequestParam boolean status) {
        offerClientService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getStatus/{status}")
    public ResponseEntity<List<OfferClient>> getClaimsByStatus(@PathVariable boolean status) {
        List<OfferClient> claims = offerClientService.getClaimsByStatus(status);
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }
    @GetMapping("/adminProjects/{adminProjectId}/projectClients")
    @ResponseBody
    public List<OfferClient> getProjectClientsByAdminProjectId(@PathVariable("adminProjectId") Long adminProjectId) {
        return offerClientService.findByOffersId(adminProjectId);
    }
    @GetMapping("/catId/{id}")
    public ResponseEntity<OfferClient> getEventsById(@PathVariable("id") Long id) {

        OfferClient employee = offerClientService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        return ResponseEntity.ok().body(employee);

    }
}
