package com.esprit.springjwt.service;


import com.esprit.springjwt.entity.Hackerspaces;
import com.esprit.springjwt.entity.Progress;
import com.esprit.springjwt.repository.HackerspacesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class HackerspacesService {
    @Autowired
    private HackerspacesRepository hackerspacesRepository;
    @Autowired
    private NotificationService notificationService;
    //add hackerspace with upload it photo
   // public static String UPLOAD_DOCUMENTS = "C:\\Users\\Wale\\Desktop\\Final Design\\bridge\\src\\assets\\Documents\\";

    //adding global variable
    @Value("${files.folder}")
    String filesFolder;
    public static String UPLOAD_DOCUMENTS = "C:\\Users\\DELL\\Desktop\\The Bridge Front\\9antraFormationFrant\\src\\assets\\Documents\\";


    public Hackerspaces addHackerspaces(
            String Region,
            String Location,
            Integer Phone,
            String Email,
            String Description,
            String Adresse,
            MultipartFile photo
    ) throws IOException {
        String currentDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
        String filesName = currentDate + photo.getOriginalFilename();

        byte[] bytes1 = filesName.getBytes();
        //Path path1 = Paths.get(UPLOAD_DOCUMENTS + filesName);
        Path path1 = Paths.get(filesFolder + "\\Documents\\" + filesName);
        Files.write(path1, bytes1);
        // Generate a timestamp for the image filename
        String timestamp = Long.toString(System.currentTimeMillis());

        // Set the destination path to save the image
        String destinationPath = "C:\\Users\\Wale\\Desktop\\zied\\9antraFormationFrant-wale\\src\\assets\\Documents\\";

        // Create a new filename using the timestamp and original filename
        String newFilename = timestamp + "_" + photo.getOriginalFilename();

        // Save the file to the disk
        //photo.transferTo(new File(UPLOAD_DOCUMENTS + newFilename));
        photo.transferTo(new File(filesFolder + "\\Documents\\" + newFilename));
        String attributeName = Region.replaceAll("\\s+", "");
        ;

        Hackerspaces hackerspaces = new Hackerspaces();
        hackerspaces.setRegion(attributeName);
        hackerspaces.setLocation(Location);
        hackerspaces.setPhone(Phone);
        hackerspaces.setEmail(Email);
        hackerspaces.setAdresse(Adresse);
        hackerspaces.setDescription(Description);
        hackerspaces.setPhoto(newFilename);
        notificationService.sendNotifToAllUsers("Exciting Announcement: New hackerspace released! check it now", "/hackerspace/"+attributeName, "New hackerspace");
        return hackerspacesRepository.save(hackerspaces);

    }


    public List<Hackerspaces> getAllHackerspaces() {
        return hackerspacesRepository.findAll();
    }

    public Hackerspaces updateHackerspaces(Long id,
    		 String Region,
             String Location,
             Integer Phone,
             String Email,
             String Description,
             String Adresse,
             MultipartFile photo) throws IOException {
    	
    	 String currentDate = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
         String filesName = currentDate + photo.getOriginalFilename();

         byte[] bytes1 = filesName.getBytes();
         //Path path1 = Paths.get(UPLOAD_DOCUMENTS + filesName);
         Path path1 = Paths.get(filesFolder + "\\Documents\\" + filesName);
         Files.write(path1, bytes1);
         // Generate a timestamp for the image filename
         String timestamp = Long.toString(System.currentTimeMillis());

         // Create a new filename using the timestamp and original filename
         String newFilename = timestamp + "_" + photo.getOriginalFilename();

         // Save the file to the disk
         //photo.transferTo(new File(UPLOAD_DOCUMENTS + newFilename));
         photo.transferTo(new File(filesFolder + "\\Documents\\" + newFilename));
    	
    	Hackerspaces updated = hackerspacesRepository.getReferenceById(id);
    	if(updated !=null) {
    		updated.setAdresse(Adresse);
    		updated.setDescription(Description);
    		updated.setEmail(Email);
    		updated.setLocation(Location);
    		updated.setPhone(Phone);
    		updated.setRegion(Region);
    		updated.setPhoto(newFilename);
    		return hackerspacesRepository.save(updated);
    	}
    	return null;
        
    }

    public Hackerspaces getHackerspacesById(Long id) {
        return hackerspacesRepository.findById(id).get();
    }

    public void deleteHackerspaces(Long id) {
        hackerspacesRepository.deleteById(id);
    }

    public Hackerspaces getHackerspacesByRegion(String region) {
        return hackerspacesRepository.getHackerspacesByRegion(region);
    }

}
