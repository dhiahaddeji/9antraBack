package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Record;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.RecordRepository;
import com.esprit.springjwt.repository.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

@Service
public class RecordService {
	private static final Logger logger = LoggerFactory.getLogger(RecordService.class);
	
	@Autowired
    private RecordRepository recordRepository;
	@Autowired
    private GroupsRepository groupsRepository;
	@Autowired
	UserRepository userRepository;
    @Value("${files.folder}")
    String filesFolder;

    public Record addRecord(String title, Long groupId, Long idUser, MultipartFile file) throws IOException {
        logger.info("=== RECORD UPLOAD START ===");
        logger.info("Title: {}, GroupId: {}, UserId: {}", title, groupId, idUser);
        logger.info("File name: {}, File size: {}, Content type: {}", 
            file.getOriginalFilename(), file.getSize(), file.getContentType());
        logger.info("Files folder: {}", filesFolder);

        // Validate file
        if (file.isEmpty()) {
            logger.error("ERROR: Uploaded file is empty!");
            throw new IOException("Uploaded file is empty. File size: " + file.getSize());
        }
        
        if (file.getSize() <= 0) {
            logger.error("ERROR: File size is 0 or negative: {}", file.getSize());
            throw new IOException("Invalid file size: " + file.getSize());
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType == null || (!contentType.startsWith("video/") && !contentType.equals("application/octet-stream"))) {
            logger.warn("WARNING: Unexpected content type: {}", contentType);
            // Still allow it - some browsers may send octet-stream for video
        }

        String timestamp = Long.toString(System.currentTimeMillis());
        
        // Sanitize filename - remove special characters and spaces
        String originalFilename = file.getOriginalFilename();
        String sanitizedFilename = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
        String newFilename = timestamp + "_" + sanitizedFilename;

        logger.info("Original filename: {}", originalFilename);
        logger.info("Sanitized filename: {}", newFilename);

        Optional<Groups> groupOptional = groupsRepository.findById(groupId);
        User user = userRepository.getById(idUser);
        
        if (groupOptional.isPresent()) {
            Groups group = groupOptional.get();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateFolderName = dateFormat.format(group.getCreationDate());

            Path uploadDir = Paths.get(filesFolder, "Records", dateFolderName);
            logger.info("Upload directory path: {}", uploadDir.toAbsolutePath());
            
            try {
                Files.createDirectories(uploadDir);
                logger.info("Directory created/exists: {}", uploadDir.toAbsolutePath());
            } catch (IOException e) {
                logger.error("ERROR: Failed to create directory: {}", e.getMessage(), e);
                throw new IOException("Failed to create upload directory: " + e.getMessage());
            }

            Path recordPath = uploadDir.resolve(newFilename);
            logger.info("Full file path: {}", recordPath.toAbsolutePath());
            
            try {
                logger.info("Starting file transfer...");
                long startTime = System.currentTimeMillis();
                file.transferTo(recordPath);
                long endTime = System.currentTimeMillis();
                logger.info("File transfer took: {} ms", (endTime - startTime));
                
                // Verify file was saved with content
                long savedFileSize = Files.size(recordPath);
                logger.info("File transferred successfully. Saved file size: {} bytes", savedFileSize);
                
                if (savedFileSize == 0) {
                    logger.error("ERROR: File was saved but is 0 bytes! Original size was: {}", file.getSize());
                    throw new IOException("File transfer failed - saved file is empty");
                }
                
                if (savedFileSize != file.getSize()) {
                    logger.warn("WARNING: File size mismatch. Original: {}, Saved: {}", file.getSize(), savedFileSize);
                }
            } catch (IOException e) {
                logger.error("ERROR: Failed to transfer file: {}", e.getMessage(), e);
                throw new IOException("Failed to save file: " + e.getMessage());
            }

            Record record = new Record();
            record.setTitle(title);
            record.setUser(user);
            record.setVideoLink(dateFolderName + "/" + newFilename);
            record.setGroups(group);

            Record savedRecord = recordRepository.save(record);
            logger.info("Record saved to database with ID: {}", savedRecord.getId());
            logger.info("Video link stored in DB: {}", dateFolderName + "/" + newFilename);
            logger.info("=== RECORD UPLOAD COMPLETE ===");
            
            return savedRecord;
        } else {
            logger.error("ERROR: Group not found with ID: {}", groupId);
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

