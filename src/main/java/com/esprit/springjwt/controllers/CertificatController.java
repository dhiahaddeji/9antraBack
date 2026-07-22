package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.*;
import com.esprit.springjwt.repository.CertificatRepository;
import com.esprit.springjwt.repository.EtudiantRepository;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.UserRepository;
import com.esprit.springjwt.service.CertificateService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.bind.annotation.*;
import org.json.simple.parser.JSONParser;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/certif")
@Transactional

public class CertificatController {
    @Autowired
    protected GroupsRepository groupsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private CertificatRepository certificatRepository;
    @Autowired
    private CertificateService certificateService;

    @Value("${files.folder}")
    String filesFolder;

    @Value("${site.base.url.https:http://localhost:4200}")
    private String siteBaseUrl;

    private static String rootPath = ClassPathResource.class.getClassLoader().getResource("").getPath();

    @PostMapping("/Generer")
    public ResponseEntity<?> genererCertificat(@RequestBody String c)throws RuntimeException, IOException, DocumentException
    {
        try {

            Object obj = new JSONParser().parse(c);
            JSONObject jo = (JSONObject) obj;

            String liste = (String) jo.get("liste");
            String periode = (String) jo.get("periode");
            String nom_formation = (String) jo.get("nom_formation");
            String month = (String) jo.get("month");
            String date = (String) jo.get("date");

            // Validation
            if (liste == null || liste.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("List of students is required");
            }
            if (periode == null || periode.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Period is required");
            }
            if (nom_formation == null || nom_formation.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Formation name is required");
            }
            if (month == null || month.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Month is required");
            }

            Random random = new Random();
            int randomNumber = random.nextInt(9999999) + 1;
            String certifDirName = nom_formation + " " + month + " " + randomNumber;
            File f = new File(filesFolder + "/Certifications/" + certifDirName);
            f.mkdirs();
            System.out.println("Certif directory: " + f.getAbsolutePath() + " exists=" + f.exists());

            int successCount = 0;
            int failureCount = 0;
            StringBuilder errorMessages = new StringBuilder();

            for (String s: liste.split("\n")) {
                String entry = s.replace("\n", "").replace("\r", "").trim();

                // Skip empty lines
                if (entry.isEmpty()) {
                    continue;
                }

                // Format: "userId:firstName lastName"
                Long studentUserId = null;
                String studentName;
                if (entry.contains(":")) {
                    String[] parts = entry.split(":", 2);
                    try { studentUserId = Long.parseLong(parts[0].trim()); } catch (Exception ignored) {}
                    studentName = parts[1].trim();
                } else {
                    studentName = entry;
                }

                String pdfname = f.getAbsolutePath() + "/" + studentName + ".pdf";
                String relativePath = "Certifications/" + certifDirName + "/" + studentName + ".pdf";

                Document document = new Document();
                document.setPageSize(PageSize.A4.rotate());

                try {
                    FileOutputStream fo = new FileOutputStream(new File(pdfname));
                    PdfWriter writer = PdfWriter.getInstance(document, fo);

                    document.open();

                    PdfContentByte canvas = writer.getDirectContentUnder();
                    ClassPathResource bgResource = new ClassPathResource("certif2.jpg");
                    byte[] bgBytes = bgResource.getInputStream().readAllBytes();
                    Image image = Image.getInstance(bgBytes);
                    image.scaleAbsolute(PageSize.A4.rotate()); image.setAbsolutePosition(0, 0);
                    canvas.addImage(image);


                    float pos=(document.getPageSize().getWidth()/2)-(studentName.length()*18/2);
                    FixText(studentName,"savoyeplain.ttf", "Savoye", pos,240, writer, 60);


                    certificate_footer(writer, studentName,periode,nom_formation,month);

                    FixText(date,"poppins.regular.ttf", "Poppins",280,100, writer, 13);

                    certificat.setVerificationCode(java.util.UUID.randomUUID().toString());
                    String str = siteBaseUrl + "/verify/" + certificat.getVerificationCode();
                    BarcodeQRCode my_code = new BarcodeQRCode(str, 100, 100, null);
                    //Step-6: Get Image corresponding to the input string
                    Image qr_image = my_code.getImage();
                    qr_image.setAbsolutePosition(70, 60);
                    //Step-7: Stamp the QR image into the PDF document
                    document.add(qr_image);
                    document.close();
                    writer.close();
                    fo.close();
                    System.out.println("Certificate PDF created for: " + studentName);

                    // SAVE TO DATABASE
                    Certificat certificat = new Certificat();
                    certificat.setDate(LocalDateTime.now());
                    certificat.setPeriode(periode);
                    certificat.setMonth(month);
                    certificat.setPath(relativePath);
                    
                    // Find user by ID (preferred) or fall back to name match
                    User matchedUser = null;
                    if (studentUserId != null) {
                        matchedUser = userRepository.findById(studentUserId).orElse(null);
                    }
                    if (matchedUser == null) {
                        List<User> matchingUsers = userRepository.findByFirstNameAndLastName(studentName);
                        if (!matchingUsers.isEmpty()) matchedUser = matchingUsers.get(0);
                    }
                    System.out.println("User lookup for '" + studentName + "' (id=" + studentUserId + "): " + (matchedUser != null ? "found id=" + matchedUser.getId() : "NOT FOUND"));

                    if (matchedUser != null) {
                        certificat.setUser(matchedUser);
                    }
                    certificatRepository.save(certificat);
                    System.out.println("Certificate saved" + (matchedUser != null ? " for user id=" + matchedUser.getId() : " WITHOUT user"));
                    successCount++;

                } catch (Exception e) {
                    System.err.println("Error generating certificate for: " + studentName);
                    System.err.println("Error details: " + e.getMessage());
                    e.printStackTrace();
                    errorMessages.append("Error for ").append(studentName).append(": ").append(e.getMessage()).append("; ");
                    failureCount++;
                }

            }

            System.out.println("Certificate generation completed for formation: " + nom_formation);
            String resultMessage = "Certificates generated successfully! Success: " + successCount + ", Failed: " + failureCount;
            if (failureCount > 0) {
                resultMessage += " | Errors: " + errorMessages.toString();
            }
            return ResponseEntity.ok(resultMessage);

        } catch (Exception e) {
            System.err.println("Error in genererCertificat");
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error generating certificates: " + e.getMessage());
        }


    }

