package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.Formation;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.payload.request.PaginateInfo;
import com.esprit.springjwt.service.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/formation")
public class FormationController {

    @Autowired
    private FormationService formationService;

    @GetMapping("/all")
    public List<Formation> getAllformation(){
        return formationService.getAllTypeForamtion();
    }
    
    @GetMapping("/allPaginate")
    public ResponseEntity<?> getAllformationPaginate(@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "per_page", defaultValue = "3") int size){
    	
    	if (page < 0 || size <= 0 ) {
	        return ResponseEntity.badRequest().body("Invalid page or per_page values.");
	 	}
    	try {
    		Page<Formation> formations = formationService.getAllForamtionPaginate(PageRequest.of(page, size));
        	int total = formations.getTotalPages();
            int[] count_page = new int[total];
            for (int i = 0; i < total; i++) {
                count_page[i] = i;
            }
            PaginateInfo data = new PaginateInfo(count_page, formations, page);
            return ResponseEntity.ok(data);
    	}catch(Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching paginated formations.");
    	}
    }
    
    //delete formation
    @DeleteMapping("/deleteFormation/{id}")
    public void deleteFormation(@PathVariable("id") Long id)
    {
        formationService.deleteFormation(id);
    }

    @PostMapping("/addFormation")
    public ResponseEntity<?> addFormation(@RequestBody Formation formation)
    {
        try {
            if (formation == null || formation.getCategorie() == null) {
                return ResponseEntity.badRequest().body("Formation and Category are required");
            }
            
            Formation addedFormation = this.formationService.addFormation(formation);
            return ResponseEntity.ok(addedFormation);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid argument: " + e.getMessage());
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error adding formation: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error adding formation: " + e.getMessage());
        }
    }
    // getFormations By CategorieId
    @GetMapping("/getFormationsByCategorieId/{id}")
    public List<Formation> getFormationsByCategorieId(@PathVariable("id") Long id)
    {
        return formationService.getFormationsByCategorieId(id);
    }

//get training by id
    @GetMapping("/getFormationById/{id}")
    public Formation getFormationById(@PathVariable("id") Long id)
    {
        return formationService.getFormationById(id);
    }
//get formation by nomformation
    @GetMapping("/getFormationByNomFormation/{nomFormation}")
    public Formation getFormationByNomFormation(@PathVariable("nomFormation") String nomFormation)
    {
        return formationService.getFormationByNomFormation(nomFormation);
    }
    //filtre formation by nomformation
    @GetMapping("/search/{nomFormation}")
    public List<Formation> getFormationsByNomFormationContains(@PathVariable String nomFormation) {
        return formationService.getFormationByNomFormationContains(nomFormation);
    }
    //update formation

    @PutMapping("/updateFormation/{id}")
    public Formation updateFormation(@PathVariable("id") Long id, @RequestBody Formation updatedFormation) {
        Formation formation = formationService.getFormationById(id);
        if (formation == null) {
            throw new IllegalArgumentException("Formation not found for ID: " + id);
        }

        updatedFormation.setId(id);
        Formation updatedFormationObj = formationService.updateFormation(updatedFormation);
        return updatedFormationObj;
    }

   //get formation by user id
    @GetMapping("/getFormationByUserId/{id}")
    public List<Formation> getFormationByUserId(@PathVariable("id") Long id)
    {
        return formationService.getFormationByUserId(id);
    }

    
    @GetMapping("/getCountFormationsInProgress/{id}")
    public ResponseEntity<?> getCountFormationsInProgressByUserId(@PathVariable("id") Long id){
         Integer size = formationService.getCountFormationsInProgressByUserId(id);
		return ResponseEntity.ok(size);
    }
    
    @GetMapping("/getCountFormationsCompleted/{id}")
    public ResponseEntity<?> getCountFormationsCompletedByUserId(@PathVariable("id") Long id){
         Integer size = formationService.getCountFormationsCompletedByUserId(id);
		return ResponseEntity.ok(size);
    }
    
    @GetMapping("/getCountFormationsCompletedCoach/{id}")
    public ResponseEntity<?> getCountFormationsCompletedCoach(@PathVariable("id") Long id){
         Integer size = formationService.getCountFormationsCompletedByCoach(id);
		return ResponseEntity.ok(size);
    }
    
    @GetMapping("/getCountFormationsInProgressCoach/{id}")
    public ResponseEntity<?> getCountFormationsInProgressCoach(@PathVariable("id") Long id){
         Integer size = formationService.getCountFormationsInProgressCoach(id);
		return ResponseEntity.ok(size);
    }


}
