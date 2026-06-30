package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.EmailDetails;
import com.esprit.springjwt.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public interface EmailService {

    // Method
    // To send a simple email
    String sendSimpleMail(String to, String subject, String text);

    // Method
    // To send an email with attachment
    String sendMailWithAttachment(EmailDetails details);
    

}