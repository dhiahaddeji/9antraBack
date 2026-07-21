package com.esprit.springjwt.security;

import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

public class FileUploadValidator {

    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024;   // 5 MB
    private static final long MAX_VIDEO_BYTES  = 200 * 1024 * 1024; // 200 MB
    private static final long MAX_PDF_BYTES    = 10 * 1024 * 1024;  // 10 MB
    private static final long MAX_ANY_BYTES    = 50 * 1024 * 1024;  // 50 MB default

    private static final Set<String> ALLOWED_IMAGE_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final Set<String> ALLOWED_VIDEO_TYPES = Set.of(
            "video/mp4", "video/mpeg", "video/webm", "video/ogg", "video/quicktime"
    );
    private static final Set<String> ALLOWED_PDF_TYPES = Set.of("application/pdf");

    public static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        validateType(file, ALLOWED_IMAGE_TYPES, "image (JPEG, PNG, GIF, WEBP)");
        validateSize(file, MAX_IMAGE_BYTES, "5 MB");
    }

    public static void validateVideo(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        validateType(file, ALLOWED_VIDEO_TYPES, "video (MP4, WEBM, MOV)");
        validateSize(file, MAX_VIDEO_BYTES, "200 MB");
    }

    public static void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        validateType(file, ALLOWED_PDF_TYPES, "PDF");
        validateSize(file, MAX_PDF_BYTES, "10 MB");
    }

    public static void validateImageOrPdf(MultipartFile file) {
        if (file == null || file.isEmpty()) return;
        Set<String> allowed = new java.util.HashSet<>(ALLOWED_IMAGE_TYPES);
        allowed.addAll(ALLOWED_PDF_TYPES);
        validateType(file, allowed, "image or PDF");
        validateSize(file, MAX_ANY_BYTES, "50 MB");
    }

    private static void validateType(MultipartFile file, Set<String> allowed, String label) {
        String ct = file.getContentType();
        if (ct == null || !allowed.contains(ct.toLowerCase())) {
            throw new IllegalArgumentException(
                    "Invalid file type: '" + ct + "'. Accepted: " + label);
        }
    }

    private static void validateSize(MultipartFile file, long maxBytes, String label) {
        if (file.getSize() > maxBytes) {
            throw new IllegalArgumentException(
                    "File too large. Maximum allowed size is " + label);
        }
    }
}
