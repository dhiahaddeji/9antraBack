package com.esprit.springjwt.service;


import com.esprit.springjwt.entity.Hackerspaces;
import com.esprit.springjwt.entity.Progress;
import com.esprit.springjwt.repository.HackerspacesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
public class HackerspacesService {
    @Autowired
    private HackerspacesRepository hackerspacesRepository;
    @Autowired
    private NotificationService notificationService;
    @Value("${files.folder}")
    String filesFolder;


    public Hackerspaces addHackerspaces(
            String Region,
            String Location,
            Integer Phone,
            String Email,
            String Description,
            String Adresse,
            MultipartFile photo
    ) throws IOException {
        String newFilename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        Path uploadDir = Paths.get(filesFolder, "Documents");
        Files.createDirectories(uploadDir);
        photo.transferTo(uploadDir.resolve(newFilename));

        String attributeName = Region.replaceAll("\\s+", "");
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

        String newFilename = System.currentTimeMillis() + "_" + photo.getOriginalFilename();
        Path uploadDir = Paths.get(filesFolder, "Documents");
        Files.createDirectories(uploadDir);
        photo.transferTo(uploadDir.resolve(newFilename));

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
