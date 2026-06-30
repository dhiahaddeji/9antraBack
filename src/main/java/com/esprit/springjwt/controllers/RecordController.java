package com.esprit.springjwt.controllers;

import com.esprit.springjwt.entity.Record;
import com.esprit.springjwt.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/records")
public class RecordController {

    @Autowired
    private RecordService recordService;

    @PostMapping("/add")
    public ResponseEntity<?> addRecord(@RequestParam("title") String title,
                                       @RequestParam("groupId") Long groupId,
                                       @RequestParam("idUser") Long idUser,
                                       @RequestParam("file") MultipartFile file) {
        try {
            Record record = recordService.addRecord(title, groupId, idUser, file);
            return new ResponseEntity<>(record, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Handle the case where the group with the provided ID is not found
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            // Handle IO-related errors when saving the file
            return new ResponseEntity<>("Failed to save the file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            // Handle other unexpected exceptions
            return new ResponseEntity<>("An unexpected error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/allbygroup/{groupId}")
    public ResponseEntity<?> getAllRecordsByGroupId(@PathVariable Long groupId) {
        try {
            return new ResponseEntity<>(recordService.getRecordsByGroups(groupId), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //delete record by id
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRecord(@PathVariable Long id) {
        try {
            recordService.deleteRecord(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

