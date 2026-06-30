package com.esprit.springjwt.flouci;


import com.esprit.springjwt.Mail.Mail;
import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.entity.e_learning.PaiementType;
import com.esprit.springjwt.repository.IRequestRepository;
import com.esprit.springjwt.repository.RoleRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.service.EmailServiceImpl;
import com.esprit.springjwt.service.GroupsService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static com.esprit.springjwt.entity.AuthProvider.local;

@Controller
@RequiredArgsConstructor
public class PayementController {
    private static final Logger logger = LoggerFactory.getLogger(PayementController.class);
    private final PayementService payementService;
    @Resource
    private GroupsService groupsService;
    @Autowired
    EmailServiceImpl emailService;

    @Resource
    Mail mail;
    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;
    @Resource
    IRequestRepository requestRepository;
    @Autowired
    PasswordEncoder encoder;
    @Value("${site.base.url.https}")
    private String baseurl;

    @GetMapping("api/payment/success")
    public String paymentSuccess(@RequestParam("payment_id") String paymentId, @RequestParam("studyplace") String studyplace, @RequestParam("id") Long id,@RequestParam("email") String email, @RequestParam("period") String period, @RequestParam("paiementType") String paiementType) throws IOException {
        boolean verifPayment = payementService.verifyPayment(paymentId);
        if (verifPayment) {


            Request r = requestRepository.getReferenceById(id);
            r.setEducationPlace(studyplace.toUpperCase());
            r.setRequestStatus(RequestStatus.PAID);
            r.setTrainingPeriod(period);
            r.setPaiementType(PaiementType.valueOf(paiementType));
            requestRepository.save(r);
            User testEmail = userRepository.findByEmail(email);
            if (testEmail != null && testEmail.getUsername()!="" ) {
                testEmail.setEnabled(1);
                userRepository.save(testEmail);
                List<Groups> groups = groupsService.getGroupsByFormation(r.getFormation().getId());
                if (!groups.isEmpty()) {    //bch nparcouri les groups ken el request type mt3ha 2months bch ylwj al  month 1 wonth 2 fl period fl groups wyaffecti hsinon hasb el request paymentoption bch yaffectih
                    for (Groups g : groups) {
                        System.out.print("groupdekhlin wlae" + period);
                        if (period.equals("2months")) {

                            if (g.getPeriod().equals("month1") || g.getPeriod().equals("month2")) {
                                List<User> u = g.getEtudiants();
                                boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId()));

                                // Check if the user is in any other group with the same training and period
                                if (!userExists) {
                                    boolean userInOtherGroup = groups.stream()
                                            .filter(otherGroup -> !otherGroup.getId().equals(g.getId())) // Exclude the current group
                                            .anyMatch(otherGroup ->
                                                    otherGroup.getFormation().getId().equals(r.getFormation().getId()) &&
                                                            otherGroup.getPeriod().equals(g.getPeriod()) &&
                                                            otherGroup.getEtudiants().stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId())));

                                    if (!userInOtherGroup) {
                                        groupsService.addEtudiantToGroup(g.getId(), testEmail.getId());
                                        break; // Exit loop after adding the user to the group
                                    } else {
                                        System.out.println("User is already in another group with the same training and period.");
                                        break; // Exit loop to avoid adding to multiple groups
                                    }
                                } else {
                                    System.out.println("User is already in the group.");
                                    break; // Exit loop to avoid adding to multiple groups
                                }
                            }
                        } else if (period.equals("month1")) {
                            if (g.getPeriod().equals("month1")) {
                                List<User> u = g.getEtudiants();
                                boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId()));

                                // Check if the user is in any other group with the same training and period
                                if (!userExists) {
                                    boolean userInOtherGroup = groups.stream()
                                            .filter(otherGroup -> !otherGroup.getId().equals(g.getId())) // Exclude the current group
                                            .anyMatch(otherGroup ->
                                                    otherGroup.getFormation().getId().equals(r.getFormation().getId()) &&
                                                            otherGroup.getPeriod().equals(g.getPeriod()) &&
                                                            otherGroup.getEtudiants().stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId())));

