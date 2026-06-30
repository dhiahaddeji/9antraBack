package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.Formateur;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.payload.request.PaginateInfoUser;
import com.esprit.springjwt.repository.FormateurRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import com.esprit.springjwt.service.FormateurService;
import com.esprit.springjwt.service.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.IntStream;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/formateur")
public class FormateurController {
    @Autowired
    private userService userService;
    @Autowired
    FormateurService formateurService;
    @Autowired
    UserRepository userRepository;


    @GetMapping("/all")
    public List<Formateur> getAll() {
        return formateurService.getAllFormateurs();
    }
    @GetMapping("/allPaginate")
    public ResponseEntity<?> getAllUsersPaginate(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "per_page", defaultValue = "3") int size) {
        if (page < 0 || size <= 0) {
            return ResponseEntity.badRequest().body("Invalid page or per_page values.");
        }

        try {
            Page<Formateur> formateurs = formateurService.getAllFormateurPaginate(PageRequest.of(page, size));
            PaginateInfoUser data = new PaginateInfoUser(
                    IntStream.range(0, formateurs.getTotalPages()).toArray(),
                    formateurs,
                    page);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error while fetching paginated users: " + e.getMessage());
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUserDetails() {
        User currentUser;

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            currentUser = userDetails.getUser();
            return ResponseEntity.ok(currentUser);

        } else {
        	return ResponseEntity.ok("User not found");
        }

        
    }

    @PutMapping("/updateFormateurByIdUser")
    public Formateur updateFormateurByIdUser(@RequestBody Formateur formateur) {
        return formateurService.updateFormateurByIdUser(formateur);
    }
}




