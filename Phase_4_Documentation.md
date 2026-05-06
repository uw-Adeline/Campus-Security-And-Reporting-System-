# PHASE 4: Software Test Plan

**Project:** AUCA Campus Security And Reporting System (CSRS)
**Course:** Best Programming Practices and Design Patterns
**Institution:** Adventist University of Central Africa (AUCA)

---

## 1. Introduction

### 1.1 Purpose
This document defines the complete software test plan for the CSRS application. It describes what will be tested, how it will be tested, who is responsible for testing, and the expected outcomes. The goal is to verify that the system meets all functional requirements defined in Phase 1 and implemented in Phase 2.

### 1.2 Scope
Testing covers the following components of the CSRS application:
- **Backend REST API** (Spring Boot) — Service Layer and Controller Layer
- **Frontend UI** (Vue.js) — Form validation and Admin Dashboard behavior
- **Integration** — Communication between frontend, backend, and MongoDB

### 1.3 Testing Objectives
1. Verify all API endpoints return correct HTTP status codes
2. Verify business logic in the Service Layer works correctly
3. Verify input validation rejects invalid data
4. Verify the Admin can update report statuses
5. Verify anonymous report submission works
6. Verify error handling works when resources are not found

---

## 2. Test Strategy

### 2.1 Types of Testing Used

| Test Type | Description | Tool Used | Layer Tested |
|-----------|-------------|-----------|--------------|
| **Unit Testing** | Tests a single class/method in isolation | JUnit 5 + Mockito | Service Layer |
| **Integration Testing** | Tests how layers work together | Spring MockMvc | Controller + Service |
| **Manual Testing** | Human tester follows test cases | Browser + Swagger UI | Full Application |
| **Validation Testing** | Tests that invalid input is rejected | JUnit 5 + MockMvc | Controller + Model |

### 2.2 Testing Approach

We follow the **AAA Pattern** for all automated tests:
- **Arrange** — Set up test data and mock dependencies
- **Act** — Call the method or endpoint being tested
- **Assert** — Verify the result matches expectations

### 2.3 What We Do NOT Test
- MongoDB internal operations (tested by Spring Data team)
- Vue.js framework internals (tested by Vue.js team)
- Third-party libraries

---

## 3. Test Environment

### 3.1 Automated Test Environment

| Component | Technology | Version |
|-----------|-----------|---------|
| Test Framework | JUnit 5 | 5.10.x (via Spring Boot) |
| Mocking Framework | Mockito | 5.x (via Spring Boot) |
| Web Layer Testing | Spring MockMvc | 6.x |
| Build Tool | Maven | 3.9.6 |
| Java Version | Java | 17 |

### 3.2 Manual Test Environment

| Component | Details |
|-----------|---------|
| Browser | Google Chrome (latest) |
| Frontend URL | http://localhost:5173 (dev) or http://localhost (Docker) |
| Backend URL | http://localhost:8080 |
| API Documentation | http://localhost:8080/swagger-ui.html |
| Database | MongoDB Atlas (cloud) or local Docker container |

### 3.3 How to Run Automated Tests

```bash
# Navigate to backend folder
cd csrs-backend

# Run all tests
mvn test

# Run only service tests
mvn test -Dtest=ReportServiceTest

# Run only controller tests
mvn test -Dtest=ReportControllerTest

# Run tests and generate report
mvn test surefire-report:report
```

---

## 4. Test Cases — Service Layer (Unit Tests)

These tests are in: `src/test/java/.../service/ReportServiceTest.java`

They test the **business logic** in `ReportService.java` without connecting to a real database. Mockito is used to simulate the repository.

---

### TC-SRV-001: Create Report — Valid Report Returns Saved Report

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-001 |
| **Test Name** | createReport_ValidReport_ReturnsSavedReport |
| **Component** | ReportService.createReport() |
| **Type** | Unit Test |
| **Priority** | High |

**Preconditions:** Repository mock is configured to return the sample report on save

**Test Steps:**
1. Create a valid Report object with title, category, location, priority, description
2. Configure mock: `when(repository.save(any())).thenReturn(sampleReport)`
3. Call `reportService.createReport(sampleReport)`
4. Assert result is not null
5. Assert title equals "Suspicious Activity at Gate 2"
6. Assert status equals "PENDING"
7. Verify `repository.save()` was called exactly once

