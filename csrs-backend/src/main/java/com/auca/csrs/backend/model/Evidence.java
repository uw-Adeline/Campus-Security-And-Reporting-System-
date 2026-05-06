package com.auca.csrs.backend.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Evidence {

    private String evidenceID;
    private String fileType;
    private String fileName;
    private String filePath;
    private LocalDateTime uploadedAt;

    public Evidence() {
        this.evidenceID = UUID.randomUUID().toString();
        this.uploadedAt = LocalDateTime.now();
    }

    public Evidence(String fileType, String fileName, String filePath) {
        this.evidenceID = UUID.randomUUID().toString();
        this.fileType = fileType;
        this.fileName = fileName;
        this.filePath = filePath;
        this.uploadedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getEvidenceID() { return evidenceID; }
    public void setEvidenceID(String evidenceID) { this.evidenceID = evidenceID; }

    public String getFileType() { return fileType; }
    public void setFileType(String fileType) { this.fileType = fileType; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }
}
