package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Report;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface IReportService {
    List<Report> getAll();

    Report addReport(MultipartFile image, MultipartFile file, Report report);

    Report updateReport(MultipartFile image, MultipartFile file, Report report) throws FileNotFoundException;

    void deleteReport(Long idReport) throws IOException;

    Report getOne(Long id);
}
