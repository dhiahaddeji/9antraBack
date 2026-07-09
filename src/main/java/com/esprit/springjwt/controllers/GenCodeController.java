package com.esprit.springjwt.controllers;

import com.esprit.springjwt.Mail.Mail;
import com.esprit.springjwt.entity.GenCode;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.payload.response.MessageResponse;
import com.esprit.springjwt.repository.GencodeRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.service.EmailServiceImpl;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/resetpassword")

public class GenCodeController {
   @Autowired
   EmailServiceImpl emailService;

  @Autowired
  GencodeRepository gencodeRepository;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder encoder;

  @Autowired
  Mail mail;
  
  public String generateCode() {
    String code = "";
    for (int i = 0; i < 6; i++) {
      code += (int) (Math.random() * 10);
    }
    return code;
  }

  @PostMapping("/generatecode")
//post with check if email exist in user model
  public ResponseEntity<?> generateCode(@RequestBody GenCode genCode) throws UnsupportedEncodingException, MessagingException {
	  System.out.println("========================================");
	  System.out.println("RESET PASSWORD REQUEST FOR: " + genCode.getEmail());
	  System.out.println("========================================");
	  
    if (userRepository.existsByUsername(genCode.getEmail())) {
      User user =userRepository.findByEmail(genCode.getEmail()) ;
      gencodeRepository.save(genCode);
      
      System.out.println("========================================");
      System.out.println("CODE DE VÉRIFICATION POUR: " + genCode.getEmail());
      System.out.println("CODE: " + genCode.getCode());
      System.out.println("========================================");
      
      return ResponseEntity.ok(new MessageResponse("Code generated successfully!"));
    } else {
      System.out.println("========================================");
      System.out.println("ERROR: EMAIL NOT FOUND: " + genCode.getEmail());
      System.out.println("========================================");
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is not found!"));
    }
  }

  @GetMapping("/checkcode/{code}")
//check if code exist
  public ResponseEntity<?> checkCode(@PathVariable String code) {
    if (gencodeRepository.existsByCode(code)) {
      return ResponseEntity.ok(new MessageResponse("Code is correct!"));
    } else {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Code is not correct!"));
    }
  }

  // update password by email
  @PatchMapping("/updatepassword/{email}")
  public ResponseEntity<?> updatePassword(@PathVariable String email, @RequestBody User user) {
    if (userRepository.existsByUsername(email)) {
      User user1 = userRepository.findByUsername(email);
      user1.setPassword(encoder.encode(user.getPassword()));
      userRepository.save(user1);
      return ResponseEntity.ok(new MessageResponse("Password updated successfully!"));
    } else {
      return ResponseEntity
          .badRequest()
          .body(new MessageResponse("Error: Email is not found!"));
    }
  }



}