**Expected Result:** Report is saved and returned with status PENDING
**Actual Result:** ✅ PASS

---

### TC-SRV-002: Create Report — Null Status Gets Default PENDING

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-002 |
| **Test Name** | createReport_NullStatus_SetsDefaultPendingStatus |
| **Component** | ReportService.createReport() |
| **Type** | Unit Test |
| **Priority** | High |

**Test Steps:**
1. Create a Report with `status = null`
2. Call `reportService.createReport(report)`
3. Assert that status was set to "PENDING" before saving

**Expected Result:** Status defaults to "PENDING" when null
**Actual Result:** ✅ PASS

---

### TC-SRV-003: Get All Reports — Returns Full List

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-003 |
| **Test Name** | getAllReports_ReportsExist_ReturnsAllReports |
| **Component** | ReportService.getAllReports() |
| **Type** | Unit Test |
| **Priority** | High |

**Test Steps:**
1. Configure mock to return a list of 2 reports
2. Call `reportService.getAllReports()`
3. Assert list is not null
4. Assert list size equals 2

**Expected Result:** Returns list with exactly 2 reports
**Actual Result:** ✅ PASS

---

### TC-SRV-004: Get All Reports — Returns Empty List When No Reports

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-004 |
| **Test Name** | getAllReports_NoReports_ReturnsEmptyList |
| **Component** | ReportService.getAllReports() |
| **Type** | Unit Test |
| **Priority** | Medium |

**Test Steps:**
1. Configure mock to return empty list
2. Call `reportService.getAllReports()`
3. Assert result is not null
4. Assert result is empty

**Expected Result:** Returns empty list (not null)
**Actual Result:** ✅ PASS

---

### TC-SRV-005: Get Report By ID — Returns Report When Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-005 |
| **Test Name** | getReportById_ExistingId_ReturnsReport |
| **Component** | ReportService.getReportById() |
| **Type** | Unit Test |
| **Priority** | High |

**Test Steps:**
1. Configure mock: `findById("test-id-001")` returns `Optional.of(sampleReport)`
2. Call `reportService.getReportById("test-id-001")`
3. Assert Optional is present
4. Assert report ID equals "test-id-001"

**Expected Result:** Returns Optional containing the report
**Actual Result:** ✅ PASS

---

### TC-SRV-006: Get Report By ID — Returns Empty When Not Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-006 |
| **Test Name** | getReportById_NonExistingId_ReturnsEmpty |
| **Component** | ReportService.getReportById() |
| **Type** | Unit Test |
| **Priority** | High |

**Test Steps:**
1. Configure mock: `findById("non-existing-id")` returns `Optional.empty()`
2. Call `reportService.getReportById("non-existing-id")`
3. Assert Optional is NOT present

**Expected Result:** Returns empty Optional
**Actual Result:** ✅ PASS

---

### TC-SRV-007: Update Status — Valid Status Updates Successfully

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-007 |
| **Test Name** | updateReportStatus_ValidStatus_UpdatesSuccessfully |
| **Component** | ReportService.updateReportStatus() |
| **Type** | Unit Test |
| **Priority** | High |

**Test Steps:**
1. Configure mock to return sampleReport on findById and save
2. Call `reportService.updateReportStatus("test-id-001", "IN_PROGRESS")`
3. Assert Optional is present
4. Assert status equals "IN_PROGRESS"

**Expected Result:** Status updated to IN_PROGRESS
**Actual Result:** ✅ PASS

---

### TC-SRV-008: Update Status — RESOLVED Status Works

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-008 |
| **Test Name** | updateReportStatus_ResolvedStatus_UpdatesSuccessfully |
| **Component** | ReportService.updateReportStatus() |
| **Type** | Unit Test |
| **Priority** | High |

**Test Steps:**
1. Call `reportService.updateReportStatus("test-id-001", "RESOLVED")`
2. Assert status equals "RESOLVED"

**Expected Result:** Status updated to RESOLVED
**Actual Result:** ✅ PASS

