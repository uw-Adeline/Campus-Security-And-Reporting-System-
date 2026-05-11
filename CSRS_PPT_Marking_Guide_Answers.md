# CSRS — PowerPoint Content Based on Marking Guide
## Course: BEST PROGRAMMING PRACTICES AND DESIGN PATTERNS
## Instructor: RUTARINDWA JEAN PIERRE

---

# ══════════════════════════════════════
# PHASE 1 SLIDES
# ══════════════════════════════════════

---

# SLIDE 1 — TITLE

**AUCA Campus Security And Reporting System (CSRS)**

- **Topic:** Campus Security And Reporting System
- **Case Study:** Adventist University of Central Africa (AUCA)
- **Course:** SENG 8240 — Best Programming Practices and Design Patterns
- **Live App:** https://campus-security-and-reporting-syste.vercel.app
- **GitHub:** https://github.com/uw-Adeline/Campus-Security-And-Reporting-System-

---

# SLIDE 2 — Topic and Case Study (Marking Item 1)

**Topic:** Campus Security And Reporting System (CSRS)

**Case Study:** Adventist University of Central Africa (AUCA)

AUCA is a higher education institution in Rwanda with a large campus hosting students, faculty, and staff. The institution needs a digital system to manage and track security incidents reported on campus.

The CSRS provides:
- A **Student Portal** to submit security reports
- An **Admin Dashboard** to manage and resolve reports
- **Anonymous reporting** to protect reporters
- **Real-time status tracking** of all incidents

---

# SLIDE 3 — Functional Diagram (Marking Item 2)

**Internal Working of AUCA CSRS:**

```
Student/Lecturer App
        ↓ Submits Report & Evidence
   API Gateway / Web Server
        ↓
   CSRS Core Backend (Spring Boot)
      ↙           ↓           ↘
MongoDB DB    File Storage   Notification
(Reports)     (Evidence)      Service
                                ↓
                         Push/Email Alerts
                                ↓
                         Student/Admin
```

**Flow:**
1. Student submits report via Vue.js frontend
2. Spring Boot backend validates and processes
3. Data saved to MongoDB Atlas
4. Admin receives notification
5. Admin reviews and updates status

---

# SLIDE 4 — Problem Statement (Marking Item 3)

**Problems faced by AUCA with manual security system:**

1. **Delayed Reporting** — Students must physically find a guard or call an office. Dangerous delays during critical incidents.

2. **No Anonymity** — Fear of retaliation stops students from reporting bullying, vandalism, or suspicious activity.

3. **Lost Evidence** — Photos/videos taken on phones cannot be formally attached to manual reports. Evidence gets lost.

4. **No Centralized Tracking** — Administration cannot track incident status in real time or identify recurring security hotspots.

**Solution:** CSRS — a digital platform solving all 4 problems.

---

# SLIDE 5 — Use Case Diagram (Marking Item 4.1)

**Actors:** Student, Lecturer, Admin

| Actor | Actions |
|-------|---------|
| Student | Login, Submit Report, Upload Evidence, Track Status, Receive Notifications |
| Lecturer | Login, View Reports, Receive Notifications |
| Admin | Login, View Reports, Update Status, Generate Analytics |

*(Show Use Case diagram from Phase_1_Documentation.md)*

---

# SLIDE 6 — Class Diagram (Marking Item 4.2)

**5 Classes with relationships:**

- **Student** — studentID, firstName, lastName, email, phoneNumber
- **Lecturer** — lecturerID, name, email, department
- **Admin** — adminID, username, password, role
- **Report** — reportID, title, category, location, priorityLevel, description, status, isAnonymous
- **Evidence** — evidenceID, fileType, fileName, filePath, uploadedAt

**Relationships:**
- Student **submits** 0..* Reports
- Lecturer **views** 0..* Reports
- Admin **manages** 0..* Reports
- Report **contains** 0..* Evidence (composition)

*(Show Class diagram from Phase_1_Documentation.md)*

---

# SLIDE 7 — Activity Diagram (Marking Item 4.3)

