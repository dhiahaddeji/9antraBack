package com.esprit.springjwt.controllers;

import com.esprit.springjwt.dto.CourseStatsDto;
import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.payload.request.PaginateInfoUser;
import com.esprit.springjwt.repository.FormateurRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import com.esprit.springjwt.service.FormateurService;
import com.esprit.springjwt.service.NoteService;
import com.esprit.springjwt.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private userService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NoteService noteService;

    @Autowired
    private FormateurService formateurService;


    //get All Users
    @RequestMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
    
   


    @GetMapping("/changeEnabledUser/{id}")
    public User changeEnabledUser(@PathVariable Long id) {
        return userService.changeEnabledUser(id);
    }

    @GetMapping("/finduserbyid/{id}")
    public User getUserByid(@PathVariable Long id) {
        return userService.getUserById(id);
    }


    @GetMapping("/findByTypeFormationAndStatus")
    public ResponseEntity<?> findByTypeFormationAndStatus(@RequestParam(name="formation",defaultValue="") String typeFormation, @RequestParam(name="status",defaultValue = "5")Long status) {
    	try {
    		return ResponseEntity.ok(userService.findByTypeFormationAndStatus(typeFormation, status));
    	}catch(Exception e) {
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching data");
    	}
        
    }

    //getbyemail
    @GetMapping("/getbyemail/{email}")
    public User getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email);
    }

    //update user image by id
    @PatchMapping("/updateUserImageById/{id}")
    public User updateUserImageById(@RequestParam("image") MultipartFile image, @PathVariable Long id) throws IOException {
        return userService.updateUserImageById(image, id);
    }

    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUserDetails() {
        User currentUser;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            currentUser = userDetails.getUser();
        } else {
            // Utiliser un utilisateur par défaut avec un nom d'utilisateur spécifique
            currentUser = userRepository.findByUsername("user1@gmail.com"); // Remplacez "user1@gmail.com" par le nom d'utilisateur spécifique
        }

        return ResponseEntity.ok(currentUser);
    }

    @PostMapping("/imagechange/{userId}")
    public User updateImage(@PathVariable("userId") Long userId, @RequestParam("file") MultipartFile file) throws IOException {
        User currentUser = userService.getUserById(userId);
        if (currentUser == null) {
            // Handle the case when the user doesn't exist
            // For example, throw an exception or return an error response
        }

        // Call the updateImage method from your service to update the user's image
        currentUser = userService.updateUserImageById(file, userId);

        return currentUser;
    }

    @PutMapping("/updateEnableDisable/{enabled}/{id}")
    public Integer updateEnableDisable(@PathVariable Long enabled, @PathVariable Long id) {
        System.err.println("Tbadaaaalt ");
        return userService.updateEnabled(enabled, id);
    }

    @PutMapping("/updateUser")
    public User updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }


    @PostMapping("/{userId}/notes")
    public ResponseEntity<Note> addNoteToUser(@PathVariable Long userId, @RequestBody String content) {
        Note note = noteService.addNoteToUser(userId, content);
        return ResponseEntity.ok(note);
    }

    @GetMapping("/{userId}/notes")
    public ResponseEntity<List<Note>> getAllNotesForUser(@PathVariable Long userId) {
        List<Note> notes = noteService.getAllNotesForUser(userId);
        return ResponseEntity.ok(notes);
    }

    @GetMapping("/getGroupsForUser/{userId}")
    public ResponseEntity<List<Groups>> getGroupsForUser(@PathVariable Long userId) {
        List<Groups> projects = userService.getGroupsForUser(userId);
        return ResponseEntity.ok(projects);
    }

    @PutMapping("/updateUserFormateur")
    public ResponseEntity<String> updateUserAndFormateur(@RequestBody User u) {

        User existingUser = userService.getUserById(u.getId());

        if (existingUser == null) {
            return ResponseEntity.badRequest().body("User not found.");
        }        // Update user fields if not null
        User updatedUserResult = userService.updateUser(existingUser);
        Formateur existingFormateur = existingUser.getFormateur();
        if (existingFormateur != null && u.getFormateur() != null) {
            Formateur updatedFormateur = u.getFormateur();
            // Update formateur fields if not null
            if (updatedFormateur.getGithub() != null) {
                existingFormateur.setGithub(updatedFormateur.getGithub());
            }
            if (updatedFormateur.getLinkedin() != null) {
                existingFormateur.setLinkedin(updatedFormateur.getLinkedin());
            }

            if(updatedFormateur.getCV()==null){
                updatedFormateur.setCV(existingFormateur.getCV());
            }

            if(updatedFormateur.getSkills()!=null){
                existingFormateur.setSkills(updatedFormateur.getSkills());
            }else {
                updatedFormateur.setSkills(existingFormateur.getSkills());
            }


            // Update formateur
            Formateur updatedFormateurResult = formateurService.updateFormateur(existingFormateur);

            if (updatedUserResult != null && updatedFormateurResult != null) {
                return ResponseEntity.ok("User and Formateur updated successfully.");
            } else {
                return ResponseEntity.badRequest().body("Failed to update User and Formateur.");
            }
        } else {
            return ResponseEntity.badRequest().body("Formateur not found or not provided.");
        }


    }

    @GetMapping("/getNumberOfAccounts/{type}")
    private ResponseEntity<?> getNumberOfAccounts(@PathVariable("type") String type) {
        Long nbr = userService.getNumberOfAccounts(type);

        return new ResponseEntity<>(nbr, HttpStatus.OK);
    }

    @GetMapping("/getNumberOfActivatedAccounts/{type}")
    private ResponseEntity<?> getNumberOfActivatedAccounts(@PathVariable("type") ERole type ) {
        Long nbr = userService.getNumberOfActivatedAccounts(type);

        return new ResponseEntity<>(nbr, HttpStatus.OK);
    }

    @GetMapping("/getTopCourses")
    private ResponseEntity<?> getTopCourses() {
    	try {
    		 List<CourseStatsDto> stats = new ArrayList<>();
    		 stats = userService.getTopCourses();
    		 return new ResponseEntity<>(stats, HttpStatus.OK);
    	}catch(NullPointerException e) {
    		System.err.println(e.getMessage());
    		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    	}
       
        
    }

}