---

### TC-SRV-009: Update Status — Invalid Status Throws Exception

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-009 |
| **Test Name** | updateReportStatus_InvalidStatus_ThrowsIllegalArgumentException |
| **Component** | ReportService.updateReportStatus() — Business Logic Validation |
| **Type** | Unit Test |
| **Priority** | Critical |

**Test Steps:**
1. Call `reportService.updateReportStatus("test-id-001", "INVALID_STATUS")`
2. Assert `IllegalArgumentException` is thrown
3. Assert exception message contains "Invalid status: INVALID_STATUS"
4. Verify repository `findById()` was NEVER called (validation failed before DB access)
5. Verify repository `save()` was NEVER called

**Expected Result:** Exception thrown, database never accessed
**Actual Result:** ✅ PASS

---

### TC-SRV-010: Update Status — Returns Empty When Report Not Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-010 |
| **Test Name** | updateReportStatus_NonExistingId_ReturnsEmpty |
| **Component** | ReportService.updateReportStatus() |
| **Type** | Unit Test |
| **Priority** | Medium |

**Test Steps:**
1. Configure mock: `findById("non-existing-id")` returns `Optional.empty()`
2. Call `reportService.updateReportStatus("non-existing-id", "RESOLVED")`
3. Assert result is empty Optional

**Expected Result:** Returns empty Optional when report not found
**Actual Result:** ✅ PASS

---

### TC-SRV-011: Delete Report — Returns True When Deleted

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-011 |
| **Test Name** | deleteReport_ExistingId_ReturnsTrueAndDeletes |
| **Component** | ReportService.deleteReport() |
| **Type** | Unit Test |
| **Priority** | Medium |

**Test Steps:**
1. Configure mock: `existsById("test-id-001")` returns true
2. Call `reportService.deleteReport("test-id-001")`
3. Assert result is true
4. Verify `deleteById()` was called exactly once

**Expected Result:** Returns true, deleteById called once
**Actual Result:** ✅ PASS

---

### TC-SRV-012: Delete Report — Returns False When Not Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-SRV-012 |
| **Test Name** | deleteReport_NonExistingId_ReturnsFalse |
| **Component** | ReportService.deleteReport() |
| **Type** | Unit Test |
| **Priority** | Medium |

**Test Steps:**
1. Configure mock: `existsById("non-existing-id")` returns false
2. Call `reportService.deleteReport("non-existing-id")`
3. Assert result is false
4. Verify `deleteById()` was NEVER called

**Expected Result:** Returns false, deleteById never called
**Actual Result:** ✅ PASS

---

## 5. Test Cases — Controller Layer (Integration Tests)

These tests are in: `src/test/java/.../controller/ReportControllerTest.java`

They test the **HTTP layer** using Spring MockMvc. The service is mocked so these tests focus purely on HTTP request/response behavior.

---

### TC-CTRL-001: POST /api/reports — Valid Request Returns 201

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-001 |
| **Endpoint** | POST /api/reports |
| **Type** | Integration Test |
| **Priority** | Critical |

**Request Body:**
```json
{
  "title": "Suspicious Activity at Gate 2",
  "category": "Suspicious Activity",
  "location": "Main Gate",
  "priorityLevel": "HIGH",
  "description": "Unknown person seen near gate",
  "isAnonymous": true
}
```

**Expected Response:** HTTP 201 Created
```json
{
  "reportID": "test-id-001",
  "title": "Suspicious Activity at Gate 2",
  "status": "PENDING"
}
```
**Actual Result:** ✅ PASS

---

### TC-CTRL-002: POST /api/reports — Missing Title Returns 400

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-002 |
| **Endpoint** | POST /api/reports |
| **Type** | Validation Test |
| **Priority** | Critical |

**Request Body (invalid — no title):**
```json
{
  "category": "Theft",
  "location": "Library",
  "description": "Something was stolen"
}
```

**Expected Response:** HTTP 400 Bad Request
**Why:** `@NotBlank` annotation on `title` field rejects empty/null values

**Actual Result:** ✅ PASS

---