**Two Activity Diagrams:**

**Diagram 1 — Student Submits Report:**
Start → Open App → Fill Form → Anonymous? → Submit → Validate → Save to DB → Success Message → End

**Diagram 2 — Admin Reviews Report:**
Start → Open Dashboard → Fetch Reports → Reports exist? → View Details → Select Status → Update → End

*(Show Activity diagrams from Phase_1_Documentation.md)*

---

# SLIDE 8 — Sequence Diagram (Marking Item 4.4)

**Student submits a report — step by step:**

1. Student fills form in Vue.js UI
2. UI sends POST /api/reports to Spring Boot
3. Spring Boot validates the data
4. Spring Boot saves to MongoDB
5. MongoDB returns saved Report ID
6. Spring Boot returns 201 Created
7. UI shows success message to Student
8. Admin receives notification
9. Admin opens dashboard → GET /api/reports
10. Reports displayed to Admin

*(Show Sequence diagram from Phase_1_Documentation.md)*

---

# SLIDE 9 — Component Diagram (Marking Item 4.5)

**System Components:**

```
Frontend Layer (Vue.js):
  App.vue → ReportForm.vue (Student Portal)
  App.vue → AdminDashboard.vue (Admin Panel)

Backend Layer (Spring Boot):
  ReportController → ReportService → ReportRepository

Data Layer:
  MongoDB Atlas (cloud database)
```

**Communication:**
- Frontend calls Backend via HTTP (Axios)
- Backend reads/writes MongoDB via Spring Data

*(Show Component diagram from Phase_1_Documentation.md)*

---

# ══════════════════════════════════════
# PHASE 2 SLIDES
# ══════════════════════════════════════

---

# SLIDE 10 — Prototype Overview (Marking Item 1)

**What was built:**
A fully functional web application with:
- Student Portal (report submission)
- Admin Dashboard (report management)
- REST API backend
- MongoDB cloud database

**Live Application:**
https://campus-security-and-reporting-syste.vercel.app

**API Documentation (Swagger):**
https://campus-security-and-reporting-system.onrender.com/swagger-ui.html

---

# SLIDE 11 — Screens and Layout (Marking Item 1.1)

**Screen 1 — Student Portal:**
- Navigation bar with "Student Portal" and "Admin Dashboard" buttons
- Report form with fields: Title, Category (dropdown), Location, Priority (dropdown), Description (textarea)
- "Submit Anonymously" checkbox
- "Submit Report" button (blue)
- Success message (green) / Error message (red)

**Screen 2 — Admin Dashboard:**
- "Refresh" button
- Report cards showing: Title, Status badge (color-coded), Category, Location, Priority (color-coded), Date, Description
- Status dropdown per card (PENDING → IN_PROGRESS → RESOLVED → DISMISSED)

---

# SLIDE 12 — Input Processing (Marking Item 1.2)

**Client-side validation (Vue.js):**
- HTML5 `required` attribute on all mandatory fields
- Browser shows "Please fill in this field" if empty
- Form does NOT submit if validation fails

**Server-side validation (Spring Boot):**
```java
@NotBlank(message = "Title is required")
private String title;

@NotBlank(message = "Category is required")
private String category;

@NotBlank(message = "Description is required")
private String description;
```
- `@Valid` annotation triggers validation on POST
- Returns HTTP 400 Bad Request if validation fails

---

# SLIDE 13 — Basic Workflows (Marking Item 1.3)

**Workflow 1 — Submit a Report:**
```
Student opens app
    → Clicks "Student Portal"
    → Fills in all required fields
    → Optionally checks "Submit Anonymously"
    → Clicks "Submit Report"
    → Button shows "Submitting..."
    → POST /api/reports sent to backend
    → Backend validates and saves to MongoDB
    → Returns 201 Created
    → Success message: "Report submitted successfully"
    → Form resets to empty
```

