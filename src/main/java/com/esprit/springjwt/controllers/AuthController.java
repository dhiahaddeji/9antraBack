package com.esprit.springjwt.controllers;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import com.esprit.springjwt.security.oauth2.TokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.*;
import java.util.stream.Collectors;

import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.service.IRequestService;
import com.esprit.springjwt.service.userService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import javax.validation.Valid;

import com.esprit.springjwt.Mail.Mail;
import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.repository.FormateurRepository;
import com.esprit.springjwt.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import com.esprit.springjwt.payload.request.LoginRequest;
import com.esprit.springjwt.payload.request.SignupRequest;
import com.esprit.springjwt.payload.response.JwtResponse;
import com.esprit.springjwt.payload.response.MessageResponse;
import com.esprit.springjwt.repository.RoleRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.security.jwt.JwtUtils;
import com.esprit.springjwt.security.services.UserDetailsImpl;
import org.springframework.web.multipart.MultipartFile;

import static com.esprit.springjwt.entity.AuthProvider.local;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    IRequestService requestService;


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    FormateurRepository formateurRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Value("${files.folder}")
    String filesFolder;
    @Autowired
    TokenProvider tokenProvider;
    @Autowired
    userService userService;

    @Autowired
    Mail mail;

    @PostMapping("/Signup")
    public ResponseEntity<?> Signup(@RequestBody User user){

        User testEmail = userRepository.findByEmail(user.getUsername());
        if(testEmail != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new MessageResponse("Email already used!"));
        }else {
            User newUser = new User();
            newUser.setFirstName(user.getFirstName());
            newUser.setLastName(user.getLastName());
            newUser.setNumeroTel(user.getNumeroTel());
            newUser.setUsername(user.getUsername());
            newUser.setPassword(encoder.encode(user.getPassword()));
            newUser.setCountry(user.getCountry());
            newUser.setProvider(local);
            newUser.setEnabled(0);
            newUser.setImage("avatarStudent.png");
            Set<Role> roles = new HashSet<>();
            Optional<Role> roleOptional = roleRepository.findByName(ERole.ETUDIANT);
            if (roleOptional.isPresent()) {
                roles.add(roleOptional.get());
            } else {
                return ResponseEntity.ok("Error: Role not found!");
            }

            newUser.setRoles(roles);
            userRepository.save(newUser);

            try {
                mail.sendVerificationEmail(newUser);
            } catch(Exception e) {
                // Mail not configured — auto-verify so the account is usable immediately.
                // Remove this block once real MAIL_USERNAME/MAIL_PASSWORD are set.
                newUser.setEmail_verified_at(new Date());
                userRepository.save(newUser);
            }
            return ResponseEntity.ok(newUser);
        }
    }

    @PostMapping("/verifytoken")
    public ResponseEntity<?> verifyToken(@RequestBody String token) {
        try {
            Long userId = tokenProvider.getUserIdFromToken(token);
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                return ResponseEntity.ok(user);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid token.");
        }
    }
    @PostMapping("/verify")
    public ResponseEntity<?> VerifiedEmail(@RequestBody String email){
        try {
            userService.VerifyEmail(email);
        }catch(Exception e) {
            return new ResponseEntity<String>(e.getMessage(),HttpStatus.CONFLICT);
        }
        return  ResponseEntity.ok().body("email verifed");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        // Check if the user exists by email
        User user = userRepository.findByEmail(loginRequest.getUsername());
        Optional<User> userOptional = userRepository.isfindByEmail(loginRequest.getUsername());

        if (userOptional.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("User does not exist!"));
        }

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
            if (user.getEmail_verified_at() == null) {
                // Auto-verify: mail server not configured, so unlock the account on first login
                user.setEmail_verified_at(new Date());
                userRepository.save(user);
            }
            if (userRepository.findUserByEnabled(loginRequest.getUsername()).isEmpty()) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("User Inactive!"));
            }



            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    roles));

        } catch (AuthenticationException e) {
            return ResponseEntity
                    .badRequest()
                    .body(new MessageResponse("Incorrect password!"));
        }
    }


    //public static String UPLOAD_DOCUMENTS = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\Documents\\";
    // public static String UPLOAD_DOCUMENTS = "C:\\Users\\Wale\\Desktop\\Final Design\\bridge\\src\\assets\\Documents\\";
    // public static String UPLOAD_DOCUMENTS = "C:\\Users\\zied1\\OneDrive\\Bureau\\L\\9antraFormationBack\\src\\main\\java\\com\\esprit\\springjwt\\files\\";


    @PostMapping("/signup")
    public ResponseEntity<?> registerUserCoach(@RequestParam("username") String username,
                                               @RequestParam("password") String password,
                                               @RequestParam("firstName") String firstName,
                                               @RequestParam("lastName") String lastName,
                                               @RequestParam("numeroTel") String numeroTel,
                                               @RequestParam("CV") MultipartFile files,
                                               @RequestParam("typeFormation") String typeFormation,
                                               @RequestParam("Github") String GithubLink,
                                               @RequestParam("country") String country,
                                               @RequestParam("skills") String skills,
                                               @RequestParam("Linkedin") String LinkedinLink
            /*    @RequestParam("about") String about*/

    ) throws IOException {

        String msj = "Bonjour " + firstName + " " + lastName + " votre compte a été crée avec succés";
        String subject = "Bienvenue sur 9antraTraining";

        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        // Create new user's account
        String currentDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String filesName = currentDate + files.getOriginalFilename();
        User user = new User();
        user.setUsername(username);
        user.setPassword(encoder.encode(password));
        user.setProvider(local);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setNumeroTel(numeroTel);
        Date d= new Date();
        user.setEmail_verified_at(d);
        user.setTypeFormation(typeFormation);
        /*    user.setAbout(about);*/

        user.setImage("avatarCoach.png");

        byte[] bytes1 = files.getBytes();
        //Path path1 = Paths.get(UPLOAD_DOCUMENTS + filesName);
        Path path1 = Paths.get(filesFolder + "Documents\\" + filesName);
        // Create directories if they don't exist
        Files.createDirectories(path1.getParent());
        Files.write(path1, bytes1);

        Set<Role> roles = new HashSet<>();
        Optional<Role> roleOptional = roleRepository.findByName(ERole.FORMATEUR);
        if (roleOptional.isPresent()) {
            roles.add(roleOptional.get());
        } else {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Role not found!"));
        }
        user.setRoles(roles);
        user.setCountry(country);
        // Auto-enable FORMATEUR accounts BEFORE saving
        user.setEnabled(1);

        User savedUser = userRepository.save(user);
        
        // Verify enabled status was saved
        if (savedUser.getEnabled() != 1) {
            System.err.println("WARNING: User enabled status not saved correctly!");
        }

        Formateur formateur = new Formateur();
        formateur.setCV(filesName);
        formateur.setGithub(GithubLink);
        formateur.setLinkedin(LinkedinLink);
        formateur.setSkills(skills);
        formateur.setUser(savedUser);
        formateurRepository.save(formateur);
        emailService.sendSimpleMail(username, subject, msj);
        return ResponseEntity.ok(new MessageResponse("Formateur registered successfully!"));
    }
    //private static final String DEFAULT_IMAGE_PATH = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\img\\profile-img.jpg";
    // private static final String DEFAULT_IMAGE_PATH = "C:\\Users\\Wale\\Desktop\\Final Design\\bridge\\src\\assets\\Documents\\";




    @PostMapping("/signupstudent/{idRequest}")
    public ResponseEntity<?> registerUserStudent(@PathVariable("idRequest") Long idRequest
    ) throws IOException {
        try {
            Request request = requestService.getOneById(idRequest);
            String msj = "Hi " + request.getFirstName() + " " + request.getLastName() + " Welcome to The-Bridge " +
                    "Thank you for your request for information regarding "+ request.getFormation().getNomFormation() + "Training" +
                    "We are pleased to inform you that you are now officially a member of our platform." +
                    " Just wait to be assigned to a group.."

                    ;






            String subject = "Welcome To 9antraTraining";



            if (userRepository.existsByUsername(request.getEmail())) {
//                return ResponseEntity
//                        .badRequest()
//                        .body(new MessageResponse("Error: email is already taken!"));
                return ResponseEntity.ok(new MessageResponse("student exists"));
            }


            //String imagePath = DEFAULT_IMAGE_PATH;
            String imagePath = filesFolder + "\\img\\profile-img.jpg";

            // Create new user's account

            User user = new User();
            user.setUsername(request.getEmail());
            user.setPassword(encoder.encode(request.getPhoneNumber().toString()));
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setNumeroTel(request.getPhoneNumber().toString());
            user.setTypeFormation(request.getFormation().getNomFormation());
            user.setCountry(request.getCountry());
            user.setImage("avatarStudent.png");




            Set<Role> roles = new HashSet<>();
            //set role of stroles
//            strRoles.forEach(role -> {
//                switch (role) {
//                    case "ADMINISTRATEUR":
//                        Role adminRole = roleRepository.findByName(ERole.ADMINISTRATEUR)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(adminRole);
//
//                        break;
//                    case "ETUDIANT":
//                        Role etudeRole = roleRepository.findByName(ERole.ETUDIANT)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(etudeRole);
//
//                        break;
//                    default:
//                        Role userRole = roleRepository.findByName(ERole.ETUDIANT)
//                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
//                        roles.add(userRole);
//
//                }
//
//            });
            Role etudeRole = roleRepository.findByName(ERole.ETUDIANT)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(etudeRole);



            user.setRoles(roles);
            user.setEnabled(1);
            userRepository.save(user);

            System.out.println("Student registered successfully: " + user.getFirstName() + " " + user.getLastName());
            return ResponseEntity.ok(new MessageResponse("Student registered successfully!"));




        } catch (RecordNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }





}
