package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.EmailDetails;
import com.esprit.springjwt.entity.User;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;

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
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
     JavaMailSender javaMailSender;

    @Value("${spring.mail.username:contact@9antra.tn}") private String sender;
    @Value("${resend.api.key:}") private String resendApiKey;
    @Value("${brevo.api.key:}") private String brevoApiKey;
    @Value("${mailjet.api.key:}") private String mailjetApiKey;
    @Value("${mailjet.secret.key:}") private String mailjetSecretKey;

    private void sendViaMailjet(String to, String subject, String htmlBody, String icsBase64) throws Exception {
        if (mailjetApiKey == null || mailjetApiKey.isBlank()) throw new IllegalStateException("MAILJET_API_KEY not configured");
        String auth = java.util.Base64.getEncoder().encodeToString((mailjetApiKey + ":" + mailjetSecretKey).getBytes(StandardCharsets.UTF_8));
        String subjectEsc = subject.replace("\"", "\\\"");
        String bodyEsc = htmlBody.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
        String attachPart = icsBase64 != null
            ? ",\"Attachments\":[{\"ContentType\":\"text/calendar\",\"Filename\":\"invite.ics\",\"Base64Content\":\"" + icsBase64 + "\"}]"
            : "";
        String json = "{\"Messages\":[{" +
            "\"From\":{\"Email\":\"" + sender + "\",\"Name\":\"9antra Platform\"}," +
            "\"To\":[{\"Email\":\"" + to + "\"}]," +
            "\"Subject\":\"" + subjectEsc + "\"," +
            "\"HTMLPart\":\"" + bodyEsc + "\"" + attachPart + "}]}";
        URL url = new URL("https://api.mailjet.com/v3.1/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Basic " + auth);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        try (OutputStream os = conn.getOutputStream()) { os.write(json.getBytes(StandardCharsets.UTF_8)); }
        int code = conn.getResponseCode();
        if (code >= 300) {
            String err = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new RuntimeException("Mailjet API error " + code + ": " + err);
        }
    }

    private void sendViaGmailAPI(String to, String subject, String htmlBody, String icsBase64) throws Exception {
        String serviceAccountJson = System.getenv("GOOGLE_SERVICE_ACCOUNT_JSON");
        if (serviceAccountJson == null || serviceAccountJson.isBlank()) {
            throw new IllegalStateException("GOOGLE_SERVICE_ACCOUNT_JSON not set");
        }
        String cleanJson = serviceAccountJson.replace("\\n", "\n");
        ServiceAccountCredentials saCreds = (ServiceAccountCredentials) ServiceAccountCredentials
            .fromStream(new ByteArrayInputStream(cleanJson.getBytes(StandardCharsets.UTF_8)));
        GoogleCredentials credentials = saCreds
            .createScoped(Collections.singletonList("https://www.googleapis.com/auth/gmail.send"))
            .createDelegated(sender);
        credentials.refreshIfExpired();
        String accessToken = credentials.getAccessToken().getTokenValue();

        String boundary = "bound_" + UUID.randomUUID().toString().replace("-", "");
        StringBuilder mime = new StringBuilder();
        mime.append("MIME-Version: 1.0\r\n");
        mime.append("To: ").append(to).append("\r\n");
        mime.append("From: 9antra Platform <").append(sender).append(">\r\n");
        mime.append("Subject: ").append(subject).append("\r\n");
        if (icsBase64 != null) {
            mime.append("Content-Type: multipart/mixed; boundary=\"").append(boundary).append("\"\r\n\r\n");
            mime.append("--").append(boundary).append("\r\n");
            mime.append("Content-Type: text/html; charset=UTF-8\r\n\r\n");
            mime.append(htmlBody).append("\r\n");
            mime.append("--").append(boundary).append("\r\n");
            mime.append("Content-Type: text/calendar; charset=UTF-8; method=REQUEST\r\n");
            mime.append("Content-Transfer-Encoding: base64\r\n");
            mime.append("Content-Disposition: attachment; filename=\"invite.ics\"\r\n\r\n");
            mime.append(icsBase64).append("\r\n");
            mime.append("--").append(boundary).append("--\r\n");
        } else {
            mime.append("Content-Type: text/html; charset=UTF-8\r\n\r\n");
            mime.append(htmlBody);
        }
        String rawMessage = java.util.Base64.getUrlEncoder().withoutPadding()
            .encodeToString(mime.toString().getBytes(StandardCharsets.UTF_8));

        String jsonBody = "{\"raw\":\"" + rawMessage + "\"}";
        URL url = new URL("https://gmail.googleapis.com/gmail/v1/users/me/messages/send");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Authorization", "Bearer " + accessToken);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        try (OutputStream os = conn.getOutputStream()) { os.write(jsonBody.getBytes(StandardCharsets.UTF_8)); }
        int code = conn.getResponseCode();
        if (code >= 300) {
            String err = new String(conn.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
            throw new RuntimeException("Gmail API error " + code + ": " + err);
        }
        System.out.println("[Email] Sent via Gmail API to: " + to);
    }

    private void sendEmail(String to, String subject, String htmlBody, String icsBase64) {
        try {
            sendViaGmailAPI(to, subject, htmlBody, icsBase64);
        } catch (Exception gmailEx) {
            System.err.println("[Email] Gmail API failed, falling back to Mailjet: " + gmailEx.getMessage());
            try {
                sendViaMailjet(to, subject, htmlBody, icsBase64);
            } catch (Exception mjEx) {
                System.err.println("[Email] Mailjet also failed: " + mjEx.getMessage());
                throw new RuntimeException("All email providers failed", mjEx);
            }
        }
    }

    // Method 1
    public String sendSimpleMail(String to, String subject, String text) {
        try {
            sendEmail(to, subject, text, null);
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
        String meetButton = meetLink != null
            ? "<div style='margin:24px 0;'><a href='" + meetLink + "' style='background:#af3065;color:#fff;padding:12px 28px;border-radius:8px;text-decoration:none;font-size:16px;font-weight:bold;display:inline-block;'>📹 Join Google Meet</a></div>"
            : "";
        String html = "<html><body style='font-family:Arial,sans-serif;background:#f5f7f9;padding:20px;'>" +
            "<div style='max-width:560px;margin:auto;background:#fff;border-radius:12px;overflow:hidden;box-shadow:0 2px 12px rgba(0,0,0,.1);'>" +
            "<div style='background:linear-gradient(135deg,#af3065,#e071a9);padding:28px 32px;'>" +
            "<h2 style='color:#fff;margin:0;font-size:22px;'>📅 Session Invitation</h2>" +
            "<p style='color:rgba(255,255,255,.85);margin:6px 0 0;font-size:16px;'>" + sessionName + "</p>" +
            "</div>" +
            "<div style='padding:28px 32px;'>" +
            "<table style='width:100%;border-collapse:collapse;'>" +
            "<tr><td style='padding:8px 0;color:#888;width:110px;'>📆 Date</td><td style='padding:8px 0;font-weight:bold;'>" + readable.format(startDate) + "</td></tr>" +
            "<tr><td style='padding:8px 0;color:#888;'>⏰ Time</td><td style='padding:8px 0;font-weight:bold;'>" + readable.format(startDate) + " – " + readable.format(endDate) + "</td></tr>" +
            (desc != null && !desc.isBlank() ? "<tr><td style='padding:8px 0;color:#888;'>📝 Description</td><td style='padding:8px 0;'>" + desc + "</td></tr>" : "") +
            "</table>" +
            meetButton +
            "<hr style='border:none;border-top:1px solid #eee;margin:20px 0;'/>" +
            "<p style='color:#aaa;font-size:13px;'>Open the attached <b>invite.ics</b> to add this session to your calendar.</p>" +
            "</div></div></body></html>";
        for (String email : attendeeEmails) {
            try {
                sendEmail(email, "Session Invitation: " + sessionName, html, icsBase64);
                System.out.println("[Email] Calendar invite sent to: " + email);
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