**Workflow 2 — Admin Updates Status:**
```
Admin clicks "Admin Dashboard"
    → GET /api/reports fetches all reports
    → Reports displayed as cards
    → Admin selects new status from dropdown
    → PATCH /api/reports/{id}/status sent
    → Status badge updates immediately
```

---

# SLIDE 14 — Links Between Pages (Marking Item 1.4)

**Navigation between views:**

```vue
<!-- App.vue — navigation buttons -->
<button @click="currentView = 'student'">Student Portal</button>
<button @click="currentView = 'admin'">Admin Dashboard</button>

<!-- Conditional rendering — shows correct view -->
<ReportForm v-if="currentView === 'student'" />
<AdminDashboard v-if="currentView === 'admin'" />
```

**How it works:**
- No page reload needed (Single Page Application)
- Vue.js switches components instantly
- Active button highlighted in blue

---

# SLIDE 15 — User Journeys (Marking Item 1.5)

**Student Journey:**
```
Open App → Student Portal (default) → Fill Report Form
→ Submit → Success Message → Form Resets → Done
```

**Admin Journey:**
```
Open App → Click "Admin Dashboard" → Reports Load
→ Review Report Details → Change Status Dropdown
→ Status Updates Instantly → Review Next Report
```

**Anonymous Reporting Journey:**
```
Student fills form → Checks "Submit Anonymously"
→ isAnonymous = true saved in database
→ No student identity linked to report
→ Admin sees report but not who submitted it
```

---

# SLIDE 16 — Database Responses (Marking Item 1.6)

**Real MongoDB responses — not simulated:**

**POST /api/reports → saves and returns:**
```json
{
  "reportID": "683a1b2c3d4e5f6g7h8i9j0k",
  "title": "Suspicious Activity at Gate 2",
  "category": "Suspicious Activity",
  "location": "Main Gate",
  "priorityLevel": "HIGH",
  "status": "PENDING",
  "submittedAt": "2026-05-06T14:30:00",
  "isAnonymous": true
}
```

**GET /api/reports → returns all reports:**
```json
[
  { "reportID": "...", "title": "...", "status": "PENDING" },
  { "reportID": "...", "title": "...", "status": "RESOLVED" }
]
```

**Empty database returns:** `[]`

---

# SLIDE 17 — Programming Best Practices (Marking Item 2)

**1. Meaningful Names:**
```java
// GOOD — clear and descriptive
public Optional<Report> updateReportStatus(String reportId, String newStatus)
public List<Report> getReportsByStatus(String status)
private boolean isValidStatus(String status)

// BAD — what we avoided
public Optional<Report> upd(String id, String s)
```

**2. Proper Indentation:**
- 4-space indentation throughout
- One statement per line
- Opening braces on same line (Google Java Style)

**3. Single Responsibility:**
- `ReportController` — HTTP only
- `ReportService` — business logic only
- `ReportRepository` — database only

**4. Comments:**
```java
/**
 * Updates the status of an existing report.
 * Business logic: Validates that the new status is one of the allowed values.
 * @param reportId the unique identifier of the report
 * @param newStatus the new status to set
 * @throws IllegalArgumentException if the status is invalid
 */
```

---

# SLIDE 18 — Design Pattern (Marking Item 3)

**Pattern Used: Service Layer Pattern**

**What it is:**
Separates application into 3 layers, each with ONE responsibility.

**How we applied it:**

| Layer | Class | Responsibility |
|-------|-------|---------------|
| Controller | `ReportController` | Handle HTTP requests/responses ONLY |
| Service | `ReportService` | Business logic and validation ONLY |
| Repository | `ReportRepository` | Database operations ONLY |

**Key business rule in Service Layer:**
```java
// Only PENDING, IN_PROGRESS, RESOLVED, DISMISSED are valid
private boolean isValidStatus(String status) {
    return status.equals("PENDING") ||
           status.equals("IN_PROGRESS") ||
           status.equals("RESOLVED") ||
           status.equals("DISMISSED");
}
```

**Why this pattern?**
- Controller doesn't know about database
- Service doesn't know about HTTP
- Repository doesn't know about business rules
- Each layer can be tested independently