                                    if (!userInOtherGroup) {
                                        groupsService.addEtudiantToGroup(g.getId(), testEmail.getId());
                                        break; // Exit loop after adding the user to the group
                                    } else {
                                        System.out.println("User is already in another group with the same training and period.");
                                        break; // Exit loop to avoid adding to multiple groups
                                    }
                                } else {
                                    System.out.println("User is already in the group.");
                                    break; // Exit loop to avoid adding to multiple groups
                                }
                            }
                        } else if (period.equals("month2")) {
                            if (g.getPeriod().equals("month2")) {
                                List<User> u = g.getEtudiants();
                                boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId()));

                                // Check if the user is in any other group with the same training and period
                                if (!userExists) {
                                    boolean userInOtherGroup = groups.stream()
                                            .filter(otherGroup -> !otherGroup.getId().equals(g.getId())) // Exclude the current group
                                            .anyMatch(otherGroup ->
                                                    otherGroup.getFormation().getId().equals(r.getFormation().getId()) &&
                                                            otherGroup.getPeriod().equals(g.getPeriod()) &&
                                                            otherGroup.getEtudiants().stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId())));

                                    if (!userInOtherGroup) {
                                        groupsService.addEtudiantToGroup(g.getId(), testEmail.getId());
                                        break; // Exit loop after adding the user to the group
                                    } else {
                                        System.out.println("User is already in another group with the same training and period.");
                                        break; // Exit loop to avoid adding to multiple groups
                                    }
                                } else {
                                    System.out.println("User is already in the group.");
                                    break; // Exit loop to avoid adding to multiple groups
                                }
                            }
                        }
                    }
                }

                // Assuming testEmail is retrieved based on the email parameter


                String firstName = testEmail.getFirstName();
                String lastName = testEmail.getLastName();
                String username = testEmail.getUsername();

                String msj = String.format("Hello %s %s,\n\nYour payment was successfully processed.\nStudy Place: %s\nPeriod: %s\nPayment Type: %s\n\nThank you for your trust.\n\nBest regards,\nThe 9antraTraining Team",
                        firstName, lastName,studyplace, period, paiementType);

                String subject = "Payment Successful - 9antraTraining";

                // Send the email
                emailService.sendSimpleMail(username, subject, msj);
                return "redirect:" + baseurl + "/payment/paymentSuccess";


            } else {
                User newUser = new User();
                newUser.setFirstName(r.getFirstName());
                newUser.setLastName(r.getLastName());
                newUser.setNumeroTel(r.getPhoneNumber());
                newUser.setUsername(email);
                newUser.setEnabled(1);
                newUser.setProvider(local);

                // Encode password
                String encodedPassword = encoder.encode(r.getPhoneNumber());
                newUser.setPassword(encodedPassword);
                newUser.setCountry(r.getCountry());
                newUser.setImage("avatarStudent.png");
                Set<Role> roles = new HashSet<>();
                Optional<Role> roleOptional = roleRepository.findByName(ERole.ETUDIANT);
                if (roleOptional.isPresent()) {
                    roles.add(roleOptional.get());
                }

                newUser.setRoles(roles);
                System.out.println("temchi");
                try {
                    mail.sendVerificationEmail(newUser);
                    System.out.println("temchi");
                    userRepository.save(newUser);
                } catch (MessagingException e) {
                    System.out.println("Error Connexion");

                } catch (UnsupportedEncodingException e) {
                    System.out.println("Unsupported Forme");

                }


//find groups by training id
                List<Groups> groups = groupsService.getGroupsByFormation(r.getFormation().getId());
                if (!groups.isEmpty()) {
                    //bch nparcouri les groups ken el request type mt3ha 2months bch ylwj al  month 1 wonth 2 fl period fl groups wyaffecti hsinon hasb el request paymentoption bch yaffectih
                    for (Groups g : groups) {
                        if (r.getTrainingPeriod().equals("2months")) {
                            if (g.getPeriod().equals("month1") || g.getPeriod().equals("month2")) {
                                List<User> u = g.getEtudiants();
                                boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(newUser.getId()));

                                if (!userExists) {
                                    groupsService.addEtudiantToGroup(g.getId(), newUser.getId());
                                } else {
                                    System.out.println("User is already in the group.");
                                }
                            }
                        } else if (r.getTrainingPeriod().equals("month1")) {
                            if (g.getPeriod().equals("month1")) {
                                List<User> u = g.getEtudiants();
                                boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(newUser.getId()));

                                if (!userExists) {
                                    groupsService.addEtudiantToGroup(g.getId(), newUser.getId());
                                } else {
                                    System.out.println("User is already in the group.");
                                }
                            }
                        } else if (r.getTrainingPeriod().equals("month2")) {
                            if (g.getPeriod().equals("month2")) {
                                List<User> u = g.getEtudiants();
                                boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(newUser.getId()));

                                if (!userExists) {
                                    groupsService.addEtudiantToGroup(g.getId(), newUser.getId());
                                } else {
                                    System.out.println("User is already in the group.");
                                }
                            }
                        }
                    }
                }

                String firstName = newUser.getFirstName();
                String lastName =  newUser.getLastName();
                String username =  newUser.getUsername();

                String msj = String.format("Hello %s %s,\n\nYour payment was successfully processed.\nStudy Place: %s\nPeriod: %s\nPayment Type: %s\n\nThank you for your trust.\n\nBest regards,\nThe 9antraTraining Team",
                        firstName, lastName,  studyplace, period, paiementType);

                String subject = "Payment Successful - 9antraTraining";

                // Send the email
                emailService.sendSimpleMail(username, subject, msj);
                return "redirect:" + baseurl + "/payment/paymentSuccess";
            }
        } else {
            return "redirect:" + baseurl + "/payment/paymentError";
        }
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("api/payment/error")
    public String paymentError() {
        return "redirect:" + baseurl + "/payment/paymentError";
    }

    @PostMapping("api/payment/create")
    public ResponseEntity<?> createPayment(@RequestParam("amount") Integer amount,
                                           @RequestParam("studyplace") String studyplace,
                                           @RequestParam("id") Long id,
                                           @RequestParam("email") String email,
                                           @RequestParam("period") String period,
                                           @RequestParam("paiementType") String paiementType) throws IOException {
        ResponsePayment responsePayment = payementService.generatePayment(amount, studyplace, id,email, period, paiementType);
        return ResponseEntity.ok(responsePayment);
    }

    @GetMapping("api/payment/onsite")
    public ResponseEntity<?> paymentOnsite(@RequestParam("id") Long id, @RequestParam("email") String email, @RequestParam("period") String period) {


       Request r = requestRepository.getReferenceById(id);
        User testEmail = userRepository.findByEmail(email);

        if (testEmail != null && testEmail.getUsername()!="" ) {

            testEmail.setEnabled(1);
            logger.info("l9ah");
            logger.info(testEmail.getUsername());
            userRepository.save(testEmail);
            List<Groups> groups = groupsService.getGroupsByFormation(r.getFormation().getId());
            if (!groups.isEmpty()) {    //bch nparcouri les groups ken el request type mt3ha 2months bch ylwj al  month 1 wonth 2 fl period fl groups wyaffecti hsinon hasb el request paymentoption bch yaffectih
                for (Groups g : groups) {
                    System.out.print("groupdekhlin wlae" + period);
                    if (period.equals("2months")) {

                        if (g.getPeriod().equals("month1") || g.getPeriod().equals("month2")) {
                            List<User> u = g.getEtudiants();
                            boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId()));

                            // Check if the user is in any other group with the same training and period
                            if (!userExists) {
                                boolean userInOtherGroup = groups.stream()
                                        .filter(otherGroup -> !otherGroup.getId().equals(g.getId())) // Exclude the current group
                                        .anyMatch(otherGroup ->
                                                otherGroup.getFormation().getId().equals(r.getFormation().getId()) &&
                                                        otherGroup.getPeriod().equals(g.getPeriod()) &&
                                                        otherGroup.getEtudiants().stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId())));

                                if (!userInOtherGroup) {
                                    groupsService.addEtudiantToGroup(g.getId(), testEmail.getId());
                                    break; // Exit loop after adding the user to the group
                                } else {
                                    System.out.println("User is already in another group with the same training and period.");
                                    break; // Exit loop to avoid adding to multiple groups
                                }
                            } else {
                                System.out.println("User is already in the group.");
                                break; // Exit loop to avoid adding to multiple groups
                            }
                        }
                    } else if (period.equals("month1")) {
                        if (g.getPeriod().equals("month1")) {
                            List<User> u = g.getEtudiants();
                            boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId()));

                            // Check if the user is in any other group with the same training and period
                            if (!userExists) {
                                boolean userInOtherGroup = groups.stream()
                                        .filter(otherGroup -> !otherGroup.getId().equals(g.getId())) // Exclude the current group
                                        .anyMatch(otherGroup ->
                                                otherGroup.getFormation().getId().equals(r.getFormation().getId()) &&
                                                        otherGroup.getPeriod().equals(g.getPeriod()) &&
                                                        otherGroup.getEtudiants().stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId())));

                                if (!userInOtherGroup) {
                                    groupsService.addEtudiantToGroup(g.getId(), testEmail.getId());
                                    break; // Exit loop after adding the user to the group
                                } else {
                                    System.out.println("User is already in another group with the same training and period.");
                                    break; // Exit loop to avoid adding to multiple groups
                                }
                            } else {
                                System.out.println("User is already in the group.");
                                break; // Exit loop to avoid adding to multiple groups
                            }
                        }
                    } else if (period.equals("month2")) {
                        if (g.getPeriod().equals("month2")) {
                            List<User> u = g.getEtudiants();
                            boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId()));

                            // Check if the user is in any other group with the same training and period
                            if (!userExists) {
                                boolean userInOtherGroup = groups.stream()
                                        .filter(otherGroup -> !otherGroup.getId().equals(g.getId())) // Exclude the current group
                                        .anyMatch(otherGroup ->
                                                otherGroup.getFormation().getId().equals(r.getFormation().getId()) &&
                                                        otherGroup.getPeriod().equals(g.getPeriod()) &&
                                                        otherGroup.getEtudiants().stream().anyMatch(etudiant -> etudiant.getId().equals(testEmail.getId())));

                                if (!userInOtherGroup) {
                                    groupsService.addEtudiantToGroup(g.getId(), testEmail.getId());
                                    break; // Exit loop after adding the user to the group
                                } else {
                                    System.out.println("User is already in another group with the same training and period.");
                                    break; // Exit loop to avoid adding to multiple groups
                                }
                            } else {
                                System.out.println("User is already in the group.");
                                break; // Exit loop to avoid adding to multiple groups
                            }
                        }
                    }
                }
            }

            String firstName = testEmail.getFirstName();
            String lastName = testEmail.getLastName();
            String username = testEmail.getUsername();

            String msj = String.format("Hello %s %s,\n\nYour payment was successfully processed.\nStudy Place: %s\nPeriod: %s\nPayment Type: %s\n\nThank you for your trust.\n\nBest regards,\nThe 9antraTraining Team",
                    firstName, lastName,r.getEducationPlace(), period, r.getPaiementType());

            String subject = "Payment Successful - 9antraTraining";

            // Send the email
            emailService.sendSimpleMail(username, subject, msj);
            return ResponseEntity.accepted().build();


        } else {
            User newUser = new User();
            newUser.setFirstName(r.getFirstName());
            newUser.setLastName(r.getLastName());
            newUser.setNumeroTel(r.getPhoneNumber());
            newUser.setUsername(email);
            newUser.setEnabled(1);


            // Encode password
            String encodedPassword = encoder.encode(r.getPhoneNumber());
            newUser.setPassword(encodedPassword);
            newUser.setCountry(r.getCountry());
            newUser.setProvider(local);
            newUser.setImage("avatarStudent.png");
            Set<Role> roles = new HashSet<>();
            Optional<Role> roleOptional = roleRepository.findByName(ERole.ETUDIANT);
            if (roleOptional.isPresent()) {
                roles.add(roleOptional.get());
            }

            newUser.setRoles(roles);
            logger.info("Sending verification email1...");
            try {
                mail.sendVerificationEmail(newUser);
                System.out.println("temchi");
                userRepository.save(newUser);
            } catch (MessagingException e) {
                System.out.println("Error Connexion");

            } catch (UnsupportedEncodingException e) {
                System.out.println("Unsupported Forme");

            }




//find groups by training id
            List<Groups> groups = groupsService.getGroupsByFormation(r.getFormation().getId());
            if (!groups.isEmpty()) {

                //bch nparcouri les groups ken el request type mt3ha 2months bch ylwj al  month 1 wonth 2 fl period fl groups wyaffecti hsinon hasb el request paymentoption bch yaffectih
                for (Groups g : groups) {
                    if (r.getTrainingPeriod().equals("2months")) {
                        if (g.getPeriod().equals("month1") || g.getPeriod().equals("month2")) {
                            List<User> u = g.getEtudiants();
                            boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(newUser.getId()));

                            if (!userExists) {
                                groupsService.addEtudiantToGroup(g.getId(), newUser.getId());
                            } else {
                                System.out.println("User is already in the group.");
                            }
                        }
                    } else if (r.getTrainingPeriod().equals("month1")) {
                        if (g.getPeriod().equals("month1")) {
                            List<User> u = g.getEtudiants();
                            boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(newUser.getId()));

                            if (!userExists) {
                                groupsService.addEtudiantToGroup(g.getId(), newUser.getId());
                            } else {
                                System.out.println("User is already in the group.");
                            }
                        }
                    } else if (r.getTrainingPeriod().equals("month2")) {
                        if (g.getPeriod().equals("month2")) {
                            List<User> u = g.getEtudiants();
                            boolean userExists = u.stream().anyMatch(etudiant -> etudiant.getId().equals(newUser.getId()));

                            if (!userExists) {
                                groupsService.addEtudiantToGroup(g.getId(), newUser.getId());
                            } else {
                                System.out.println("User is already in the group.");
                            }
                        }
                    }
                }
            }

            String firstName = newUser.getFirstName();
            String lastName = newUser.getLastName();
            String username = newUser.getUsername();

            String msj = String.format("Hello %s %s,\n\nYour payment was successfully processed.\nStudy Place: %s\nPeriod: %s\nPayment Type: %s\n\nThank you for your trust.\n\nBest regards,\nThe 9antraTraining Team",
                    firstName, lastName,r.getEducationPlace(), period, r.getPaiementType());

            String subject = "Payment Successful - 9antraTraining";

            // Send the email
            emailService.sendSimpleMail(username, subject, msj);
            return ResponseEntity.accepted().build();
        }

    }
}


