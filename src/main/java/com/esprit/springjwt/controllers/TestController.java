package com.esprit.springjwt.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
  @GetMapping("/all")
  public String allAccess() {
    return "Public Content.";
  }

  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMINISTRATEUR') or hasRole('ETUDIANT') or hasRole('FORMATEUR')")
  public String userAccess() {
    return "User Content les roles  lkol.";
  }

  @GetMapping("/ETUDIANT")
  @PreAuthorize("hasRole('ETUDIANT')")
  public String ETUDIANTAccess() {

    System.err.println("ena etudiant");
    return "ETUDIANT Board.";
  }

  @GetMapping("/FORMATEUR")
  @PreAuthorize("hasRole('FORMATEUR')")
  public String adminAccess() {
    return "FORMATEUR Board.";
  }
}
