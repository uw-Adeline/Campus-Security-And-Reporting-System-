package com.auca.csrs.backend.service;

import com.auca.csrs.backend.model.Report;
import com.auca.csrs.backend.repository.ReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit Tests for ReportService.
 *
 * These tests verify the business logic in the Service Layer
 * WITHOUT connecting to a real database.
 *
 * We use Mockito to "mock" (fake) the ReportRepository,
 * so tests run fast and don't need MongoDB running.
 *
 * Test Naming Convention: methodName_StateUnderTest_ExpectedBehavior
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    // @Mock creates a fake version of ReportRepository
    // It doesn't actually connect to MongoDB
    @Mock
    private ReportRepository reportRepository;

    // @InjectMocks creates a real ReportService and injects the mock repository into it
    @InjectMocks
    private ReportService reportService;

    // A sample report used across multiple tests
    private Report sampleReport;

    /**
     * This method runs BEFORE each test.
     * It sets up fresh test data so tests don't affect each other.
     */
    @BeforeEach
    void setUp() {
        sampleReport = new Report(
                "Suspicious Activity at Gate 2",  // title
                "Suspicious Activity",             // category
                "Main Gate",                       // location
                "HIGH",                            // priorityLevel
                "Unknown person seen near gate",   // description
                true                               // isAnonymous
        );
        sampleReport.setReportID("test-id-001");
    }

    // =========================================================
    // TEST GROUP 1: createReport()
    // =========================================================

    @Test
    @DisplayName("TC-SRV-001: Create report - should save and return report with PENDING status")
    void createReport_ValidReport_ReturnsSavedReport() {
        // ARRANGE: Tell the mock what to return when save() is called
        when(reportRepository.save(any(Report.class))).thenReturn(sampleReport);

        // ACT: Call the method we are testing
        Report result = reportService.createReport(sampleReport);

        // ASSERT: Verify the result is what we expect
        assertNotNull(result, "Saved report should not be null");
        assertEquals("Suspicious Activity at Gate 2", result.getTitle());
        assertEquals("PENDING", result.getStatus());

        // Verify that repository.save() was called exactly once
        verify(reportRepository, times(1)).save(sampleReport);
    }

    @Test
    @DisplayName("TC-SRV-002: Create report - should set default PENDING status if status is null")
    void createReport_NullStatus_SetsDefaultPendingStatus() {
        // ARRANGE: Create a report with no status set
        Report reportWithNoStatus = new Report();
        reportWithNoStatus.setTitle("Test Report");
        reportWithNoStatus.setStatus(null); // Explicitly null

        when(reportRepository.save(any(Report.class))).thenReturn(reportWithNoStatus);

        // ACT
        reportService.createReport(reportWithNoStatus);

        // ASSERT: Status should have been set to PENDING before saving
        assertEquals("PENDING", reportWithNoStatus.getStatus(),
                "Status should default to PENDING when null");
    }

    // =========================================================
    // TEST GROUP 2: getAllReports()
    // =========================================================

    @Test
    @DisplayName("TC-SRV-003: Get all reports - should return list of all reports")
    void getAllReports_ReportsExist_ReturnsAllReports() {
        // ARRANGE: Create a list of 2 reports
        Report report2 = new Report("Theft in Library", "Theft", "Library", "CRITICAL",
                "Laptop stolen", false);
        List<Report> reportList = Arrays.asList(sampleReport, report2);

        when(reportRepository.findAll()).thenReturn(reportList);

        // ACT
        List<Report> result = reportService.getAllReports();

        // ASSERT
        assertNotNull(result);
        assertEquals(2, result.size(), "Should return exactly 2 reports");
        verify(reportRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("TC-SRV-004: Get all reports - should return empty list when no reports exist")
    void getAllReports_NoReports_ReturnsEmptyList() {
        // ARRANGE
        when(reportRepository.findAll()).thenReturn(Arrays.asList());

        // ACT
        List<Report> result = reportService.getAllReports();

        // ASSERT
        assertNotNull(result);
        assertTrue(result.isEmpty(), "Should return empty list when no reports exist");
    }

    // =========================================================
    // TEST GROUP 3: getReportById()
    // =========================================================

    @Test
    @DisplayName("TC-SRV-005: Get report by ID - should return report when ID exists")
    void getReportById_ExistingId_ReturnsReport() {
        // ARRANGE
        when(reportRepository.findById("test-id-001")).thenReturn(Optional.of(sampleReport));

        // ACT
        Optional<Report> result = reportService.getReportById("test-id-001");

        // ASSERT
        assertTrue(result.isPresent(), "Report should be found");
        assertEquals("test-id-001", result.get().getReportID());
        assertEquals("Suspicious Activity at Gate 2", result.get().getTitle());
    }

    @Test
    @DisplayName("TC-SRV-006: Get report by ID - should return empty when ID does not exist")
    void getReportById_NonExistingId_ReturnsEmpty() {
        // ARRANGE
        when(reportRepository.findById("non-existing-id")).thenReturn(Optional.empty());

        // ACT
        Optional<Report> result = reportService.getReportById("non-existing-id");

        // ASSERT
        assertFalse(result.isPresent(), "Should return empty Optional for non-existing ID");
    }

    // =========================================================
    // TEST GROUP 4: updateReportStatus()
    // =========================================================

    @Test
    @DisplayName("TC-SRV-007: Update status - should update to IN_PROGRESS successfully")
    void updateReportStatus_ValidStatus_UpdatesSuccessfully() {
        // ARRANGE
        when(reportRepository.findById("test-id-001")).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenReturn(sampleReport);

        // ACT
        Optional<Report> result = reportService.updateReportStatus("test-id-001", "IN_PROGRESS");

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("IN_PROGRESS", result.get().getStatus(),
                "Status should be updated to IN_PROGRESS");
    }

    @Test
    @DisplayName("TC-SRV-008: Update status - should update to RESOLVED successfully")
    void updateReportStatus_ResolvedStatus_UpdatesSuccessfully() {
        // ARRANGE
        when(reportRepository.findById("test-id-001")).thenReturn(Optional.of(sampleReport));
        when(reportRepository.save(any(Report.class))).thenReturn(sampleReport);

        // ACT
        Optional<Report> result = reportService.updateReportStatus("test-id-001", "RESOLVED");

        // ASSERT
        assertTrue(result.isPresent());
        assertEquals("RESOLVED", result.get().getStatus());
    }

    @Test
    @DisplayName("TC-SRV-009: Update status - should throw exception for invalid status")
    void updateReportStatus_InvalidStatus_ThrowsIllegalArgumentException() {
        // ACT & ASSERT: Expect an exception to be thrown
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> reportService.updateReportStatus("test-id-001", "INVALID_STATUS"),
                "Should throw IllegalArgumentException for invalid status"
        );

        // Verify the exception message
        assertTrue(exception.getMessage().contains("Invalid status: INVALID_STATUS"));

        // Verify that the repository was NEVER called (validation failed before DB access)
        verify(reportRepository, never()).findById(any());
        verify(reportRepository, never()).save(any());
    }

    @Test
    @DisplayName("TC-SRV-010: Update status - should return empty when report ID not found")
    void updateReportStatus_NonExistingId_ReturnsEmpty() {
        // ARRANGE
        when(reportRepository.findById("non-existing-id")).thenReturn(Optional.empty());

        // ACT
        Optional<Report> result = reportService.updateReportStatus("non-existing-id", "RESOLVED");

        // ASSERT
        assertFalse(result.isPresent(), "Should return empty when report not found");
    }

    // =========================================================
    // TEST GROUP 5: deleteReport()
    // =========================================================

    @Test
    @DisplayName("TC-SRV-011: Delete report - should return true when report exists")
    void deleteReport_ExistingId_ReturnsTrueAndDeletes() {
        // ARRANGE
        when(reportRepository.existsById("test-id-001")).thenReturn(true);
        doNothing().when(reportRepository).deleteById("test-id-001");

        // ACT
        boolean result = reportService.deleteReport("test-id-001");

        // ASSERT
        assertTrue(result, "Should return true when deletion is successful");
        verify(reportRepository, times(1)).deleteById("test-id-001");
    }

    @Test
    @DisplayName("TC-SRV-012: Delete report - should return false when report does not exist")
    void deleteReport_NonExistingId_ReturnsFalse() {
        // ARRANGE
        when(reportRepository.existsById("non-existing-id")).thenReturn(false);

        // ACT
        boolean result = reportService.deleteReport("non-existing-id");

        // ASSERT
        assertFalse(result, "Should return false when report does not exist");
        // Verify deleteById was NEVER called since report doesn't exist
        verify(reportRepository, never()).deleteById(any());
    }
}
