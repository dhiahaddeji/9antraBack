package com.esprit.springjwt.controllers.e_learning;

import com.esprit.springjwt.entity.e_learning.Report;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.service.e_learning.IReportService;
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

@RestController()
@RequestMapping("/api/e-learning/reports")
public class ReportController {

    @Autowired
    IReportService reportService;

    @GetMapping()
    public ResponseEntity<?> getReports() {
    	List<Report> reports =new ArrayList<>();
        try {
            reports=reportService.getAll();
            return new ResponseEntity<>(reports, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(reports,HttpStatus.OK);
        }
    }

    @GetMapping("{idCourse}")
    public ResponseEntity<?> getOne(@PathVariable("idCourse") Long id) {
        try {
            Report r = reportService.getOne(id);

            return new ResponseEntity<>(r, HttpStatus.OK);
        } catch (RecordNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }

    @PostMapping
    public ResponseEntity<?> addReport(@RequestParam("image") MultipartFile image,
                                       @RequestParam("file") MultipartFile file,
                                       @RequestParam String report)throws JsonProcessingException {
        if(report.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Report r = objectMapper.readValue(report, Report.class);
            System.out.println("\n" + r + "\n");
            return new ResponseEntity<>(reportService.addReport(image, file, r), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping
    public ResponseEntity<?> updateReport(@RequestParam("image") MultipartFile image,
                                          @RequestParam("file") MultipartFile file,
                                          @RequestParam String report) throws JsonProcessingException, FileNotFoundException {
        if(report.length() != 0) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            Report r = objectMapper.readValue(report, Report.class);

            return new ResponseEntity<>(reportService.updateReport(image, file, r), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/{idReport}")
    public ResponseEntity<?> deleteReport(@PathVariable("idReport") Long idReport) {
        try {
            reportService.deleteReport(idReport);
        } catch (IOException e) {
            return new ResponseEntity<>("Error While deleting Report's image or file", HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
