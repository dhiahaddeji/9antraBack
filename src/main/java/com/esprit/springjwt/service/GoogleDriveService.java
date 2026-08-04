package com.esprit.springjwt.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.Permission;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Service
public class GoogleDriveService {

    @Value("${google.service.account.json:}")
    private String serviceAccountJson;

    @Value("${google.drive.folder.id:}")
    private String configuredFolderId;

    private String recordingsFolderId;

    private Drive buildDriveService() throws Exception {
        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8)))
            .createScoped(Collections.singleton(DriveScopes.DRIVE));

        return new Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            JacksonFactory.getDefaultInstance(),
            new HttpCredentialsAdapter(credentials)
        ).setApplicationName("9antra-platform").build();
    }

    public boolean isConfigured() {
        return serviceAccountJson != null && !serviceAccountJson.isBlank();
    }

    @PostConstruct
    public void initDriveFolder() {
        if (!isConfigured()) {
            System.out.println("[GoogleDrive] Not configured — Drive upload disabled");
            return;
        }
        if (configuredFolderId != null && !configuredFolderId.isBlank()) {
            recordingsFolderId = configuredFolderId;
            System.out.println("[GoogleDrive] Using configured folder: " + recordingsFolderId);
        } else {
            System.err.println("[GoogleDrive] No folder ID configured (GOOGLE_DRIVE_FOLDER_ID)");
        }
    }

    /**
     * Uploads a file to the 9antra Recordings folder and makes it publicly readable.
     * Returns the Google Drive view URL.
     */
    public DriveUploadResult uploadFile(String fileName, String mimeType, InputStream content, long size) {
        if (!isConfigured() || recordingsFolderId == null) return null;

        try {
            Drive service = buildDriveService();

            File fileMeta = new File();
            fileMeta.setName(fileName);
            fileMeta.setParents(Collections.singletonList(recordingsFolderId));

            InputStreamContent mediaContent = new InputStreamContent(mimeType, content);
            if (size > 0) mediaContent.setLength(size);

            File uploaded = service.files()
                .create(fileMeta, mediaContent)
                .setFields("id, name, webViewLink")
                .execute();

            // Make publicly readable (anyone with link)
            Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
            service.permissions().create(uploaded.getId(), permission).execute();

            String viewUrl = "https://drive.google.com/file/d/" + uploaded.getId() + "/preview";
            System.out.println("[GoogleDrive] Uploaded: " + fileName + " → " + viewUrl);

            return new DriveUploadResult(uploaded.getId(), viewUrl);

        } catch (Exception e) {
            System.err.println("[GoogleDrive] Upload failed: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deletes a file from Drive by its file ID.
     */
    public void deleteFile(String fileId) {
        if (!isConfigured() || fileId == null) return;
        try {
            Drive service = buildDriveService();
            service.files().delete(fileId).execute();
            System.out.println("[GoogleDrive] Deleted file: " + fileId);
        } catch (Exception e) {
            System.err.println("[GoogleDrive] Delete failed for " + fileId + ": " + e.getMessage());
        }
    }

    public static class DriveUploadResult {
        public final String fileId;
        public final String viewUrl;

        public DriveUploadResult(String fileId, String viewUrl) {
            this.fileId = fileId;
            this.viewUrl = viewUrl;
        }
    }
}
