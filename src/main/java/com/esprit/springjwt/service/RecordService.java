package com.esprit.springjwt.service;

import com.esprit.springjwt.entity.Groups;
import com.esprit.springjwt.entity.Record;
import com.esprit.springjwt.entity.User;
import com.esprit.springjwt.repository.GroupsRepository;
import com.esprit.springjwt.repository.RecordRepository;
import com.esprit.springjwt.repository.UserRepository;

import com.esprit.springjwt.service.GoogleDriveService;
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
	@Autowired
	private GoogleDriveService googleDriveService;
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

        if (!groupOptional.isPresent()) {
            logger.error("ERROR: Group not found with ID: {}", groupId);
            throw new IllegalArgumentException("Group not found with ID: " + groupId);
        }

        Groups group = groupOptional.get();
        Record record = new Record();
        record.setTitle(title);
        record.setUser(user);
        record.setGroups(group);

        // Try Google Drive first; fall back to local filesystem
        if (googleDriveService.isConfigured()) {
            logger.info("Uploading to Google Drive...");
            String mimeType = file.getContentType() != null ? file.getContentType() : "video/mp4";
            GoogleDriveService.DriveUploadResult driveResult =
                googleDriveService.uploadFile(newFilename, mimeType, file.getInputStream(), file.getSize());

            if (driveResult != null) {
                record.setVideoLink(driveResult.viewUrl);
                record.setDriveFileId(driveResult.fileId);
                logger.info("Uploaded to Drive: {}", driveResult.viewUrl);
            } else {
                logger.warn("Drive upload failed — falling back to local storage");
                saveLocally(file, group, newFilename, record);
            }
        } else {
            logger.info("Drive not configured — saving locally");
            saveLocally(file, group, newFilename, record);
        }

        Record savedRecord = recordRepository.save(record);
        logger.info("Record saved with ID: {} | link: {}", savedRecord.getId(), savedRecord.getVideoLink());
        logger.info("=== RECORD UPLOAD COMPLETE ===");
        return savedRecord;
    }
    private void saveLocally(MultipartFile file, Groups group, String newFilename, Record record) throws IOException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateFolderName = dateFormat.format(group.getCreationDate());
        Path uploadDir = Paths.get(filesFolder, "Records", dateFolderName);
        Files.createDirectories(uploadDir);
        Path recordPath = uploadDir.resolve(newFilename);
        file.transferTo(recordPath);
        record.setVideoLink(dateFolderName + "/" + newFilename);
        logger.info("Saved locally: {}", recordPath.toAbsolutePath());
    }

    public Iterable<Record> getRecordsByGroups(Long groupId) {
        return recordRepository.findByGroups(groupId);
    }

    public void deleteRecord(Long id) {
        recordRepository.findById(id).ifPresent(record -> {
            if (record.getDriveFileId() != null) {
                googleDriveService.deleteFile(record.getDriveFileId());
            }
            recordRepository.deleteById(id);
        });
    }

}

