package com.esprit.springjwt.service.e_learning;

import com.esprit.springjwt.entity.e_learning.Report;
import com.esprit.springjwt.exception.FileTypeErrorException;
import com.esprit.springjwt.exception.RecordNotFoundException;
import com.esprit.springjwt.repository.e_learning.IReportRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
public class ReportServiceImpl implements IReportService{

    @Autowired
    IReportRepository reportRepository;

    @Value("${files.folder}")
    String filesFolder;


    @Override
    public List<Report> getAll() {

        List<Report> reports = reportRepository.findAll();

        if(reports.isEmpty())
            throw new RecordNotFoundException("No reports found");

        return reports;
    }

    @Override
    public Report addReport(MultipartFile image, MultipartFile file, Report report) {

        Report sr = new Report();

        String contentTypeImage = image.getContentType();
        String contentTypeFile = file.getContentType();

        if (contentTypeImage != null && (
                contentTypeImage.equals("image/jpeg") ||
                        contentTypeImage.equals("image/jpg") ||
                        contentTypeImage.equals("image/png") ||
                        contentTypeImage.equals("image/avif")
        ) && contentTypeFile != null && (
                contentTypeFile.equals("application/pdf") ||
                        contentTypeFile.equals("application/msword") ||
                        contentTypeFile.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentTypeFile.equals("application/vnd.ms-word") ||
                        contentTypeFile.equals("application/vnd.ms-word.document.macroEnabled.12") ||
                        contentTypeFile.equals("application/vnd.ms-word.template.macroEnabled.12") ||
                        contentTypeFile.equals("application/x-latex")
                )) {
            sr = reportRepository.save(report);
            boolean isExit = new File(filesFolder + "/Reports/").exists();
            if(!isExit)
                new File (filesFolder + "/Reports/").mkdir();

            String reportStorage = filesFolder + "/Reports/Report_" + sr.getId();

            File rFolder = new File(reportStorage);
            if (!rFolder.exists()) {
                boolean created = rFolder.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create Report folder");
                }
            }

            String reportImage = LocalDateTime.now()
                    .format(DateTimeFormatter.
                            ofPattern("yyyyMMddHHmmss")) + "_" + image.getOriginalFilename();
            String reportFile = LocalDateTime.now()
                    .format(DateTimeFormatter.
                            ofPattern("yyyyMMddHHmmss")) + "_" + file.getOriginalFilename();

            try {
                FileUtils.writeByteArrayToFile(new File( reportStorage + "\\" + reportImage) , image.getBytes() );
                FileUtils.writeByteArrayToFile(new File( reportStorage + "\\" + reportFile) , file.getBytes() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            sr.setImage(reportImage);
            sr.setFile(reportFile);

            reportRepository.save(sr);

        } else
            throw new FileTypeErrorException("image or file type is not allowed");

        return sr;
    }

    @Override
    public Report updateReport(MultipartFile image, MultipartFile file, Report r) throws FileNotFoundException {

        Report report = reportRepository.findById(r.getId()).orElseThrow(() -> new RecordNotFoundException("Report Not found"));

        String contentTypeImage = image.getContentType();
        String contentTypeFile = file.getContentType();

        if (contentTypeImage != null && (
                contentTypeImage.equals("image/jpeg") ||
                        contentTypeImage.equals("image/jpg") ||
                        contentTypeImage.equals("image/png") ||
                        contentTypeImage.equals("image/avif")
        ) && contentTypeFile != null && (
                contentTypeFile.equals("application/pdf") ||
                        contentTypeFile.equals("application/msword") ||
                        contentTypeFile.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
                        contentTypeFile.equals("application/vnd.ms-word") ||
                        contentTypeFile.equals("application/vnd.ms-word.document.macroEnabled.12") ||
                        contentTypeFile.equals("application/vnd.ms-word.template.macroEnabled.12") ||
                        contentTypeFile.equals("application/x-latex")
        )) {
            boolean isExit = new File(filesFolder + "/Reports/").exists();
            if(!isExit)
                throw new FileNotFoundException("Report folder not found !!");

            File originalImage = new File( filesFolder + "/Reports/Report_" + report.getId() + "\\" + report.getImage());
            File orirginalFile = new File( filesFolder + "/Reports/Report_" + report.getId() + "\\" + report.getFile());

            if (originalImage.delete())
                log.info("image deleted successfully");
            else
                log.error("Error while deleting old image");

            if (orirginalFile.delete())
                log.info("File deleted successfully");
            else
                log.error("Error while deleting old file");

            String reportStorage = filesFolder + "/Reports/Report_" + report.getId();

            File cFolder = new File(reportStorage);
            if (!cFolder.exists()) {
                boolean created = cFolder.mkdirs();
                if (!created) {
                    throw new RuntimeException("Failed to create Report folder");
                }
            }

            String reportImage;
            if(!report.getImage().equals(image.getOriginalFilename())) {
                reportImage = LocalDateTime.now()
                        .format(DateTimeFormatter.
                                ofPattern("yyyyMMddHHmmss")) + "_" + image.getOriginalFilename();
            } else
                reportImage = report.getImage();

            String reportFile;
            if( !report.getFile().equals(file.getOriginalFilename())) {
                reportFile = LocalDateTime.now()
                        .format(DateTimeFormatter.
                                ofPattern("yyyyMMddHHmmss")) + "_" + file.getOriginalFilename();
            } else
                reportFile = report.getFile();

            try {
                FileUtils.writeByteArrayToFile(new File( reportStorage + "\\" + reportImage) , image.getBytes() );
                FileUtils.writeByteArrayToFile(new File( reportStorage + "\\" + reportFile) , file.getBytes() );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            report.setDescription(r.getDescription());
            report.setTitle(r.getTitle());
            report.setFile(reportFile);
            report.setImage(reportImage);

            return reportRepository.save(report);

        } else
            throw new FileTypeErrorException("image or file type is not allowed");

    }

    @Override
    public void deleteReport(Long idReport) throws IOException {
        Report r = reportRepository.findById(idReport).orElseThrow(() -> new RecordNotFoundException("Report not found"));

        File reportStorage = new File( filesFolder + "/Reports/Report_" + r.getId());

        reportRepository.deleteById(idReport);
        FileUtils.deleteDirectory(reportStorage);
    }

    @Override
    public Report getOne(Long id) {
        Report r = reportRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("Report not found"));

        return r;
    }
}