---

# ══════════════════════════════════════
# PHASE 3 SLIDES
# ══════════════════════════════════════

---

# SLIDE 19 — What is Docker? (Marking Item 1)

**Definition:**
Docker packages an application and ALL its dependencies into a **container** — a portable unit that runs identically on any machine.

**Without Docker — someone needs to:**
1. Install Java 17 manually
2. Install Node.js 20 manually
3. Install MongoDB manually
4. Configure environment variables
5. Run 3 separate commands

**With Docker — one command:**
```bash
docker-compose up --build
```
Everything starts automatically.

---

# SLIDE 20 — How We Dockerized CSRS (Marking Item 1)

**3 Dockerfiles created:**

**1. csrs-backend/Dockerfile (Spring Boot)**
- Stage 1: Maven compiles Java → produces .jar
- Stage 2: Lightweight JRE runs the .jar
- Result: 200MB image (not 600MB)

**2. csrs-frontend/Dockerfile (Vue.js)**
- Stage 1: Node.js builds Vue → produces /dist
- Stage 2: Nginx serves static files + proxies API

**3. docker-compose.yml**
- Starts MongoDB, Backend, Frontend together
- Containers communicate via `csrs-network`
- MongoDB data persists via `mongodb_data` volume

---

# SLIDE 21 — docker-compose Architecture (Marking Item 1)

```
docker-compose up --build
        ↓
┌───────────────────────────────────┐
│  1. MongoDB starts first          │
│     image: mongo:7.0              │
│     port: 27017                   │
├───────────────────────────────────┤
│  2. Backend starts (after MongoDB)│
│     build: csrs-backend/          │
│     port: 8080                    │
│     connects to: mongodb:27017    │
├───────────────────────────────────┤
│  3. Frontend starts (after Backend│
│     build: csrs-frontend/         │
│     port: 80                      │
│     proxies /api/ → backend:8080  │
└───────────────────────────────────┘
```

Access: http://localhost (port 80)

---

# SLIDE 22 — Version Control with Git (Marking Item 2)

**Git was installed and configured:**

```bash
# 1. Initialize repository
git init

# 2. Configure identity
git config user.name "AUCA Student"
git config user.email "student@auca.ac.rw"

# 3. Create .gitignore (excludes generated files)
# target/, node_modules/, .env

# 4. Stage all source files
git add .

# 5. Commit with message
git commit -m "Phase 1: System analysis and design"

# 6. Push to GitHub
git push -u origin master
```

**Repository captures ALL parts of the application:**
- Backend Java source code
- Frontend Vue.js components
- Configuration files
- Documentation
- Dockerfiles
- Test files

---

# SLIDE 23 — Git Commit History (Marking Item 2)

**All commits showing project progression:**

```
0e68dc0  Add final submission document
4d76fa9  Fix MongoDB connection string for production
419913a  Update deployment config
ed5bbf8  Phase 3: Configure Docker and deployment
f0a37c2  Phase 4: Add JUnit tests and test plan
c702ef0  Phase 1: System analysis and design
```

**GitHub Repository (public):**
https://github.com/uw-Adeline/Campus-Security-And-Reporting-System-

**What .gitignore excludes:**
- `target/` — compiled Java (auto-generated)
- `node_modules/` — npm packages (300MB+)
- `.env` — passwords and secrets

---

# ══════════════════════════════════════
# PHASE 4 SLIDES
# ══════════════════════════════════════

---

# SLIDE 24 — Testing Goals (Marking Item 1.1)

**Goals of testing — aligned with system requirements:**

| Goal | System Requirement It Verifies |
|------|-------------------------------|
| Verify POST /api/reports creates report | Students can submit reports |
| Verify invalid input returns 400 | System rejects incomplete reports |
| Verify status validation | Only valid statuses allowed |
| Verify GET /api/reports returns all | Admin can view all reports |
| Verify PATCH updates status | Admin can manage reports |
| Verify DELETE removes report | Admin can remove reports |
| Verify anonymous submission | Students can report anonymously |

