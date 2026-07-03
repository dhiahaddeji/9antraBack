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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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

        Optional<Groups> groupOptional = groupsRepository.findById(groupId);
        User user = userRepository.getById(idUser);
        if (groupOptional.isPresent()) {
            Groups group = groupOptional.get();

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String dateFolderName = dateFormat.format(group.getCreationDate());

            Path uploadDir = Paths.get(filesFolder, "Records", dateFolderName);
            Files.createDirectories(uploadDir);

            Path recordPath = uploadDir.resolve(newFilename);
            file.transferTo(recordPath);

            Record record = new Record();
            record.setTitle(title);
            record.setUser(user);
            record.setVideoLink(dateFolderName + "/" + newFilename);
            record.setGroups(group);

            return recordRepository.save(record);
        } else {
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