    @PostMapping("/Generer/{idgroupe}")
    public void genererCertificatForGroup(@PathVariable Long idgroupe, @RequestParam String month, @RequestParam String periode) throws RuntimeException, IOException, DocumentException {
        try {
            Groups group = groupsRepository.findById(idgroupe).orElseThrow(() -> new RuntimeException("Groupe introuvable"));

            Formation formation = group.getFormation();
            String nom_formation = formation.getNomFormation();

            List<User> users = group.getEtudiants();

            if (users.isEmpty()) {
                throw new RuntimeException("Aucun utilisateur n'est associé au groupe.");
            }

            if (group.isCertificatesGenerated()) {
                throw new RuntimeException("Certificates already generated for this group.");
            }

            for (User user : users) {
                // Vérifier si l'utilisateur a déjà un certificat
                if (user.getCertificats().isEmpty()) {
                    String fullName = user.getFirstName() + " " + user.getLastName();

                    String relativePath = "Certifications/" + nom_formation + " " + month + "/" + "_" + user.getId() + "_" + user.getFirstName() + "_" + user.getLastName() + ".pdf";
                    //String pdfname = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\" + relativePath;
                    String pdfname = filesFolder + "\\" + relativePath;
                    //File f = new File("C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\Certifications\\" + nom_formation + " " + month);
                    File f = new File(filesFolder + "\\Certifications\\" + nom_formation + " " + month);
                    if (f.mkdir()) {
                        System.out.println("Directory has been created successfully");
                    } else {
                        System.out.println("Directory cannot be created");
                    }
                    Document document = new Document();
                    document.setPageSize(PageSize.A4.rotate());

                    Certificat certificat = new Certificat();
                    certificat.setDate(LocalDateTime.now());
                    certificat.setPeriode(periode);
                    certificat.setMonth(month);
                    group.setCertificatesGenerated(true);

                    certificat.setUser(user); // Set the relationship between Certificat and User

                    try {
                    FileOutputStream fo = new FileOutputStream(new File(pdfname));
                    PdfWriter writer = PdfWriter.getInstance(document, fo);

                    document.open();

                    PdfContentByte canvas = writer.getDirectContentUnder();

                    Image image = Image.getInstance("src/main/resources/certif2.jpg");
                    image.scaleAbsolute(PageSize.A4.rotate());
                    image.setAbsolutePosition(0, 0);
                    canvas.addImage(image);

                    float pos = (document.getPageSize().getWidth() / 2) - (fullName.length() * 18 / 2);
                    FixText(fullName, "savoyeplain.ttf", "Savoye", pos, 240, writer, 60);

                    certificate_footer(writer, fullName, periode, nom_formation, month);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    String formattedDate = certificat.getDate().format(formatter);
                    FixText(formattedDate, "poppins.regular.ttf", "Poppins", 280, 100, writer, 13);

                    String str = siteBaseUrl + "/verify/" + certificat.getVerificationCode();
                    BarcodeQRCode my_code = new BarcodeQRCode(str, 100, 100, null);
                    Image qr_image = my_code.getImage();
                    qr_image.setAbsolutePosition(70, 60);
                    document.add(qr_image);

                    document.close();
                    writer.close();
                    fo.close();
                    System.out.println("Done");
                    String certificateLink = "http://localhost:4200/student/profile/" + user.getId();
                    sendEmailWithAttachment(user.getUsername(), pdfname, fullName, certificateLink);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    // Set the relationship between Certificat and User
                    certificat.setPath(relativePath);
                    certificat.setUserOrGroupId(group.getId()); // ou certificat.setUserOrGroupId(group.getId());

                    certificatRepository.save(certificat);
                    System.out.print(nom_formation);
                } else {
                    System.out.println("Certificat already exists for user: " + user.getFirstName() + " " + user.getLastName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /***********user*********/
    @PostMapping("/Generer/{idgroupe}/userone/{iduser}")
    public void genererCertificatForUserOneInGroup(
            @PathVariable Long idgroupe,
            @PathVariable Long iduser,
            @RequestParam String month,
            @RequestParam String periode) throws RuntimeException, IOException, DocumentException {
        try {
            Groups group = groupsRepository.findById(idgroupe)
                    .orElseThrow(() -> new RuntimeException("Groupe introuvable"));
            areAllUsersCertified(group); // Appel pour afficher le compte dans la console

            User user = userRepository.findById(iduser)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
            if (!user.getCertificats().isEmpty()) {
                throw new RuntimeException("Cet utilisateur a déjà une certification.");
            }

            Formation formation = group.getFormation();
            String nom_formation = formation.getNomFormation();

            String fullName = user.getFirstName() + " " + user.getLastName();

            String relativePath = "Certifications/" + nom_formation + " " + month + "/" +
                    "_" + user.getId() + "_" + user.getFirstName() + "_" + user.getLastName() + ".pdf";
            String pdfname = filesFolder + "\\" + relativePath;
            File f = new File(filesFolder + "\\Certifications\\" + nom_formation + " " + month);

            if (f.mkdir()) {
                System.out.println("Directory has been created successfully");
            } else {
                System.out.println("Directory cannot be created");
            }
            Document document = new Document();
            document.setPageSize(PageSize.A4.rotate());

            Certificat certificat = new Certificat();
            certificat.setDate(LocalDateTime.now());
            certificat.setPeriode(periode);
            certificat.setMonth(month);
            certificat.setUserOrGroupId(group.getId()); // ou certificat.setUserOrGroupId(group.getId());

            certificat.setUser(user); // Set the relationship between Certificat and User

            try {
                FileOutputStream fo = new FileOutputStream(new File(pdfname));
                PdfWriter writer = PdfWriter.getInstance(document, fo);

                document.open();

                PdfContentByte canvas = writer.getDirectContentUnder();

                Image image = Image.getInstance("src/main/resources/certif2.jpg");
                image.scaleAbsolute(PageSize.A4.rotate());
                image.setAbsolutePosition(0, 0);
                canvas.addImage(image);

                float pos = (document.getPageSize().getWidth() / 2) - (fullName.length() * 18 / 2);
                FixText(fullName, "savoyeplain.ttf", "Savoye", pos, 240, writer, 60);

                certificate_footer(writer, fullName, periode, nom_formation, month);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                String formattedDate = certificat.getDate().format(formatter);
                FixText(formattedDate, "poppins.regular.ttf", "Poppins", 280, 100, writer, 13);

                String str = "http://localhost:4200/student/profile/" + user.getId(); // Utiliser l'ID de l'utilisateur pour le lien QR
                BarcodeQRCode my_code = new BarcodeQRCode(str, 100, 100, null);
                Image qr_image = my_code.getImage();
                qr_image.setAbsolutePosition(70, 60);
                document.add(qr_image);

                document.close();
                writer.close();
                fo.close();
                System.out.println("Done");
                String certificateLink = "http://localhost:4200/student/profile/" + user.getId();
                sendEmailWithAttachment(user.getUsername(), pdfname, fullName, certificateLink);

            } catch (Exception e) {
                e.printStackTrace();
            }
            // Set the relationship between Certificat and User
            certificat.setPath(relativePath);
            if (areAllUsersCertified(group)) {
                // Réagir en conséquence (par exemple, renvoyer une réponse indiquant que les utilisateurs n'ont pas de certification)
                String responseMessage = "wfew";
                System.out.println(responseMessage);
                // Ici, vous pouvez retourner un code d'erreur HTTP approprié et un message JSON, par exemple
                // Vous devez également adapter cela à la structure de réponse que votre API utilise
            group.setCertificatesGenerated(true);            }

            groupsRepository.save(group);

            certificat.setUserOrGroupId(group.getId()); // ou certificat.setUserOrGroupId(group.getId());

            System.out.print(nom_formation);
            certificatRepository.save(certificat);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public boolean areAllUsersCertified(Groups group) {
        List<User> usersInGroup = group.getEtudiants();
        int certifiedUserCount = 1;

        for (User user : usersInGroup) {
            if (!user.getCertificats().isEmpty()) {
                certifiedUserCount++;
            }
        }

        System.out.println(certifiedUserCount + "/" + usersInGroup.size() + " " + (certifiedUserCount == usersInGroup.size()));

        return certifiedUserCount == usersInGroup.size();
    }





    @PutMapping("/close-group/{groupId}")
    @Transactional
    public ResponseEntity<?> closeGroupAndGenerateCertificates(
            @PathVariable Long groupId,
            @RequestParam String month) {
        try {
            Groups group = groupsRepository.findById(groupId)
                    .orElseThrow(() -> new RuntimeException("Group not found"));

            group.setSessionClosed(true);
            group.setClosedDate(new java.util.Date());

            Formation formation = group.getFormation();
            if (formation == null) {
                return ResponseEntity.badRequest().body("Group has no formation assigned");
            }
            String nom_formation = formation.getNomFormation();
            String periode = group.getPeriod() != null ? group.getPeriod() : "1 month";

            List<User> allStudents = group.getEtudiants();
            Map<Long, Boolean> presenceMap = group.getUserPresenceStatus();

            List<User> eligible = allStudents.stream()
                    .filter(u -> u.getEnabled() == 1
                            && (presenceMap.isEmpty() || Boolean.TRUE.equals(presenceMap.get(u.getId()))))
                    .collect(Collectors.toList());

            int successCount = 0;
            int skippedCount = allStudents.size() - eligible.size();
            List<String> generatedFor = new ArrayList<>();

            if (!eligible.isEmpty() && !group.isCertificatesGenerated()) {
                File certDir = new File(filesFolder + File.separator + "Certifications"
                        + File.separator + nom_formation + " " + month);
                certDir.mkdirs();

                for (User user : eligible) {
                    if (user.getCertificats() != null && !user.getCertificats().isEmpty()) {
                        skippedCount++;
                        continue;
                    }
                    String fullName = user.getFirstName() + " " + user.getLastName();
                    String relativePath = "Certifications/" + nom_formation + " " + month
                            + "/_" + user.getId() + "_" + user.getFirstName() + "_" + user.getLastName() + ".pdf";
                    String pdfname = filesFolder + File.separator
                            + relativePath.replace("/", File.separator);

                    try {
                        Document document = new Document();
                        document.setPageSize(PageSize.A4.rotate());
                        FileOutputStream fo = new FileOutputStream(new File(pdfname));
                        PdfWriter writer = PdfWriter.getInstance(document, fo);
                        document.open();

                        PdfContentByte canvas = writer.getDirectContentUnder();
                        ClassPathResource bgResource = new ClassPathResource("certif2.jpg");
                        byte[] bgBytes = bgResource.getInputStream().readAllBytes();
                        Image bgImage = Image.getInstance(bgBytes);
                        bgImage.scaleAbsolute(PageSize.A4.rotate());
                        bgImage.setAbsolutePosition(0, 0);
                        canvas.addImage(bgImage);

                        float pos = (document.getPageSize().getWidth() / 2) - (fullName.length() * 18 / 2);
                        FixText(fullName, "savoyeplain.ttf", "Savoye", pos, 240, writer, 60);
                        certificate_footer(writer, fullName, periode, nom_formation, month);

                        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        FixText(LocalDateTime.now().format(fmt), "poppins.regular.ttf", "Poppins", 280, 100, writer, 13);

                        cert.setVerificationCode(java.util.UUID.randomUUID().toString());
                        String qrUrl = siteBaseUrl + "/verify/" + cert.getVerificationCode();
                        BarcodeQRCode qrCode = new BarcodeQRCode(qrUrl, 100, 100, null);
                        Image qrImage = qrCode.getImage();
                        qrImage.setAbsolutePosition(70, 60);
                        document.add(qrImage);

                        document.close();
                        writer.close();
                        fo.close();

                        Certificat cert = new Certificat();
                        cert.setDate(LocalDateTime.now());
                        cert.setPeriode(periode);
                        cert.setMonth(month);
                        cert.setPath(relativePath);
                        cert.setUser(user);
                        cert.setUserOrGroupId(groupId);
                        certificatRepository.save(cert);

                        try {
                            sendEmailWithAttachment(user.getUsername(), pdfname, fullName,
                                    siteBaseUrl + "/student/getcertifcates");
                        } catch (Exception emailEx) {
                            System.err.println("Email failed for " + user.getUsername() + ": " + emailEx.getMessage());
                        }

                        generatedFor.add(fullName);
                        successCount++;
                    } catch (Exception e) {
                        System.err.println("Error generating cert for " + fullName + ": " + e.getMessage());
                    }
                }

                if (successCount > 0) {
                    group.setCertificatesGenerated(true);
                }
            }

            groupsRepository.save(group);

            Map<String, Object> result = new java.util.HashMap<>();
            result.put("groupClosed", true);
            result.put("totalStudents", allStudents.size());
            result.put("eligibleStudents", eligible.size());
            result.put("certificatesGenerated", successCount);
            result.put("ineligibleStudents", skippedCount);
            result.put("generatedFor", generatedFor);
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error closing group: " + e.getMessage());
        }
    }

    private void sendEmailWithAttachment(String toEmail, String attachmentPath, String fullName, String certificateLink) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        try {
            helper.setTo(toEmail);
            helper.setSubject("Your Certificate");

            // Set the email body
            String emailBody = "Hello " + fullName + ",\n\n" +
                    "Congratulations! Your certificate has been generated.\n" +
                    "You can download your certificate using the link below:\n\n" +
                    certificateLink + "\n\n" +
                    "Best regards,\n" +
                    "Your Certificate Team";

            helper.setText(emailBody);

            // Attach the certificate PDF
            FileSystemResource file = new FileSystemResource(new File(attachmentPath));
            helper.addAttachment("Certificate.pdf", file);

            javaMailSender.send(message);
            System.out.println("Email sent with certificate to: " + toEmail);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private static void FixText (String text, String fontfile, String fontname,float x, int y, PdfWriter writer,
                                 int size) throws DocumentException, IOException {
        PdfContentByte cb = writer.getDirectContent();

        try {
            // Use ClassPathResource to load fonts from resources folder
            ClassPathResource fontResource = new ClassPathResource("fonts/" + fontfile);
            String fontPath = fontResource.getFile().getAbsolutePath();
            
            FontFactory.register(fontPath);
            Font textFont = FontFactory.getFont(fontname, BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED, 10);
            BaseFont bf = textFont.getBaseFont();

            cb.saveState();
            cb.beginText();
            cb.moveText(x, y);
            cb.setFontAndSize(bf, size);
            cb.showText(text);
            cb.endText();
            cb.restoreState();
        } catch (Exception e) {
            System.err.println("Error loading font: " + fontfile);
            e.printStackTrace();
            // Fallback to default font
            cb.saveState();
            cb.beginText();
            cb.moveText(x, y);
            cb.setFontAndSize(BaseFont.createFont(), size);
            cb.showText(text);
            cb.endText();
            cb.restoreState();
        }
    }
    private static void certificate_footer(PdfWriter writer, String name, String periode, String formation, String month ) throws DocumentException, IOException {

        PdfContentByte cb = writer.getDirectContent();

        try {
            // Load fonts using ClassPathResource
            ClassPathResource poppinsThinResource = new ClassPathResource("fonts/Poppins-Thin.ttf");
            ClassPathResource poppinsRegularResource = new ClassPathResource("fonts/poppins.regular.ttf");
            
            String poppinsThinPath = poppinsThinResource.getFile().getAbsolutePath();
            String poppinsRegularPath = poppinsRegularResource.getFile().getAbsolutePath();
            
            FontFactory.register(poppinsThinPath);
            Font textFont = FontFactory.getFont("Poppins Thin", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED, 10);
            BaseFont bf = textFont.getBaseFont();
            
            FontFactory.register(poppinsRegularPath);
            Font textFont2 = FontFactory.getFont("Poppins", BaseFont.IDENTITY_H,
                    BaseFont.EMBEDDED, 10);
            BaseFont bf2 = textFont2.getBaseFont();

            cb.saveState();
            cb.beginText();
            cb.moveText(180, 185);
            cb.setFontAndSize(bf, 14);
            String begin = "This is to certify that";
            cb.showText(begin);
            cb.endText();

            cb.beginText();
            cb.setFontAndSize(bf2, 14);
            float pos_name = 180+cb.getEffectiveStringWidth(begin, false);
            cb.moveText(pos_name, 185);
            cb.showText(name);
            cb.endText();

            cb.beginText();
            cb.setFontAndSize(bf, 14);
            float pos_text2 = pos_name+cb.getEffectiveStringWidth(name, false);
            cb.moveText(pos_text2 + 10, 185);
            String next ="successfully completed ";
            cb.showText(next);
            cb.endText();


            cb.beginText();
            cb.setFontAndSize(bf2, 14);
            float pos_mot2 = pos_text2 +cb.getEffectiveStringWidth(next, false);
            cb.moveText(pos_mot2 + 7, 185);
            cb.showText(periode);
            cb.endText();

            cb.beginText();
            cb.moveText(180,165);
            cb.setFontAndSize(bf, 14);
            String of = "of";
            cb.showText(of);
        cb.endText();

        cb.beginText();
        cb.setFontAndSize(bf2, 14);
        float pos_formation = 180+cb.getEffectiveStringWidth(of, false);
        cb.moveText(pos_formation + 7, 165);
        cb.showText(formation);
        cb.endText();

        cb.beginText();
        cb.setFontAndSize(bf, 14);
        float pos_text3 = pos_formation+cb.getEffectiveStringWidth(formation, false);
        cb.moveText(pos_text3 + 20, 165);
        String next2 = "training and coaching on";
        cb.showText(next2);
        cb.endText();

        cb.beginText();
        cb.setFontAndSize(bf2, 14);
        float pos_month = pos_text3+cb.getEffectiveStringWidth(next2, false);
        cb.moveText(pos_month + 20, 165);
        cb.showText(month);
        cb.endText();

        cb.restoreState();
        } catch (Exception e) {
            System.err.println("Error in certificate_footer: " + e.getMessage());
            e.printStackTrace();
        }
    }






    private void deleteCertificatFiles(String pathToDelete) {
        try {
            Path directoryPath = Paths.get(pathToDelete);
            Files.walk(directoryPath)
                    .sorted((p1, p2) -> -p1.compareTo(p2)) // Delete files in reverse order (deepest first)
                    .forEach(p -> {
                        try {
                            Files.delete(p);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /***hedhi**/
    @DeleteMapping("/Supprimer/{idgroupe}")
    public ResponseEntity<String> supprimerCertificatsForGroup(@PathVariable Long idgroupe) {
        try {
            Groups group = groupsRepository.findById(idgroupe).orElseThrow(() -> new RuntimeException("Groupe introuvable"));
            Formation formation = group.getFormation();
            String nom_formation = formation.getNomFormation();
            List<User> users = group.getEtudiants(); // Assuming "getEtudiants()" returns the list of users associated with the group
group.setCertificatesGenerated(false);
            if (users.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            for (User user : users) {
                List<Certificat> certificats = certificatRepository.findByUser(user); // Assuming "findByUser" is the correct method in your repository
                for (Certificat certificat : certificats) {
                    String pathToDelete = filesFolder + "\\Certifications\\" + nom_formation + " " + certificat.getMonth();

                    deleteCertificatFiles(pathToDelete);
                    certificatRepository.delete(certificat);
                }
            }

            return ResponseEntity.ok("Certificats deleted successfully for the group: " + idgroupe);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting certificats.");
        }
    }
    /**** hedhi ****/
    @PutMapping("/ModifierCertificats/{idgroupe}")
    public ResponseEntity<String> modifierCertificatsForGroup(@PathVariable Long idgroupe, @RequestParam String periode, @RequestParam String month) {
        try {
            Groups group = groupsRepository.findById(idgroupe).orElseThrow(() -> new RuntimeException("Groupe introuvable"));

            List<User> users = group.getEtudiants();

            if (users.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            for (User user : users) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                Formation formation = group.getFormation();
                String nom_formation = formation.getNomFormation();

                String relativePath = "Certifications/" + nom_formation + " " + month + "/" + "_" + user.getId() + "_" + user.getFirstName() + "_" + user.getLastName() + ".pdf";
                String pdfname = filesFolder + "\\" + relativePath;
                File pdfFile = new File(pdfname);

                List<Certificat> certificats = certificatRepository.findByUser(user);
                for (Certificat certificat : certificats) {
                    String oldPath = certificat.getPath();
                    String newPath = oldPath.replace(certificat.getMonth().trim(), month.trim());

                    // Update periode and month in the certificate
                    certificat.setPeriode(periode);

                    // Check if the new month is different from the existing month
                    if (!month.equals(certificat.getMonth())) {
                        certificat.setMonth(month);

                        // Update certificate path if month changed
                        certificat.setPath(newPath);

                        // Delete the old folder if it exists
                        File oldFolder = new File(filesFolder + "\\" + oldPath).getParentFile();
                        if (oldFolder.exists() && oldFolder.isDirectory()) {
                            File[] files = oldFolder.listFiles();
                            if (files != null) {
                                for (File file : files) {
                                    file.delete();
                                }
                            }
                            oldFolder.delete();
                        }

                        // Create the new folder
                        pdfFile.getParentFile().mkdirs();
                    }

                    Document document = new Document();
                    document.setPageSize(PageSize.A4.rotate());
                    // Save the modified certificate


                    try {
                        FileOutputStream fo = new FileOutputStream(new File(pdfname));
                        PdfWriter writer = PdfWriter.getInstance(document, fo);

                        document.open();

                        PdfContentByte canvas = writer.getDirectContentUnder();

                        Image image = Image.getInstance("src/main/resources/certif2.jpg");
                        image.scaleAbsolute(PageSize.A4.rotate());
                        image.setAbsolutePosition(0, 0);
                        canvas.addImage(image);

                        float pos = (document.getPageSize().getWidth() / 2) - (fullName.length() * 18 / 2);
                        FixText(fullName, "savoyeplain.ttf", "Savoye", pos, 240, writer, 60);

                        certificate_footer(writer, fullName, periode, nom_formation, month);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        String formattedDate = certificat.getDate().format(formatter);
                        FixText(formattedDate, "poppins.regular.ttf", "Poppins", 280, 100, writer, 13);

                        String str = "http://localhost:4200/student/profile/" + user.getId(); // Utiliser l'ID de l'utilisateur pour le lien QR
                        BarcodeQRCode my_code = new BarcodeQRCode(str, 100, 100, null);
                        Image qr_image = my_code.getImage();
                        qr_image.setAbsolutePosition(70, 60);
                        document.add(qr_image);

                        document.close();
                        writer.close();
                        fo.close();
                        System.out.println("Done");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    certificat.setPath(relativePath);
                    Certificat savedCertificat = certificatRepository.save(certificat);
                    System.out.println("Updated certificat path: " + savedCertificat.getPath());
                }
            }

            return ResponseEntity.ok("Certificats updated successfully for the group: " + idgroupe);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating certificats.");
        }
    }
    @PutMapping("/Modifier/{idgroupe}/userone/{iduser}")
    public ResponseEntity<String> modifierCertificatForUserInGroup(
            @PathVariable Long idgroupe,
            @PathVariable Long iduser,
            @RequestParam String month,
            @RequestParam String periode) {
        try {
            Groups group = groupsRepository.findById(idgroupe)
                    .orElseThrow(() -> new RuntimeException("Groupe introuvable"));

            User user = userRepository.findById(iduser)
                    .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            List<Certificat> certificats = certificatRepository.findByUser(user);

            if (certificats.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            for (Certificat certificat : certificats) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                Formation formation = group.getFormation();
                String nom_formation = formation.getNomFormation();

                String relativePath = "Certifications/" + nom_formation + " " + month + "/" +
                        "_" + user.getId() + "_" + user.getFirstName() + "_" + user.getLastName() + ".pdf";
                String pdfname = filesFolder + "\\" + relativePath;
                File pdfFile = new File(pdfname);

                // Update periode and month in the certificate
                certificat.setPeriode(periode);

                // Check if the new month is different from the existing month
                if (!month.equals(certificat.getMonth())) {
                    String oldPath = certificat.getPath();
                    String newPath = oldPath.replace(certificat.getMonth().trim(), month.trim());

                    certificat.setMonth(month);

                    // Update certificate path if month changed
                    certificat.setPath(newPath);

                    // Delete the old file if it exists
                    File oldFile = new File(filesFolder + "\\" + oldPath);
                    if (oldFile.exists() && oldFile.isFile()) {
                        oldFile.delete();
                    }

                    // Create the new folder
                    pdfFile.getParentFile().mkdirs();
                }

                Document document = new Document();
                document.setPageSize(PageSize.A4.rotate());
                // Save the modified certificate

                try {
                    FileOutputStream fo = new FileOutputStream(new File(pdfname));
                    PdfWriter writer = PdfWriter.getInstance(document, fo);

                    document.open();

                    PdfContentByte canvas = writer.getDirectContentUnder();

                    Image image = Image.getInstance("src/main/resources/certif2.jpg");
                    image.scaleAbsolute(PageSize.A4.rotate());
                    image.setAbsolutePosition(0, 0);
                    canvas.addImage(image);

                    float pos = (document.getPageSize().getWidth() / 2) - (fullName.length() * 18 / 2);
                    FixText(fullName, "savoyeplain.ttf", "Savoye", pos, 240, writer, 60);

                    certificate_footer(writer, fullName, periode, nom_formation, month);

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                    String formattedDate = certificat.getDate().format(formatter);
                    FixText(formattedDate, "poppins.regular.ttf", "Poppins", 280, 100, writer, 13);

                    String str = siteBaseUrl + "/verify/" + certificat.getVerificationCode();
                    BarcodeQRCode my_code = new BarcodeQRCode(str, 100, 100, null);
                    Image qr_image = my_code.getImage();
                    qr_image.setAbsolutePosition(70, 60);
                    document.add(qr_image);

                    document.close();    writer.close();
                    fo.close();
                    System.out.println("Done");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                certificatRepository.save(certificat);
                System.out.println("Updated certificat path: " + certificat.getPath());
            }

            return ResponseEntity.ok("Certificat updated successfully for the user: " + iduser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating the certificat.");
        }
    }
/****** periode tetmodifa ***/
    @PutMapping("/ModifierCertificatsUpdatePeriode/{idgroupe}")
    public ResponseEntity<String> modifierCertificatsForGroupUpdatePeriode(@PathVariable Long idgroupe, @RequestParam String newPeriode, @RequestParam String newMonth) {
        try {
            Groups group = groupsRepository.findById(idgroupe).orElseThrow(() -> new RuntimeException("Groupe introuvable"));

            List<User> users = group.getEtudiants();

            if (users.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            for (User user : users) {
                String fullName = user.getFirstName() + " " + user.getLastName();
                Formation formation = group.getFormation();
                String nom_formation = formation.getNomFormation();

                String relativePath = "Certifications/" + nom_formation + " " + newMonth + "/" +"_"+user.getId() +"_"+ user.getFirstName() +"_"+ user.getLastName() + ".pdf";
                String pdfname = filesFolder + "\\" + relativePath;
                File f = new File(filesFolder + "\\Certifications\\" + nom_formation + " " + newMonth);
             // Creates all directories if they don't exist

                if (f.mkdir()) {
                    System.out.println("Directory has been created successfully");
                } else {
                    System.out.println("Directory cannot be created");
                }
                Document document = new Document();
                document.setPageSize(PageSize.A4.rotate());
                List<Certificat> certificats = certificatRepository.findByUser(user);
                for (Certificat certificat : certificats) {
                    String oldPath = certificat.getPath();
                    String newPath = oldPath.replace(certificat.getMonth().trim(), newMonth.trim());

                    // Update periode and month in the certificate
                    certificat.setPeriode(newPeriode);
                    certificat.setMonth(newMonth);

                    // Update certificate path if month changed
                    if (!newMonth.equals(certificat.getMonth())) {
                        certificat.setPath(newPath);
                    }

                    // Save the modified certificate


                    try {
                        FileOutputStream fo = new FileOutputStream(new File(pdfname));
                        PdfWriter writer = PdfWriter.getInstance(document, fo);

                        document.open();

                        PdfContentByte canvas = writer.getDirectContentUnder();

                        Image image = Image.getInstance("src/main/resources/certif2.jpg");
                        image.scaleAbsolute(PageSize.A4.rotate());
                        image.setAbsolutePosition(0, 0);
                        canvas.addImage(image);

                        float pos = (document.getPageSize().getWidth() / 2) - (fullName.length() * 18 / 2);
                        FixText(fullName, "savoyeplain.ttf", "Savoye", pos, 240, writer, 60);

                        certificate_footer(writer, fullName, newPeriode, nom_formation, newMonth);

                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                        String formattedDate = certificat.getDate().format(formatter);
                        FixText(formattedDate, "poppins.regular.ttf", "Poppins", 280, 100, writer, 13);

                        String str = "http://localhost:4200/student/profile/" + user.getId(); // Utiliser l'ID de l'utilisateur pour le lien QR
                        BarcodeQRCode my_code = new BarcodeQRCode(str, 100, 100, null);
                        Image qr_image = my_code.getImage();
                        qr_image.setAbsolutePosition(70, 60);
                        document.add(qr_image);

                        document.close();
                        writer.close();
                        fo.close();
                        System.out.println("Done");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    certificat.setPath(relativePath);
                    Certificat savedCertificat = certificatRepository.save(certificat);
                    System.out.println("Updated certificat path: " + savedCertificat.getPath());
                }
            }

            return ResponseEntity.ok("Certificats updated successfully for the group: " + idgroupe);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while updating certificats.");
        }
    }
    @GetMapping("/all")
    public ResponseEntity<List<Map<String, Object>>> getAllCertificates() {
        List<Certificat> certs = certificatRepository.findAll();
        List<Map<String, Object>> result = new ArrayList<>();
        for (Certificat c : certs) {
            Map<String, Object> dto = new java.util.LinkedHashMap<>();
            dto.put("id", c.getIdCertificat());
            dto.put("period", c.getPeriode());
            dto.put("month", c.getMonth());
            dto.put("date", c.getDate() != null ? c.getDate().toString() : null);
            dto.put("path", c.getPath());
            // Extract formation from path: "Certifications/FormationName Month number/..."
            String formation = "";
            if (c.getPath() != null) {
                String[] parts = c.getPath().split("/");
                if (parts.length >= 2) {
                    String folderName = parts[1];
                    // Remove trailing " Month number" pattern
                    formation = folderName.replaceAll("\\s+\\w+\\s+\\d+$", "").replaceAll("\\s+\\w+$", "").trim();
                    if (formation.isEmpty()) formation = folderName;
                }
            }
            dto.put("formation", formation);
            if (c.getUser() != null) {
                dto.put("studentId", c.getUser().getId());
                dto.put("studentFirstName", c.getUser().getFirstName());
                dto.put("studentLastName", c.getUser().getLastName());
                dto.put("studentEmail", c.getUser().getUsername());
            } else {
                dto.put("studentId", null);
                dto.put("studentFirstName", "Unknown");
                dto.put("studentLastName", "");
                dto.put("studentEmail", "");
            }
            result.add(dto);
        }
        return ResponseEntity.ok(result);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAndRegenerateCertificate(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        try {
            Certificat cert = certificatRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Certificate not found"));

            String name = body.getOrDefault("name",
                    cert.getUser() != null ? cert.getUser().getFirstName() + " " + cert.getUser().getLastName() : "Unknown");
            String period = body.getOrDefault("period", cert.getPeriode());
            String formation = body.getOrDefault("formation", "");
            String month = body.getOrDefault("month", cert.getMonth());
            String date = body.getOrDefault("date", cert.getDate() != null ? cert.getDate().toString() : "");

            // Regenerate PDF in memory and return as download
            Document document = new Document();
            document.setPageSize(PageSize.A4.rotate());
            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, baos);
            document.open();

            PdfContentByte canvas = writer.getDirectContentUnder();
            ClassPathResource bgResource = new ClassPathResource("certif2.jpg");
            byte[] bgBytes = bgResource.getInputStream().readAllBytes();
            Image image = Image.getInstance(bgBytes);
            image.scaleAbsolute(PageSize.A4.rotate());
            image.setAbsolutePosition(0, 0);
            canvas.addImage(image);

            float pos = (document.getPageSize().getWidth() / 2) - (name.length() * 18 / 2);
            FixText(name, "savoyeplain.ttf", "Savoye", pos, 240, writer, 60);
            certificate_footer(writer, name, period, formation, month);
            FixText(date, "poppins.regular.ttf", "Poppins", 280, 100, writer, 13);

            String verifyCode = cert.getVerificationCode() != null ? cert.getVerificationCode() : java.util.UUID.randomUUID().toString();
            BarcodeQRCode qrCode = new BarcodeQRCode(siteBaseUrl + "/verify/" + verifyCode, 100, 100, null);
            Image qrImage = qrCode.getImage();
            qrImage.setAbsolutePosition(70, 60);
            document.add(qrImage);
            document.close();
            writer.close();

            // Update DB record
            cert.setPeriode(period);
            cert.setMonth(month);
            certificatRepository.save(cert);

            byte[] pdfBytes = baos.toByteArray();
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"Certificate_" + name.replace(" ", "_") + ".pdf\"")
                    .header("Content-Type", "application/pdf")
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error regenerating certificate: " + e.getMessage());
        }
    }

    @GetMapping("/group-status/{groupId}")
    public ResponseEntity<Map<String, Object>> getGroupStatus(@PathVariable Long groupId) {
        Groups group = groupsRepository.findById(groupId).orElse(null);
        if (group == null) return ResponseEntity.notFound().build();
        Map<String, Object> status = new java.util.HashMap<>();
        status.put("sessionClosed", group.isSessionClosed());
        status.put("closedDate", group.getClosedDate());
        status.put("certificatesGenerated", group.isCertificatesGenerated());
        return ResponseEntity.ok(status);
    }

    @GetMapping("/check-generated/{groupId}")
    public ResponseEntity<Boolean> checkCertificatesGenerated(@PathVariable Long groupId) {
        boolean certificatesGenerated = certificateService.checkIfCertificatesGenerated(groupId);
        System.out.println("Certificates generated for group " + groupId + ": " + certificatesGenerated);
        return ResponseEntity.ok(certificatesGenerated);

    }
    @GetMapping("/values/{idgroupe}")
    public ResponseEntity<List<Certificat>> getCertificateValuesByGroupId(@PathVariable Long idgroupe) {
        List<Certificat> certificatValues = certificateService.getCertificatByGroupId(idgroupe);
        if (!certificatValues.isEmpty()) {
            return ResponseEntity.ok(certificatValues);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/valueswe/{idgroupe}")
    public ResponseEntity<Certificat> getCertificateValuesByGroupIdwe(@PathVariable Long idgroupe) {
        List<Certificat> certificatValues = certificateService.getCertificatByGroupId(idgroupe);

        if (!certificatValues.isEmpty()) {
            // Assuming you have a method to extract periode and month from the Certificat object
            String periode = certificatValues.get(0).getPeriode();
            String month = certificatValues.get(0).getMonth();

            Certificat certificatDTO = new Certificat(periode, month);

            return ResponseEntity.ok(certificatDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/UserCertificates/{userId}")
    public ResponseEntity<List<Certificat>> getUserCertificates(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Certificat> userCertificates = certificatRepository.findByUser(user);
            System.out.println("Certificates for user " + userId + ": " + userCertificates.size());
            return ResponseEntity.ok(userCertificates);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/UserCertificatesFormation/{userId}")
    public ResponseEntity<List<String>> getUserCertificatesFormationNames(@PathVariable Long userId) {
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Certificat> userCertificates = user.getCertificats();
            Set<String> formationNames = new HashSet<>();

            for (Certificat certificat : userCertificates) {
                String path = certificat.getPath();
                String formationName = extractFormationNameFromPath(path);
                formationNames.add(formationName);
            }

            return ResponseEntity.ok(new ArrayList<>(formationNames));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String extractFormationNameFromPath(String path) {
        int startIndex = path.indexOf("Certifications/") + "Certifications/".length();
        int endIndex = path.indexOf("/", startIndex);
        if (endIndex != -1) {
            return path.substring(startIndex, endIndex);
        }
        return "";
    }

    @GetMapping("/ShareCertificateOnLinkedIn/{certificatId}")
    public ResponseEntity<String> shareCertificateOnLinkedIn(@PathVariable Long certificatId) {
        try {
            Certificat certificat = certificatRepository.findById(certificatId)
                    .orElseThrow(() -> new RuntimeException("Certificat not found"));

            String shareUrl = "https://www.linkedin.com/sharing/share-offsite/?url="
                    + URLEncoder.encode(certificat.getPath(), "UTF-8");

            return ResponseEntity.ok(shareUrl);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/Supprimer/{idgroupe}/user/{iduser}")
    public ResponseEntity<String> supprimerUserFromGroup(@PathVariable Long idgroupe, @PathVariable Long iduser) {
        try {
            Groups group = groupsRepository.findById(idgroupe).orElseThrow(() -> new RuntimeException("Groupe introuvable"));
            User user = userRepository.findById(iduser).orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

            List<Certificat> certificats = certificatRepository.findByUser(user);
            for (Certificat certificat : certificats) {
                String pdfPathToDelete = filesFolder + "\\" + certificat.getPath();
                deleteCertificatFile(pdfPathToDelete); // Supprime le fichier PDF

                certificatRepository.delete(certificat);
            }
            group.setCertificatesGenerated(false);

            group.getEtudiants().remove(user);
            groupsRepository.save(group);

            return ResponseEntity.ok("User and associated certificats deleted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while deleting the user and certificats.");
        }
    }

    private void deleteCertificatFile(String pdfFilePath) {
        File pdfFile = new File(pdfFilePath);
        if (pdfFile.exists() && pdfFile.isFile()) {
            pdfFile.delete();
        }
    }

    /** Public endpoint — no auth required — used by QR code scan */
    @GetMapping("/verify/{code}")
    public ResponseEntity<Map<String, Object>> verifyCertificate(@PathVariable String code) {
        return certificatRepository.findByVerificationCode(code)
            .map(c -> {
                Map<String, Object> info = new java.util.LinkedHashMap<>();
                info.put("valid", true);
                info.put("studentName", c.getUser() != null
                    ? c.getUser().getFirstName() + " " + c.getUser().getLastName() : "—");
                info.put("formation", extractFormationNameFromPath(c.getPath() != null ? c.getPath() : ""));
                info.put("period", c.getPeriode());
                info.put("month", c.getMonth());
                info.put("issuedAt", c.getDate() != null ? c.getDate().toString() : null);
                return ResponseEntity.ok(info);
            })
            .orElseGet(() -> {
                Map<String, Object> info = new java.util.LinkedHashMap<>();
                info.put("valid", false);
                return ResponseEntity.ok(info);
            });
    }

}
