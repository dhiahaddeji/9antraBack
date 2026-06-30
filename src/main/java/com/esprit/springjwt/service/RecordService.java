package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Record;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.RecordRepository;
import com.esprit.springjwt.repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static com.esprit.springjwt.service.HackerspacesService.UPLOAD_DOCUMENTS;

@Service
public class RecordService {
	@Autowired
    private RecordRepository recordRepository;
	@Autowired
    private GroupsRepository groupsRepository;
	@Autowired
	UserRepository userRepository;
    @Value("${files.folder}")
    String filesFolder;
    public Record addRecord(String title, Long groupId, Long idUser, MultipartFile file) throws IOException {

        String timestamp = Long.toString(System.currentTimeMillis());
        
        String newFilename = timestamp + "_" + file.getOriginalFilename();

        // Find the group by ID
        Optional<Groups> groupOptional = groupsRepository.findById(groupId);
        User user = userRepository.getById(idUser);
        if (groupOptional.isPresent()) {
            Groups group = groupOptional.get();

            // Create a folder for the group based on its creation date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String groupFolderName = dateFormat.format(group.getCreationDate());
            String groupFolderPath = filesFolder + "\\Records\\" + groupFolderName;

            // Ensure the folder exists; if not, create it
            File groupFolder = new File(groupFolderPath);
            if (!groupFolder.exists()) {
                groupFolder.mkdirs();
            }

            // Save the file to the group's folder
            File recordFile = new File(groupFolderPath + "\\" + newFilename);
            file.transferTo(recordFile);

            Record record = new Record();
            record.setTitle(title);
            if(user!=null) {
            	record.setUser(user);
            }else {
            	record.setUser(null);
            }
            
            record.setVideoLink(groupFolderName + "/" + newFilename);
            record.setGroups(group);

            return recordRepository.save(record);
        } else {
            // Handle the case where the group with the provided ID is not found
            throw new IllegalArgumentException("Group not found with ID: " + groupId);
        }
    }
    //get records by groups
    public Iterable<Record> getRecordsByGroups(Long groupId) {
        return recordRepository.findByGroups(groupId);
    }



//delete records by id
    public void deleteRecord(Long id) {
        recordRepository.deleteById(id);
    }

}

