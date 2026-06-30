package com.esprit.springjwt.controllers;


import com.esprit.springjwt.entity.Hackerspaces;
import com.esprit.springjwt.entity.Projects;
import com.esprit.springjwt.service.HackerspacesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.swing.plaf.synth.Region;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/Hackerspaces")
@CrossOrigin(origins = "*")
public class HackerspacesController {
    @Autowired
    HackerspacesService HackerspacesService;

    @GetMapping("/allHackerspaces")
    public List<Hackerspaces> getAllHackerspaces() {
        return HackerspacesService.getAllHackerspaces();

    }
    @PostMapping("/addHackerspaces")
    public Hackerspaces addHackerspaces(@RequestParam("Region")String Region,
                                        @RequestParam("Location") String Location,
                                        @RequestParam("Phone")Integer Phone,
                                        @RequestParam("Email")String Email,
                                        @RequestParam("Description")String Description,
                                        @RequestParam("Photo") MultipartFile Photo,
                                        @RequestParam("adresse") String Adresse

                                        ) throws IOException {
        return HackerspacesService.addHackerspaces(Region,Location,Phone,Email,Description,Adresse,Photo);
    }
    @PostMapping("/updateHackerspaces")
    public ResponseEntity<?> updateHackerspaces(@RequestParam("id") Long id ,@RequestParam("Region")String Region,
            @RequestParam("Location") String Location,
            @RequestParam("Phone")Integer Phone,
            @RequestParam("Email")String Email,
            @RequestParam("Description")String Description,
            @RequestParam("Photo") MultipartFile Photo,
            @RequestParam("adresse") String Adresse){
       
        try{
        	Hackerspaces hackerspace = HackerspacesService.updateHackerspaces(id,Region,Location,Phone,Email,Description,Adresse,Photo);
		    return ResponseEntity.ok(hackerspace);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<String>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
		
    }
    @GetMapping("/getHackerspacesById/{id}")
    public Hackerspaces getHackerspacesById(@PathVariable("id") Long id)
    {
        return HackerspacesService.getHackerspacesById(id);
    }
    @DeleteMapping("/deleteHackerspaces/{id}")
    public void deleteHackerspaces(@PathVariable("id") Long id){
        HackerspacesService.deleteHackerspaces(id);
    }

    //get by region
    @GetMapping("/getHackerspacesByRegion/{Region}")
    public Hackerspaces getHackerspacesByRegion(@PathVariable("Region") String Region){
        return HackerspacesService.getHackerspacesByRegion(Region);
    }

}