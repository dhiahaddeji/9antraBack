package com.esprit.springjwt.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "*")
public class FileController {

    @Value("${files.folder}")
    private String filesFolder;

    @GetMapping("/Documents/{filename}")
    public ResponseEntity<Resource> serveDocument(@PathVariable String filename) throws IOException {
        return serveFromFolder(Paths.get(filesFolder, "Documents", filename));
    }

    @GetMapping("/Records/{date}/{filename}")
    public ResponseEntity<Resource> serveRecord(@PathVariable String date,
                                                 @PathVariable String filename) throws IOException {
        return serveFromFolder(Paths.get(filesFolder, "Records", date, filename));
    }

    @GetMapping("/Certifications/**")
    public ResponseEntity<Resource> serveCertificate(@RequestParam String path) throws IOException {
        // path example: "Certifications/Grafana June 2026 4649761/test test.pdf"
        return serveFromFolder(Paths.get(filesFolder, path));
    }
    @GetMapping("/Projects")
    public ResponseEntity<Resource> serveProject(@RequestParam String path) throws IOException {
        return serveFromFolder(Paths.get(filesFolder, "projects").resolve(path));
    }

    @GetMapping("/Certifications")
    public ResponseEntity<Resource> serveCertification(@RequestParam String path) throws IOException {
        // path = "Certifications/Formation Month 12345/Name.pdf"
        return serveFromFolder(Paths.get(filesFolder).resolve(path));
    }

    private ResponseEntity<Resource> serveFromFolder(Path filePath) throws IOException {
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }
        String filename = filePath.getFileName().toString().toLowerCase();
        String contentType;
        if (filename.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else {
            contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "inline; filename=\"" + filePath.getFileName() + "\"")
                .body(resource);
    }
}

