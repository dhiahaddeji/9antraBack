package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.EmailDetails;

import com.esprit.springjwt.service.EmailService;
import com.esprit.springjwt.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api")
@CrossOrigin(value = "*")
public class EmailController {

/*
    @Autowired
    EmailServiceImpl emailService;

    // Sending a simple Email
    @PostMapping("/sendEmail")
    public String
    sendMail(@RequestBody EmailDetails details)
    {
       return emailService.sendSimpleMail(details);

    }

    // Sending email with attachment
    @PostMapping("/sendMailWithAttachment")
    public String sendMailWithAttachment(
            @RequestBody EmailDetails details)
    {
        String status
                = emailService.sendMailWithAttachment(details);

        return status;
    }*/
}
