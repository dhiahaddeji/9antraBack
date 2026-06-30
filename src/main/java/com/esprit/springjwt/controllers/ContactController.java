package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.Contact;
import com.esprit.springjwt.service.ContactService;
import com.esprit.springjwt.service.EmailServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/contact")
public class ContactController {
    @Autowired
    private ContactService contactService;
    @Autowired
    private EmailServiceImpl emailService;
    @Autowired
    private JavaMailSender emailSender;

    //add contact
    @PostMapping("/addContact")
    public Contact addContact(@RequestBody Contact contact) {
        Contact savedContact = contactService.addContact(contact);

        // Send email
        String subject = "New Contact Added";
        String body = "A new contact has been added:\n\n" +
                "First Name: " + contact.getFirstName() + "\n" +
                "Last Name: " + contact.getLastName() + "\n" +
                "Email: " + contact.getEmail() + "\n" +
                "Subject: " + contact.getSubject() + "\n" +
                "Message: " + contact.getMessage();

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("zied.19965@gmail.com"); // Replace with the recipient email address
        message.setSubject(subject);
        message.setText(body);

        emailSender.send(message);

        return savedContact;
    }
}
