package com.auca.csrs.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "reports")
public class Report {

    @Id
    private String reportID;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Location is required")
    private String location;

    private String priorityLevel;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    private String status; // PENDING, IN_PROGRESS, RESOLVED, DISMISSED
    private LocalDateTime submittedAt;
    private Boolean isAnonymous;
    
    // Reference to the Student who created it (can be null if anonymous)
    private String studentID;
    
    // Embedded evidence list
    private List<Evidence> evidenceList = new ArrayList<>();

    public Report() {
        this.submittedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    public Report(String title, String category, String location, String priorityLevel, String description, Boolean isAnonymous) {
        this.title = title;
        this.category = category;
        this.location = location;
        this.priorityLevel = priorityLevel;
        this.description = description;
        this.isAnonymous = isAnonymous;
        this.submittedAt = LocalDateTime.now();
        this.status = "PENDING";
    }

    // Getters and Setters
    public String getReportID() { return reportID; }
    public void setReportID(String reportID) { this.reportID = reportID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPriorityLevel() { return priorityLevel; }
    public void setPriorityLevel(String priorityLevel) { this.priorityLevel = priorityLevel; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public String getStudentID() { return studentID; }
    public void setStudentID(String studentID) { this.studentID = studentID; }

    public List<Evidence> getEvidenceList() { return evidenceList; }
    public void setEvidenceList(List<Evidence> evidenceList) { this.evidenceList = evidenceList; }
}
