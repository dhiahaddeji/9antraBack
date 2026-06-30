package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.EmailDetails;
import com.esprit.springjwt.entity.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.UnsupportedEncodingException;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
     JavaMailSender javaMailSender;

    @Value("${spring.mail.username}") private String sender;

    // Method 1
    // To send a simple email
    public String sendSimpleMail(String to, String subject, String text)
    {

        // Try block to check for exceptions
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage
                    = new SimpleMailMessage();
            // Creating a MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();

            // Creating a helper for the MimeMessage
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            // Setting up necessary details
           /* mailMessage.setFrom(sender);
            mailMessage.setTo(to);
            mailMessage.setText(text);
            mailMessage.setSubject(subject);*/
            // Setting up necessary details
            helper.setFrom(sender);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);  // Set to true for HTML content

            // Sending the mail
            javaMailSender.send(message);
            return "Mail Sent Successfully...";

            // Sending the mail
          /*  javaMailSender.send(mailMessage);
            return "Mail Sent Successfully...";*/
        }

        // Catch block to handle the exceptions
        catch (Exception e) {
            return "Error while Sending Mail";
        }
    }

    // Method 2
    // To send an email with attachment
    public String
    sendMailWithAttachment(EmailDetails details)
    {
        // Creating a mime message
        MimeMessage mimeMessage
                = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper
            = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(
                    details.getSubject());

            // Adding the attachment
            FileSystemResource file
                    = new FileSystemResource(
                    new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(
                    file.getFilename(), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        }

        // Catch block to handle MessagingException
        catch (MessagingException e) {

            // Display message when exception occurred
            return "Error while sending mail!!!";
        }
    }
    
}