### TC-CTRL-003: GET /api/reports — Returns All Reports With 200

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-003 |
| **Endpoint** | GET /api/reports |
| **Type** | Integration Test |
| **Priority** | High |

**Expected Response:** HTTP 200 OK with array of 2 reports
```json
[
  { "title": "Suspicious Activity at Gate 2" },
  { "title": "Theft in Library" }
]
```
**Actual Result:** ✅ PASS

---

### TC-CTRL-004: GET /api/reports — Empty List Returns 200

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-004 |
| **Endpoint** | GET /api/reports |
| **Type** | Integration Test |
| **Priority** | Medium |

**Expected Response:** HTTP 200 OK with empty array `[]`
**Note:** Returns 200 (not 404) because the endpoint itself works — there's just no data

**Actual Result:** ✅ PASS

---

### TC-CTRL-005: GET /api/reports/{id} — Returns Report When Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-005 |
| **Endpoint** | GET /api/reports/test-id-001 |
| **Type** | Integration Test |
| **Priority** | High |

**Expected Response:** HTTP 200 OK with report object
**Actual Result:** ✅ PASS

---

### TC-CTRL-006: GET /api/reports/{id} — Returns 404 When Not Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-006 |
| **Endpoint** | GET /api/reports/non-existing-id |
| **Type** | Integration Test |
| **Priority** | High |

**Expected Response:** HTTP 404 Not Found
**Actual Result:** ✅ PASS

---

### TC-CTRL-007: PATCH /api/reports/{id}/status — Updates to RESOLVED

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-007 |
| **Endpoint** | PATCH /api/reports/test-id-001/status?status=RESOLVED |
| **Type** | Integration Test |
| **Priority** | High |

**Expected Response:** HTTP 200 OK with updated report
```json
{ "status": "RESOLVED" }
```
**Actual Result:** ✅ PASS

---

### TC-CTRL-008: PATCH /api/reports/{id}/status — Invalid Status Returns 400

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-008 |
| **Endpoint** | PATCH /api/reports/test-id-001/status?status=INVALID |
| **Type** | Validation Test |
| **Priority** | Critical |

**Expected Response:** HTTP 400 Bad Request
**Why:** Service throws `IllegalArgumentException` for invalid status, controller catches it and returns 400

**Actual Result:** ✅ PASS

---

### TC-CTRL-009: PATCH /api/reports/{id}/status — 404 When Report Not Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-009 |
| **Endpoint** | PATCH /api/reports/non-existing-id/status?status=RESOLVED |
| **Type** | Integration Test |
| **Priority** | Medium |

**Expected Response:** HTTP 404 Not Found
**Actual Result:** ✅ PASS

---

### TC-CTRL-010: DELETE /api/reports/{id} — Returns 204 When Deleted

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-010 |
| **Endpoint** | DELETE /api/reports/test-id-001 |
| **Type** | Integration Test |
| **Priority** | Medium |

**Expected Response:** HTTP 204 No Content
**Actual Result:** ✅ PASS

---

### TC-CTRL-011: DELETE /api/reports/{id} — Returns 404 When Not Found

| Field | Details |
|-------|---------|
| **Test ID** | TC-CTRL-011 |
| **Endpoint** | DELETE /api/reports/non-existing-id |
| **Type** | Integration Test |
| **Priority** | Medium |

**Expected Response:** HTTP 404 Not Found
**Actual Result:** ✅ PASS

---

## 6. Manual Test Cases — Frontend UI

These tests are performed manually by a tester using a web browser.

---

### TC-UI-001: Student Submits a Valid Report

| Field | Details |
|-------|---------|
| **Test ID** | TC-UI-001 |
| **Component** | ReportForm.vue |
| **Type** | Manual Test |
| **Priority** | Critical |

**Preconditions:** Backend is running at localhost:8080, frontend at localhost:5173

**Test Steps:**
1. Open browser and navigate to `http://localhost:5173`
2. Click "Student Portal" button
3. Fill in "Incident Title": "Suspicious person near parking"
4. Select Category: "Suspicious Activity"
5. Fill in Location: "Parking Lot B"
6. Select Priority: "HIGH"
7. Fill in Description: "Unknown person walking around cars"
8. Check "Submit Anonymously" checkbox
9. Click "Submit Report" button

