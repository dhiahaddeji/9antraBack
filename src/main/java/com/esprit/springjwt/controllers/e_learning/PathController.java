package com.esprit.springjwt.controllers.e_learning;

import com.esprit.springjwt.entity.e_learning.Path;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.service.e_learning.IPathService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/e-learning/path")
public class PathController {

    @Autowired
    IPathService pathService;

    @GetMapping
    public ResponseEntity<?> getPaths() {
    	List<Path> paths = new ArrayList<>();
        try {
             paths = pathService.getAll();
            return new ResponseEntity<>(paths, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(paths,  HttpStatus.OK);
        }
    }

    @GetMapping("{idPath}")
    public ResponseEntity<?> getOne(@PathVariable("idPath") Long id) {
        try {
            Path p = pathService.getOne(id);

            return new ResponseEntity<>(p, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<?> addPath(@RequestParam("image") MultipartFile image,
                                     @RequestParam String path)throws JsonProcessingException {
        if(path.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Path p = objectMapper.readValue(path, Path.class);
            System.out.println("\n" + p + "\n");
            return new ResponseEntity<>(pathService.addPath(image, p), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping
    public ResponseEntity<?> updatePath(@RequestParam("image") MultipartFile image,
                                     @RequestParam String path) throws JsonProcessingException, FileNotFoundException {
        if(path.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Path p = objectMapper.readValue(path, Path.class);
            System.out.println("\n" + p + "\n");
            return new ResponseEntity<>(pathService.updatePath(image, p), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("{idPath}")
    public ResponseEntity<?> deletePath(@PathVariable("idPath") Long idPath) {
        try {
            pathService.deletePath(idPath);
        } catch (IOException e) {
            return new ResponseEntity<>("Error While deleting Path's image or file", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }




}
