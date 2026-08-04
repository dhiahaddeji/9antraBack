package com.esprit.springjwt.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.InputStreamContent;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;
import com.google.api.services.drive.model.FileList;
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
import java.util.List;

@Service
public class GoogleDriveService {

    private static final String FOLDER_NAME = "9antra Recordings";

    @Value("${google.service.account.json:}")
    private String serviceAccountJson;

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
        try {
            recordingsFolderId = getOrCreateFolder(FOLDER_NAME);
            System.out.println("[GoogleDrive] Recordings folder ready: " + recordingsFolderId);
        } catch (Exception e) {
            System.err.println("[GoogleDrive] Failed to init folder: " + e.getMessage());
        }
    }

    private String getOrCreateFolder(String name) throws Exception {
        Drive service = buildDriveService();

        // Check if folder already exists
        FileList result = service.files().list()
            .setQ("name='" + name + "' and mimeType='application/vnd.google-apps.folder' and trashed=false")
            .setSpaces("drive")
            .setFields("files(id, name)")
            .execute();

        List<File> folders = result.getFiles();
        if (folders != null && !folders.isEmpty()) {
            return folders.get(0).getId();
        }

        // Create folder
        File folderMeta = new File();
        folderMeta.setName(name);
        folderMeta.setMimeType("application/vnd.google-apps.folder");
        File created = service.files().create(folderMeta).setFields("id").execute();
        System.out.println("[GoogleDrive] Created folder '" + name + "': " + created.getId());
        return created.getId();
    }

    /**
     * Uploads a file to the 9antra Recordings folder and makes it publicly readable.
     * Returns the Google Drive view URL.
     */
    public DriveUploadResult uploadFile(String fileName, String mimeType, InputStream content, long size) {
        if (!isConfigured()) return null;
        if (recordingsFolderId == null) {
            try { recordingsFolderId = getOrCreateFolder(FOLDER_NAME); } catch (Exception e) { return null; }
        }

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
