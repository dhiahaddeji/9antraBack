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

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

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

    public void sendCalendarInvite(String sessionName, String description,
                                   Date startDate, Date endDate,
                                   String meetLink, List<String> attendeeEmails) {
        if (attendeeEmails == null || attendeeEmails.isEmpty()) return;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        String start = sdf.format(startDate);
        String end = sdf.format(endDate);
        String uid = UUID.randomUUID().toString();

        StringBuilder attendeeLines = new StringBuilder();
        for (String email : attendeeEmails) {
            attendeeLines.append("ATTENDEE;CUTYPE=INDIVIDUAL;ROLE=REQ-PARTICIPANT;PARTSTAT=NEEDS-ACTION;RSVP=TRUE;CN=")
                .append(email).append(":mailto:").append(email).append("\r\n");
        }

        String ics = "BEGIN:VCALENDAR\r\n" +
            "VERSION:2.0\r\n" +
            "PRODID:-//9antra//9antra Platform//EN\r\n" +
            "METHOD:REQUEST\r\n" +
            "BEGIN:VEVENT\r\n" +
            "UID:" + uid + "\r\n" +
            "DTSTART:" + start + "\r\n" +
            "DTEND:" + end + "\r\n" +
            "SUMMARY:" + sessionName + "\r\n" +
            "DESCRIPTION:" + (description != null ? description : "") +
                (meetLink != null ? "\\nGoogle Meet: " + meetLink : "") + "\r\n" +
            "LOCATION:" + (meetLink != null ? meetLink : "") + "\r\n" +
            "ORGANIZER;CN=9antra Platform:mailto:" + sender + "\r\n" +
            attendeeLines +
            "BEGIN:VALARM\r\n" +
            "TRIGGER:-PT30M\r\n" +
            "ACTION:DISPLAY\r\n" +
            "DESCRIPTION:Reminder: " + sessionName + "\r\n" +
            "END:VALARM\r\n" +
            "END:VEVENT\r\n" +
            "END:VCALENDAR\r\n";

        for (String email : attendeeEmails) {
            try {
                MimeMessage message = javaMailSender.createMimeMessage();
                MimeMultipart multipart = new MimeMultipart("mixed");

                MimeBodyPart htmlPart = new MimeBodyPart();
                String htmlBody = "<html><body>" +
                    "<h2>📅 Session Invitation: " + sessionName + "</h2>" +
                    "<p><b>Date:</b> " + startDate + "</p>" +
                    "<p><b>Time:</b> " + sdf.format(startDate) + " – " + sdf.format(endDate) + " UTC</p>" +
                    (description != null ? "<p><b>Description:</b> " + description + "</p>" : "") +
                    (meetLink != null ? "<p><b>Google Meet:</b> <a href='" + meetLink + "'>" + meetLink + "</a></p>" : "") +
                    "<p>Please open the attached <b>.ics</b> file to add this session to your calendar.</p>" +
                    "</body></html>";
                htmlPart.setContent(htmlBody, "text/html; charset=UTF-8");
                multipart.addBodyPart(htmlPart);

                MimeBodyPart icsPart = new MimeBodyPart();
                DataSource ds = new ByteArrayDataSource(ics.getBytes("UTF-8"), "text/calendar; charset=UTF-8; method=REQUEST");
                icsPart.setDataHandler(new DataHandler(ds));
                icsPart.setHeader("Content-Type", "text/calendar; charset=UTF-8; method=REQUEST");
                icsPart.setFileName("invite.ics");
                multipart.addBodyPart(icsPart);

                message.setFrom(sender);
                message.setRecipients(javax.mail.Message.RecipientType.TO, email);
                message.setSubject("📅 Session Invitation: " + sessionName);
                message.setContent(multipart);

                javaMailSender.send(message);
                System.out.println("[Email] Calendar invite sent to: " + email);
            } catch (Exception e) {
                System.err.println("[Email] Failed to send invite to " + email + ": " + e.getMessage());
            }
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
