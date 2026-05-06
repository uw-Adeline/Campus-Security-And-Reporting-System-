package com.auca.csrs.backend.service;

import com.auca.csrs.backend.model.Report;
import com.auca.csrs.backend.repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Report entity.
 * Implements business logic and acts as an intermediary between Controller and Repository.
 * This follows the Service Layer Design Pattern for separation of concerns.
 */
@Service
public class ReportService {

    private final ReportRepository reportRepository;

    @Autowired
    public ReportService(ReportRepository reportRepository) {
        this.reportRepository = reportRepository;
    }

    /**
     * Creates and saves a new report to the database.
     * 
     * @param report the report object to be saved
     * @return the saved report with generated ID
     */
    public Report createReport(Report report) {
        // Business logic: Ensure default values are set
        if (report.getStatus() == null || report.getStatus().isEmpty()) {
            report.setStatus("PENDING");
        }
        return reportRepository.save(report);
    }

    /**
     * Retrieves all reports from the database.
     * 
     * @return list of all reports
     */
    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    /**
     * Retrieves a specific report by its ID.
     * 
     * @param reportId the unique identifier of the report
     * @return Optional containing the report if found
     */
    public Optional<Report> getReportById(String reportId) {
        return reportRepository.findById(reportId);
    }

    /**
     * Retrieves all reports filtered by status.
     * 
     * @param status the status to filter by (PENDING, IN_PROGRESS, RESOLVED, DISMISSED)
     * @return list of reports matching the status
     */
    public List<Report> getReportsByStatus(String status) {
        return reportRepository.findByStatus(status);
    }

    /**
     * Retrieves all reports submitted by a specific student.
     * 
     * @param studentId the unique identifier of the student
     * @return list of reports submitted by the student
     */
    public List<Report> getReportsByStudent(String studentId) {
        return reportRepository.findByStudentID(studentId);
    }

    /**
     * Updates the status of an existing report.
     * Business logic: Validates that the new status is one of the allowed values.
     * 
     * @param reportId the unique identifier of the report
     * @param newStatus the new status to set
     * @return Optional containing the updated report if successful
     * @throws IllegalArgumentException if the status is invalid
     */
    public Optional<Report> updateReportStatus(String reportId, String newStatus) {
        // Business logic: Validate status
        if (!isValidStatus(newStatus)) {
            throw new IllegalArgumentException("Invalid status: " + newStatus);
        }

        Optional<Report> reportData = reportRepository.findById(reportId);
        if (reportData.isPresent()) {
            Report report = reportData.get();
            report.setStatus(newStatus);
            return Optional.of(reportRepository.save(report));
        }
        return Optional.empty();
    }

    /**
     * Deletes a report by its ID.
     * 
     * @param reportId the unique identifier of the report to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteReport(String reportId) {
        if (reportRepository.existsById(reportId)) {
            reportRepository.deleteById(reportId);
            return true;
        }
        return false;
    }

    /**
     * Helper method to validate report status.
     * 
     * @param status the status string to validate
     * @return true if valid, false otherwise
     */
    private boolean isValidStatus(String status) {
        return status.equals("PENDING") || 
               status.equals("IN_PROGRESS") || 
               status.equals("RESOLVED") || 
               status.equals("DISMISSED");
    }
}
