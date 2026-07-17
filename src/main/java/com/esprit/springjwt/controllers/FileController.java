package com.esprit.springjwt.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uploads")
@CrossOrigin(origins = "*")
public class FileController {

    @Value("${files.folder}")
    private String filesFolder;

    @GetMapping("/Events/{filename}")
    public ResponseEntity<Resource> serveEvent(@PathVariable String filename) throws IOException {
        Path filePath = Paths.get(filesFolder, "Events", filename);
        System.out.println("✓ Attempting to serve event image from: " + filePath.toAbsolutePath());
        
        Resource resource = new FileSystemResource(filePath);
        if (!resource.exists()) {
            System.out.println("✗ Event image NOT FOUND: " + filePath.toAbsolutePath());
            return ResponseEntity.notFound().build();
        }
        
        System.out.println("✓ Event image found successfully");
        return serveFromFolder(filePath);
    }

    @GetMapping("/debug/config")
    public ResponseEntity<?> getFileConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("filesFolder", filesFolder);
        config.put("eventsDir", new File(filesFolder + "/Events/").getAbsolutePath());
        config.put("eventsDirExists", new File(filesFolder + "/Events/").exists());
        config.put("eventsDirCanWrite", new File(filesFolder + "/Events/").canWrite());
        
        File eventsDir = new File(filesFolder + "/Events/");
        if (eventsDir.exists()) {
            File[] files = eventsDir.listFiles();
            config.put("filesCount", files != null ? files.length : 0);
            if (files != null && files.length > 0) {
                List<String> fileNames = new ArrayList<>();
                for (File f : files) {
                    fileNames.add(f.getName());
                }
                config.put("fileNames", fileNames);
            }
        }
        
        return ResponseEntity.ok(config);
    }

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
        System.out.println("📁 Serving file from path: " + filePath.toAbsolutePath());
        
        File file = filePath.toFile();
        System.out.println("   File absolute path: " + file.getAbsolutePath());
        System.out.println("   File exists: " + file.exists());
        System.out.println("   File is readable: " + file.canRead());
        
        if (!file.exists()) {
            System.out.println("✗ File NOT found: " + file.getAbsolutePath());
            return ResponseEntity.notFound().build();
        }
        
        FileSystemResource resource = new FileSystemResource(file);
        System.out.println("✓ File found successfully");
        
        String filename = file.getName().toLowerCase();
        String contentType;
        if (filename.endsWith(".pdf")) {
            contentType = "application/pdf";
        } else {
            contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
        }
        
        System.out.println("   Content-Type: " + contentType);
        
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header("Content-Disposition", "inline; filename=\"" + file.getName() + "\"")
                .body(resource);
    }
}
