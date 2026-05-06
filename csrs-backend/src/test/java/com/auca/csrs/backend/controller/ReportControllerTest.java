package com.auca.csrs.backend.controller;

import com.auca.csrs.backend.model.Report;
import com.auca.csrs.backend.service.ReportService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests for ReportController (Web Layer).
 *
 * @WebMvcTest loads only the web layer (Controller + Filters).
 * It does NOT start a full server or connect to a database.
 *
 * MockMvc simulates HTTP requests so we can test our API endpoints
 * without actually running the server.
 */
@WebMvcTest(ReportController.class)
class ReportControllerTest {

    // MockMvc simulates HTTP requests to our controller
    @Autowired
    private MockMvc mockMvc;

    // MockBean creates a mock of ReportService for the web layer
    @MockBean
    private ReportService reportService;

    // ObjectMapper converts Java objects to JSON and back
    @Autowired
    private ObjectMapper objectMapper;

    private Report sampleReport;

    @BeforeEach
    void setUp() {
        sampleReport = new Report(
                "Suspicious Activity at Gate 2",
                "Suspicious Activity",
                "Main Gate",
                "HIGH",
                "Unknown person seen near gate",
                true
        );
        sampleReport.setReportID("test-id-001");
        sampleReport.setStatus("PENDING");
    }

    // =========================================================
    // TEST GROUP 1: POST /api/reports
    // =========================================================

    @Test
    @DisplayName("TC-CTRL-001: POST /api/reports - should create report and return 201")
    void createReport_ValidRequest_Returns201() throws Exception {
        // ARRANGE: Mock the service to return our sample report
        when(reportService.createReport(any(Report.class))).thenReturn(sampleReport);

        // ACT & ASSERT: Perform POST request and verify response
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleReport)))
                .andExpect(status().isCreated())                          // HTTP 201
                .andExpect(jsonPath("$.title").value("Suspicious Activity at Gate 2"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.category").value("Suspicious Activity"));
    }

    @Test
    @DisplayName("TC-CTRL-002: POST /api/reports - should return 400 when title is missing")
    void createReport_MissingTitle_Returns400() throws Exception {
        // ARRANGE: Create a report with no title (violates @NotBlank)
        Report invalidReport = new Report();
        invalidReport.setCategory("Theft");
        invalidReport.setLocation("Library");
        invalidReport.setDescription("Something was stolen");
        // title is intentionally missing

        // ACT & ASSERT
        mockMvc.perform(post("/api/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidReport)))
                .andExpect(status().isBadRequest()); // HTTP 400
    }

    // =========================================================
    // TEST GROUP 2: GET /api/reports
    // =========================================================

    @Test
    @DisplayName("TC-CTRL-003: GET /api/reports - should return all reports with 200")
    void getAllReports_ReportsExist_Returns200WithList() throws Exception {
        // ARRANGE
        Report report2 = new Report("Theft in Library", "Theft", "Library",
                "CRITICAL", "Laptop stolen", false);
        report2.setReportID("test-id-002");

        when(reportService.getAllReports()).thenReturn(Arrays.asList(sampleReport, report2));

        // ACT & ASSERT
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())                               // HTTP 200
                .andExpect(jsonPath("$.length()").value(2))               // 2 reports
                .andExpect(jsonPath("$[0].title").value("Suspicious Activity at Gate 2"))
                .andExpect(jsonPath("$[1].title").value("Theft in Library"));
    }

    @Test
    @DisplayName("TC-CTRL-004: GET /api/reports - should return empty list with 200")
    void getAllReports_NoReports_Returns200WithEmptyList() throws Exception {
        // ARRANGE
        when(reportService.getAllReports()).thenReturn(Arrays.asList());

        // ACT & ASSERT
        mockMvc.perform(get("/api/reports"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }

    // =========================================================
    // TEST GROUP 3: GET /api/reports/{id}
    // =========================================================

    @Test
    @DisplayName("TC-CTRL-005: GET /api/reports/{id} - should return report when found")
    void getReportById_ExistingId_Returns200WithReport() throws Exception {
        // ARRANGE
        when(reportService.getReportById("test-id-001")).thenReturn(Optional.of(sampleReport));

        // ACT & ASSERT
        mockMvc.perform(get("/api/reports/test-id-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reportID").value("test-id-001"))
                .andExpect(jsonPath("$.title").value("Suspicious Activity at Gate 2"));
    }

    @Test
    @DisplayName("TC-CTRL-006: GET /api/reports/{id} - should return 404 when not found")
    void getReportById_NonExistingId_Returns404() throws Exception {
        // ARRANGE
        when(reportService.getReportById("non-existing-id")).thenReturn(Optional.empty());

        // ACT & ASSERT
        mockMvc.perform(get("/api/reports/non-existing-id"))
                .andExpect(status().isNotFound()); // HTTP 404
    }

    // =========================================================
    // TEST GROUP 4: PATCH /api/reports/{id}/status
    // =========================================================

    @Test
    @DisplayName("TC-CTRL-007: PATCH /api/reports/{id}/status - should update status to RESOLVED")
    void updateReportStatus_ValidStatus_Returns200() throws Exception {
        // ARRANGE
        sampleReport.setStatus("RESOLVED");
        when(reportService.updateReportStatus("test-id-001", "RESOLVED"))
                .thenReturn(Optional.of(sampleReport));

        // ACT & ASSERT
        mockMvc.perform(patch("/api/reports/test-id-001/status")
                        .param("status", "RESOLVED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RESOLVED"));
    }

    @Test
    @DisplayName("TC-CTRL-008: PATCH /api/reports/{id}/status - should return 400 for invalid status")
    void updateReportStatus_InvalidStatus_Returns400() throws Exception {
        // ARRANGE: Service throws exception for invalid status
        when(reportService.updateReportStatus(eq("test-id-001"), eq("INVALID")))
                .thenThrow(new IllegalArgumentException("Invalid status: INVALID"));

        // ACT & ASSERT
        mockMvc.perform(patch("/api/reports/test-id-001/status")
                        .param("status", "INVALID"))
                .andExpect(status().isBadRequest()); // HTTP 400
    }

    @Test
    @DisplayName("TC-CTRL-009: PATCH /api/reports/{id}/status - should return 404 when report not found")
    void updateReportStatus_NonExistingId_Returns404() throws Exception {
        // ARRANGE
        when(reportService.updateReportStatus("non-existing-id", "RESOLVED"))
                .thenReturn(Optional.empty());

        // ACT & ASSERT
        mockMvc.perform(patch("/api/reports/non-existing-id/status")
                        .param("status", "RESOLVED"))
                .andExpect(status().isNotFound()); // HTTP 404
    }

    // =========================================================
    // TEST GROUP 5: DELETE /api/reports/{id}
    // =========================================================

    @Test
    @DisplayName("TC-CTRL-010: DELETE /api/reports/{id} - should return 204 when deleted")
    void deleteReport_ExistingId_Returns204() throws Exception {
        // ARRANGE
        when(reportService.deleteReport("test-id-001")).thenReturn(true);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/reports/test-id-001"))
                .andExpect(status().isNoContent()); // HTTP 204
    }

    @Test
    @DisplayName("TC-CTRL-011: DELETE /api/reports/{id} - should return 404 when not found")
    void deleteReport_NonExistingId_Returns404() throws Exception {
        // ARRANGE
        when(reportService.deleteReport("non-existing-id")).thenReturn(false);

        // ACT & ASSERT
        mockMvc.perform(delete("/api/reports/non-existing-id"))
                .andExpect(status().isNotFound()); // HTTP 404
    }
}