**Expected Result:**
- Button shows "Submitting..." while request is in progress
- Success message appears: "Report submitted successfully. Thank you for keeping AUCA safe."
- Form fields are cleared/reset

**Pass Criteria:** Report appears in Admin Dashboard after submission

---

### TC-UI-002: Student Submits With Missing Required Fields

| Field | Details |
|-------|---------|
| **Test ID** | TC-UI-002 |
| **Component** | ReportForm.vue |
| **Type** | Manual Test |
| **Priority** | High |

**Test Steps:**
1. Open Student Portal
2. Leave "Incident Title" empty
3. Click "Submit Report"

**Expected Result:**
- Browser shows native HTML5 validation message: "Please fill in this field"
- Form is NOT submitted
- No API call is made

---

### TC-UI-003: Admin Views All Reports

| Field | Details |
|-------|---------|
| **Test ID** | TC-UI-003 |
| **Component** | AdminDashboard.vue |
| **Type** | Manual Test |
| **Priority** | Critical |

**Preconditions:** At least one report exists in the database

**Test Steps:**
1. Click "Admin Dashboard" button in navigation
2. Wait for reports to load

**Expected Result:**
- Loading message shown briefly
- Reports displayed as cards sorted newest first
- Each card shows: title, category, location, priority (color-coded), date, description
- Status badge shown with correct color (PENDING=yellow, IN_PROGRESS=blue, RESOLVED=green)

---

### TC-UI-004: Admin Updates Report Status

| Field | Details |
|-------|---------|
| **Test ID** | TC-UI-004 |
| **Component** | AdminDashboard.vue |
| **Type** | Manual Test |
| **Priority** | Critical |

**Test Steps:**
1. Open Admin Dashboard
2. Find a report with status "PENDING"
3. Click the status dropdown on that report
4. Select "IN_PROGRESS"

**Expected Result:**
- Status badge on the card updates immediately to "IN_PROGRESS"
- No page refresh needed
- Change is persisted (refresh page — status still shows IN_PROGRESS)

---

### TC-UI-005: Admin Dashboard Shows Empty State

| Field | Details |
|-------|---------|
| **Test ID** | TC-UI-005 |
| **Component** | AdminDashboard.vue |
| **Type** | Manual Test |
| **Priority** | Low |

**Preconditions:** No reports in the database

**Test Steps:**
1. Open Admin Dashboard

**Expected Result:**
- Message displayed: "No reports found in the system."
- No error or crash

---

### TC-UI-006: Anonymous Report Submission

| Field | Details |
|-------|---------|
| **Test ID** | TC-UI-006 |
| **Component** | ReportForm.vue |
| **Type** | Manual Test |
| **Priority** | High |

**Test Steps:**
1. Fill in all required fields
2. Ensure "Submit Anonymously" checkbox IS checked
3. Submit the report
4. Open Admin Dashboard and find the report

**Expected Result:**
- Report is saved with `isAnonymous: true`
- No student identity linked to the report

---

## 7. Test Summary Table

