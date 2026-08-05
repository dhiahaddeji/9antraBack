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
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
     JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}") private String sender;
    @Value("${resend.api.key:}") private String resendApiKey;

    private String resendFrom() {
        return "9antra Platform <onboarding@resend.dev>";
    }

    private void sendViaResend(String to, String subject, String htmlBody) throws Exception {
        if (resendApiKey == null || resendApiKey.isBlank()) throw new IllegalStateException("RESEND_API_KEY not configured");
        String escapedSubject = subject.replace("\"", "\\\"");
        String escapedBody = htmlBody.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
        String json = "{\"from\":\"" + resendFrom() + "\",\"to\":[\"" + to + "\"],\"subject\":\"" + escapedSubject + "\",\"html\":\"" + escapedBody + "\"}";
        URL url = new URL("https://api.resend.com/emails");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + resendApiKey);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes(StandardCharsets.UTF_8));
        }
        int code = conn.getResponseCode();
        if (code >= 300) {
            String err = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new RuntimeException("Resend API error " + code + ": " + err);
        }
    }

    // Method 1
    public String sendSimpleMail(String to, String subject, String text) {
        try {
            sendViaResend(to, subject, text);
            return "Mail Sent Successfully...";
        } catch (Exception e) {
            System.err.println("[Email] sendSimpleMail failed: " + e.getMessage());
            return "Error while Sending Mail: " + e.getMessage();
        }
    }

    public void sendCalendarInvite(String sessionName, String desc,
                                   Date startDate, Date endDate,
                                   String meetLink, List<String> attendeeEmails) {
        if (attendeeEmails == null || attendeeEmails.isEmpty()) return;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat readable = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm 'UTC'");
        readable.setTimeZone(TimeZone.getTimeZone("UTC"));
        String start = sdf.format(startDate);
        String end = sdf.format(endDate);
        String uid = UUID.randomUUID().toString();
        String ics = "BEGIN:VCALENDAR\r\nVERSION:2.0\r\nPRODID:-//9antra//Platform//EN\r\nMETHOD:REQUEST\r\n" +
            "BEGIN:VEVENT\r\nUID:" + uid + "\r\nDTSTART:" + start + "\r\nDTEND:" + end + "\r\n" +
            "SUMMARY:" + sessionName + "\r\n" +
            "DESCRIPTION:" + (desc != null ? desc : "") + (meetLink != null ? "\\nMeet: " + meetLink : "") + "\r\n" +
            "LOCATION:" + (meetLink != null ? meetLink : "") + "\r\n" +
            "END:VEVENT\r\nEND:VCALENDAR\r\n";
        String icsBase64 = java.util.Base64.getEncoder().encodeToString(ics.getBytes(StandardCharsets.UTF_8));
        String html = "<html><body>" +
            "<h2>📅 Session Invitation: " + sessionName + "</h2>" +
            "<p><b>Date:</b> " + readable.format(startDate) + " – " + readable.format(endDate) + "</p>" +
            (desc != null && !desc.isBlank() ? "<p><b>Description:</b> " + desc + "</p>" : "") +
            (meetLink != null ? "<p><b>Google Meet:</b> <a href='" + meetLink + "'>" + meetLink + "</a></p>" : "") +
            "<p style='color:#888'>Open the attached <b>invite.ics</b> to add this to your calendar.</p>" +
            "</body></html>";
        for (String email : attendeeEmails) {
            try {
                if (resendApiKey == null || resendApiKey.isBlank()) throw new IllegalStateException("RESEND_API_KEY not configured");
                String escapedSubject = ("📅 Session Invitation: " + sessionName).replace("\"", "\\\"");
                String escapedHtml = html.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
                String json = "{\"from\":\"" + resendFrom() + "\",\"to\":[\"" + email + "\"],\"subject\":\"" + escapedSubject + "\",\"html\":\"" + escapedHtml + "\"," +
                    "\"attachments\":[{\"filename\":\"invite.ics\",\"content\":\"" + icsBase64 + "\"}]}";
                URL url = new URL("https://api.resend.com/emails");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "Bearer " + resendApiKey);
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);
                try (OutputStream os = conn.getOutputStream()) { os.write(json.getBytes(StandardCharsets.UTF_8)); }
                int code = conn.getResponseCode();
                if (code >= 300) {
                    String err = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
                    System.err.println("[Email] Resend error " + code + " for " + email + ": " + err);
                } else {
                    System.out.println("[Email] Calendar invite sent to: " + email);
                }
            } catch (Exception e) {
                System.err.println("[Email] Failed to send invite to " + email + ": " + e.getMessage());
            }
        }
    }

    public void sendCalendarInvite_OLD(String sessionName, String description,
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
