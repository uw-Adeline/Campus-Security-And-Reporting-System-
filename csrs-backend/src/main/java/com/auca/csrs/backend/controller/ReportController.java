package com.auca.csrs.backend.controller;

import com.auca.csrs.backend.model.Report;
import com.auca.csrs.backend.service.ReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for managing security reports.
 * Handles all HTTP requests related to the Report entity.
 * Delegates all business logic to the ReportService layer.
 *
 * <p>Follows the MVC pattern: Controller handles HTTP, Service handles logic,
 * Repository handles data access.</p>
 */
@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*") // Allow Vue.js frontend to connect
public class ReportController {

    private final ReportService reportService;

    @Autowired
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Creates a new security report.
     * POST /api/reports
     *
     * @param report the report data from the request body (validated)
     * @return the saved report with HTTP 201 Created
     */
    @PostMapping
    public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) {
        Report savedReport = reportService.createReport(report);
        return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
    }

    /**
     * Retrieves all security reports.
     * GET /api/reports
     *
     * @return list of all reports with HTTP 200 OK
     */
    @GetMapping
    public ResponseEntity<List<Report>> getAllReports() {
        return new ResponseEntity<>(reportService.getAllReports(), HttpStatus.OK);
    }

    /**
     * Retrieves a specific report by its ID.
     * GET /api/reports/{id}
     *
     * @param id the report ID from the URL path
     * @return the report if found (200 OK), or 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Report> getReportById(@PathVariable("id") String id) {
        Optional<Report> report = reportService.getReportById(id);
        return report
                .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * Updates the status of an existing report (Admin action).
     * PATCH /api/reports/{id}/status?status=RESOLVED
     *
     * @param id     the report ID from the URL path
     * @param status the new status value from the query parameter
     * @return the updated report (200 OK), 404 Not Found, or 400 Bad Request
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<Report> updateReportStatus(
            @PathVariable("id") String id,
            @RequestParam("status") String status) {
        try {
            Optional<Report> updatedReport = reportService.updateReportStatus(id, status);
            return updatedReport
                    .map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                    .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Deletes a report by its ID (Admin action).
     * DELETE /api/reports/{id}
     *
     * @param id the report ID from the URL path
     * @return 204 No Content if deleted, or 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable("id") String id) {
        boolean deleted = reportService.deleteReport(id);
        return deleted
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