| Test ID | Test Name | Type | Priority | Status |
|---------|-----------|------|----------|--------|
| TC-SRV-001 | Create valid report | Unit | High | ✅ PASS |
| TC-SRV-002 | Default PENDING status | Unit | High | ✅ PASS |
| TC-SRV-003 | Get all reports | Unit | High | ✅ PASS |
| TC-SRV-004 | Get empty list | Unit | Medium | ✅ PASS |
| TC-SRV-005 | Get report by ID (found) | Unit | High | ✅ PASS |
| TC-SRV-006 | Get report by ID (not found) | Unit | High | ✅ PASS |
| TC-SRV-007 | Update status to IN_PROGRESS | Unit | High | ✅ PASS |
| TC-SRV-008 | Update status to RESOLVED | Unit | High | ✅ PASS |
| TC-SRV-009 | Invalid status throws exception | Unit | Critical | ✅ PASS |
| TC-SRV-010 | Update status — report not found | Unit | Medium | ✅ PASS |
| TC-SRV-011 | Delete existing report | Unit | Medium | ✅ PASS |
| TC-SRV-012 | Delete non-existing report | Unit | Medium | ✅ PASS |
| TC-CTRL-001 | POST valid report → 201 | Integration | Critical | ✅ PASS |
| TC-CTRL-002 | POST missing title → 400 | Validation | Critical | ✅ PASS |
| TC-CTRL-003 | GET all reports → 200 | Integration | High | ✅ PASS |
| TC-CTRL-004 | GET empty list → 200 | Integration | Medium | ✅ PASS |
| TC-CTRL-005 | GET by ID (found) → 200 | Integration | High | ✅ PASS |
| TC-CTRL-006 | GET by ID (not found) → 404 | Integration | High | ✅ PASS |
| TC-CTRL-007 | PATCH status → 200 | Integration | High | ✅ PASS |
| TC-CTRL-008 | PATCH invalid status → 400 | Validation | Critical | ✅ PASS |
| TC-CTRL-009 | PATCH report not found → 404 | Integration | Medium | ✅ PASS |
| TC-CTRL-010 | DELETE existing → 204 | Integration | Medium | ✅ PASS |
| TC-CTRL-011 | DELETE not found → 404 | Integration | Medium | ✅ PASS |
| TC-UI-001 | Student submits valid report | Manual | Critical | ✅ PASS |
| TC-UI-002 | Submit with missing fields | Manual | High | ✅ PASS |
| TC-UI-003 | Admin views all reports | Manual | Critical | ✅ PASS |
| TC-UI-004 | Admin updates report status | Manual | Critical | ✅ PASS |
| TC-UI-005 | Empty state message | Manual | Low | ✅ PASS |
| TC-UI-006 | Anonymous submission | Manual | High | ✅ PASS |

**Total Tests: 28**
**Passed: 28**
**Failed: 0**
**Pass Rate: 100%**

---

## 8. Test Coverage Summary

| Layer | Classes Tested | Methods Tested | Coverage |
|-------|---------------|----------------|----------|
| Service Layer | ReportService | createReport, getAllReports, getReportById, updateReportStatus, deleteReport | 100% |
| Controller Layer | ReportController | POST, GET all, GET by ID, PATCH status, DELETE | 100% |
| Frontend UI | ReportForm, AdminDashboard | Submit, Fetch, Update Status | Manual |

---

## 9. Tools and Frameworks Explained

### JUnit 5
The standard Java testing framework. Each `@Test` method is an independent test case. `@BeforeEach` runs setup code before every test.

```java
@Test
@DisplayName("TC-SRV-001: Create report returns PENDING status")
void createReport_ValidReport_ReturnsSavedReport() {
    // Arrange, Act, Assert
}
```

### Mockito
A mocking framework that creates fake versions of dependencies. This lets us test the Service Layer without a real database.

```java
@Mock
private ReportRepository reportRepository; // Fake repository

// Tell the fake what to return
when(reportRepository.save(any())).thenReturn(sampleReport);

// Verify it was called
verify(reportRepository, times(1)).save(sampleReport);
```

### Spring MockMvc
Simulates HTTP requests to test controllers without starting a real server.

```java
mockMvc.perform(post("/api/reports")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(report)))
    .andExpect(status().isCreated())
    .andExpect(jsonPath("$.status").value("PENDING"));
```

---

## 10. Test Schedule

| Phase | Activity | When |
|-------|----------|------|
| Development | Write unit tests alongside code | During Phase 2 |
| Pre-submission | Run all automated tests | Before submission |
| Demo | Run `mvn test` live to show results | During presentation |
| Manual testing | Test UI in browser | Before submission |

---

## 11. Conclusion

The CSRS application has been thoroughly tested across all layers:

- **12 Unit Tests** verify the Service Layer business logic in isolation
- **11 Integration Tests** verify the REST API endpoints return correct HTTP responses
- **6 Manual Tests** verify the Vue.js frontend behaves correctly in a browser

All 28 test cases pass successfully. The test suite covers all critical paths including happy paths (valid input), error paths (invalid input, missing resources), and edge cases (null status, empty lists). The use of Mockito ensures tests run fast without requiring a database connection, making them suitable for continuous integration pipelines.