**All goals directly align with Phase 1 Use Case Diagram requirements.**

---

# SLIDE 25 — Features Tested (Marking Item 1.2)

**Features clearly identified for testing:**

| Feature | Test Type | Test IDs |
|---------|-----------|---------|
| Create Report | Unit + Integration | TC-SRV-001, TC-CTRL-001 |
| Input Validation | Unit + Integration | TC-SRV-002, TC-CTRL-002 |
| Get All Reports | Unit + Integration | TC-SRV-003, TC-CTRL-003 |
| Get Report by ID | Unit + Integration | TC-SRV-005, TC-CTRL-005 |
| Update Status | Unit + Integration | TC-SRV-007, TC-CTRL-007 |
| Invalid Status | Unit + Integration | TC-SRV-009, TC-CTRL-008 |
| Delete Report | Unit + Integration | TC-SRV-011, TC-CTRL-010 |
| Submit Form (UI) | Manual | TC-UI-001 |
| Admin Dashboard | Manual | TC-UI-003, TC-UI-004 |
| Anonymous Report | Manual | TC-UI-006 |

---

# SLIDE 26 — Test Cases (Marking Item 1.3)

**Example test case — TC-SRV-009 (Critical):**

| Field | Details |
|-------|---------|
| Test ID | TC-SRV-009 |
| Feature | Status validation business rule |
| Steps | Call updateReportStatus("id", "INVALID_STATUS") |
| Expected | IllegalArgumentException thrown |
| Expected | Repository never accessed |
| Actual | ✅ Exception thrown with message "Invalid status: INVALID_STATUS" |
| Result | ✅ PASS |

**Example test case — TC-CTRL-002 (Critical):**

| Field | Details |
|-------|---------|
| Test ID | TC-CTRL-002 |
| Feature | Input validation on POST endpoint |
| Steps | POST /api/reports with no title field |
| Expected | HTTP 400 Bad Request |
| Actual | ✅ HTTP 400 returned |
| Result | ✅ PASS |

---

# SLIDE 27 — Tools for Tracking Issues (Marking Item 1.4)

**Tools used:**

| Tool | Purpose |
|------|---------|
| **JUnit 5** | Write and run automated test cases |
| **Mockito** | Mock dependencies (fake repository — no real DB needed) |
| **Spring MockMvc** | Simulate HTTP requests to test API endpoints |
| **Maven Surefire** | Generate test reports automatically |

**Test reports generated at:**
```
csrs-backend/target/surefire-reports/
├── ReportServiceTest.txt
└── ReportControllerTest.txt
```

**Report content:**
```
Tests run: 12, Failures: 0, Errors: 0  ← ServiceTest
Tests run: 11, Failures: 0, Errors: 0  ← ControllerTest
```

---

# SLIDE 28 — Live Test Results

**Run this command live during presentation:**
```bash
cd csrs-backend
mvn test
```

**Actual output:**
```
-------------------------------------------------------
 T E S T S
-------------------------------------------------------
Test set: ReportServiceTest
Tests run: 12, Failures: 0, Errors: 0, Skipped: 0

Test set: ReportControllerTest
Tests run: 11, Failures: 0, Errors: 0, Skipped: 0

Results:
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS
```

**Total: 23 automated + 6 manual = 29 tests — 100% PASS RATE**

---

# SLIDE 29 — CONCLUSION

**All 4 phases completed successfully:**

| Phase | What Was Delivered | Status |
|-------|-------------------|--------|
| Phase 1 | Topic, case study, 5 UML diagrams, problem analysis | ✅ Done |
| Phase 2 | Working prototype, Service Layer Pattern, coding standards | ✅ Done |
| Phase 3 | Docker containers, docker-compose, Git version control | ✅ Done |
| Phase 4 | 29 test cases, 100% pass rate, test plan document | ✅ Done |

**Live Application:**
https://campus-security-and-reporting-syste.vercel.app

**Source Code:**
https://github.com/uw-Adeline/Campus-Security-And-Reporting-System-

---
*Thank you*
