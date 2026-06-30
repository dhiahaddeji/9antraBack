package com.esprit.springjwt.controllers;
import com.esprit.springjwt.entity.AdminProjects;
import com.esprit.springjwt.entity.Offers;
import com.esprit.springjwt.entity.ProjectOwner;
import com.esprit.springjwt.entity.Company;
import com.esprit.springjwt.exception.ResourceNotFoundException;
import com.esprit.springjwt.repository.OffersRepository;
import com.esprit.springjwt.service.CompanyService;
import com.esprit.springjwt.service.OffersService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/Company")
@CrossOrigin("*")
public class CompanyController {
    @Autowired
    CompanyService companyService;
    @Autowired
    OffersRepository offersRepository;
    @Autowired
    OffersService offersService;

    @Value("${files.folder}")
    String filesFolder;

    @GetMapping("/All")
    @ResponseBody
    public List<Company> getAll(){
        return companyService.getAll();
    }

    @PostMapping("/add")
    public Company create(@RequestParam("file") MultipartFile file,
                          @RequestParam("nom") String nom,

                          @RequestParam("numtel") int numtel,
                          @RequestParam("email") String email, @RequestParam("description") String description,
                          @RequestParam("adresse") String adresse

    ) {
        try {
            Company food = new Company();
            food.setNom(nom);
            food.setAdresse(adresse);
            food.setDescription(description);

            food.setNumtel(numtel);
            food.setEmail(email);
            food.setStatus(true);

            // Generate a timestamp for the image filename
            String timestamp = Long.toString(System.currentTimeMillis());

            // Set the destination path to save the image
            //String destinationPath = "C:\\Users\\DELL\\Desktop\\testfront\\src\\assets\\Company\\";
            String destinationPath = filesFolder + "\\Company\\";

            // Create a new filename using the timestamp and original filename
            String newFilename = timestamp + "_" + file.getOriginalFilename();

            // Save the file to the disk
            file.transferTo(new File(destinationPath + newFilename));
            String newOwnerImagePath = "Company\\" + newFilename;

            // Assign the new filename to the "image" attribute of the Food object
            food.setImage(newOwnerImagePath);

            // Save the Food object in the database
            return companyService.save(food);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors while saving the image or the Food object
            return null; // Return an appropriate error response
        }
    }
    @DeleteMapping("/delete/{id}")
    @ResponseBody
    public void delete(@PathVariable("id")Long id){
        companyService.delete(id);
    }
    @GetMapping("/catId/{id}")
    public ResponseEntity<Company> getEventsById(@PathVariable("id") Long id) {

        Company employee = companyService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + id));
        return ResponseEntity.ok().body(employee);

    }

    @PutMapping("/update/{id}")
    public Company update(@PathVariable("id") Long id,
                          @RequestParam(value = "file", required = false) MultipartFile file,
                          @RequestParam(value = "nom", required = false) String nom,
                          @RequestParam(value = "numtel", required = false) Integer numtel,
                          @RequestParam(value = "email", required = false) String email,
                          @RequestParam(value = "description", required = false) String description,
                          @RequestParam(value = "adresse", required = false) String adresse
    ) throws ResourceNotFoundException {
        Company existingCompany = companyService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        try {
            // Update the fields of the existing Company object if the corresponding parameters are provided
            if (nom != null) {
                existingCompany.setNom(nom);
            }
            if (numtel != null) {
                existingCompany.setNumtel(numtel);
            }
            if (email != null) {
                existingCompany.setEmail(email);
            }
            if (description != null) {
                existingCompany.setDescription(description);
            }
            if (adresse != null) {
                existingCompany.setAdresse(adresse);
            }

            // Update the image file if a new file is provided
            if (file != null && !file.isEmpty()) {
                // Generate a timestamp for the image filename
                String timestamp = Long.toString(System.currentTimeMillis());

                // Set the destination path to save the image
                //String destinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\Company\\";
                String destinationPath = filesFolder + "\\Company\\";

                // Create a new filename using the timestamp and original filename
                String newFilename = timestamp + "_" + file.getOriginalFilename();


                
                // Save the file to the disk
                file.transferTo(new File(destinationPath + newFilename));

                // Delete the previous image file (optional)
                String oldFilename = existingCompany.getImage();
                if (oldFilename != null) {
                    File oldFile = new File(destinationPath + oldFilename);
                    oldFile.delete();
                }
                String newOwnerImagePath = "Company\\" + newFilename;

                // Assign the new filename to the "image" attribute of the Company object
                existingCompany.setImage(newOwnerImagePath);

                // Update the offers related to the Company
                List<Offers> relatedOffers = offersRepository.findByCompany(existingCompany);
                for (Offers offer : relatedOffers) {
                    offer.setNom(existingCompany.getNom());
                    offer.setDescription2(existingCompany.getDescription());
                    offer.setAdresse(existingCompany.getAdresse());
                    offer.setEmail(existingCompany.getEmail());
                    offer.setNumtel(existingCompany.getNumtel());
                    offer.setImage(existingCompany.getImage());
                    offersService.save(offer);
                }
            }

            // Save the updated Company object in the database
            return companyService.save(existingCompany);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors while saving the image or updating the Company object
            return null; // Return an appropriate error response
        }
    }


    @PostMapping("/addC")
    public Company createC(@RequestParam("file") MultipartFile file,
                              @RequestParam("nom") String nom,

                          @RequestParam("numtel") int numtel,
                          @RequestParam("email") String email, @RequestParam("description") String description,
                          @RequestParam("adresse") String adresse

    ) {
        try {
            Company food = new Company();
            food.setNom(nom);
            food.setAdresse(adresse);
            food.setDescription(description);
            food.setNumtel(numtel);
            food.setEmail(email);
            food.setStatus(false);

            // Generate a timestamp for the image filename
            String timestamp = Long.toString(System.currentTimeMillis());

            // Set the destination path to save the image
            //String destinationPath = "C:\\Users\\DELL\\Desktop\\testfront\\src\\assets\\Company\\";
            String destinationPath = filesFolder + "\\Company\\";

            // Create a new filename using the timestamp and original filename
            String newFilename = timestamp + "_" + file.getOriginalFilename();

            // Save the file to the disk
            file.transferTo(new File(destinationPath + newFilename));
            String newOwnerImagePath = "Company\\" + newFilename;

            // Save the file to the disk

            // Assign the new filename to the "image" attribute of the Food object
            food.setImage(newOwnerImagePath);

            // Save the Food object in the database
            return companyService.save(food);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors while saving the image or the Food object
            return null; // Return an appropriate error response
        }
    }

    @PutMapping ("/updateStatus/{id}")
    public ResponseEntity<Void> UpdateComplaintAdmin(@PathVariable Long id, @RequestParam boolean status) {
        companyService.updateStatus(id, status);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/getStatus/{status}")
    public ResponseEntity<List<Company>> getClaimsByStatus(@PathVariable boolean status) {
        List<Company> claims = companyService.getClaimsByStatus(status);
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }
/*    @PutMapping("/update/{id}")
    public company updateEmployee(
            @PathVariable(value = "id") Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("nom") String nom,

            @RequestParam("numtel") int numtel,
            @RequestParam("email") String email, @RequestParam("description") String description,
            @RequestParam("adresse") String adresse
    ) throws ResourceNotFoundException {
        company food = companyService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id: " + id));

        try {
            food.setNom(nom);
            food.setAdresse(adresse);
            food.setDescription(description);
            food.setNumtel(numtel);
            food.setEmail(email);
            food.setStatus(false);

            if (file != null && !file.isEmpty()) {
                // User has uploaded a new image, generate a timestamp for the filename
                String timestamp = Long.toString(System.currentTimeMillis());

                // Set the destination path to save the image
                String destinationPath = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\Company\\";

                // Create a new filename using the timestamp and original filename
                String newFilename = timestamp + "_" + file.getOriginalFilename();

                // Save the file to the disk
                file.transferTo(new File(destinationPath + newFilename));

                // Delete the previous image file (optional)
                String oldFilename = food.getImage();
                if (oldFilename != null) {
                    File oldFile = new File(destinationPath + oldFilename);
                    oldFile.delete();
                }

                // Assign the new filename to the "image" attribute of the ProjectOwner object
                food.setImage(newFilename);

                // Update the ownerImage attribute of related AdminProjects
              /*  List<AdminProjects> adminProjects = service2.findByProjectOwner(projectOwner);
                for (AdminProjects adminProject : adminProjects) {
                    adminProject.setOwnerImage(newFilename);
                    service.save(adminProject);
                }*/

            /*    System.out.println("nnnnnnn");

            } else if (file == null || file.isEmpty()) {
                // No new file is selected, keep the existing filename
                String existingFilename = food.getImage();
                food.setImage(existingFilename);
                System.out.println("eeeeeeeeeee");
            }

            // Save the ProjectOwner object in the database
            return companyService.save(food);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle errors while saving the image or the ProjectOwner object
            return null; // Return an appropriate error response
        }
    }*/
            @GetMapping("/AllByS")
            @ResponseBody
            public List<Company> getAllS(){
                return companyService.getAllActive();
            }
    @GetMapping("/getSortedByDate/{order}")
    public ResponseEntity<List<Company>> getClaimsSortedByDate(@PathVariable String order) {
        List<Company> claims;
        if (order.equalsIgnoreCase("asc")) {
            claims = companyService.getAllClaimsSortedByDateAsc();
        } else if (order.equalsIgnoreCase("desc")) {
            claims = companyService.getAllClaimsSortedByDateDesc();
        } else {
            // Handle invalid order parameter
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(claims, HttpStatus.OK);
    }

}
