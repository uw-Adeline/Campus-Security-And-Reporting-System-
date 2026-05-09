# CSRS — Presentation Slides Content
## Best Programming Practices and Design Patterns
### Instructor: RUTARINDWA JEAN PIERRE

---

# SLIDE 1 — TITLE SLIDE

**AUCA Campus Security And Reporting System**
**(CSRS)**

---

- **Course:** Best Programming Practices and Design Patterns
- **Institution:** Adventist University of Central Africa (AUCA)
- **Topic:** Campus Security And Reporting System
- **Live App:** https://campus-security-and-reporting-syste.vercel.app
- **Source Code:** https://github.com/uw-Adeline/Campus-Security-And-Reporting-System-

---

# ============================================================
# PHASE 2 — SOFTWARE DEVELOPMENT PROTOTYPE
# ============================================================

---

# SLIDE 2 — What is a Software Prototype?

**Definition:**
A software prototype is an early, working version of an application used to:
- Test concepts and validate assumptions
- Gather feedback before full development
- Identify design flaws early
- Reduce development risks

**Our Prototype:**
The CSRS prototype is a **fully functional web application** that allows:
- Students to submit security incident reports
- Admins to review and manage those reports in real time

---

# SLIDE 3 — Technology Stack

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Backend** | Spring Boot 3.2.5 | REST API server |
| **Frontend** | Vue.js 3 | User interface |
| **Database** | MongoDB Atlas | Data storage |
| **Build Tool** | Maven | Backend build |
| **HTTP Client** | Axios | API communication |
| **API Docs** | Swagger/OpenAPI | Auto documentation |
| **Java Version** | Java 17 | Backend runtime |

---

# SLIDE 4 — System Architecture

```
┌─────────────────────────────────────────────┐
│           Vue.js Frontend                   │
│   ReportForm.vue  |  AdminDashboard.vue      │
└──────────────────┬──────────────────────────┘
                   │ HTTP (Axios)
                   ▼
┌─────────────────────────────────────────────┐
│         Spring Boot Backend                 │
│  Controller → Service → Repository          │
└──────────────────┬──────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────────────┐
│         MongoDB Atlas (Cloud)               │
│  reports | students | admins | lecturers    │
└─────────────────────────────────────────────┘
```

---

# SLIDE 5 — Design Pattern: Service Layer Pattern

**What is the Service Layer Pattern?**
A structural pattern that separates application logic into 3 distinct layers, each with one responsibility.

```
┌──────────────────────────────────────┐
│  CONTROLLER LAYER (ReportController) │
│  → Handles HTTP requests only        │
│  → Returns HTTP responses            │
├──────────────────────────────────────┤
│  SERVICE LAYER (ReportService)       │
│  → Contains ALL business logic       │
│  → Validates data                    │
│  → Enforces business rules           │
├──────────────────────────────────────┤
│  REPOSITORY LAYER (ReportRepository) │
│  → Handles database operations only  │
│  → CRUD operations on MongoDB        │
└──────────────────────────────────────┘
```

---

# SLIDE 6 — Design Pattern: How We Applied It

**Before (Bad Practice — No Service Layer):**
```java
// Controller doing everything — WRONG
@PostMapping
public Report createReport(@RequestBody Report report) {
    report.setStatus("PENDING"); // business logic in controller
    return reportRepository.save(report); // direct DB access
}
```

**After (Good Practice — With Service Layer):**
```java
// Controller — only handles HTTP
@PostMapping
public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) {
    Report saved = reportService.createReport(report); // delegates to service
    return new ResponseEntity<>(saved, HttpStatus.CREATED);
}

// Service — handles business logic
public Report createReport(Report report) {
    if (report.getStatus() == null) {
        report.setStatus("PENDING"); // business rule here
    }
    return reportRepository.save(report);
}
```

**Benefits:**
- Each layer has ONE clear responsibility
- Business logic is reusable and testable
- Easy to maintain and extend

---

# SLIDE 7 — Google Java Coding Standards Applied

| Standard | How We Applied It |
|----------|------------------|
| **PascalCase for classes** | `ReportService`, `ReportController` |
| **camelCase for methods** | `createReport()`, `updateReportStatus()` |
| **Constructor injection** | Dependencies injected via constructor |
| **Javadoc comments** | All public methods documented |
| **Bean Validation** | `@NotBlank`, `@Email` on model fields |
| **RESTful URLs** | `/api/reports` not `/api/getReports` |
| **HTTP status codes** | `201 Created`, `404 Not Found`, `400 Bad Request` |

---

# SLIDE 8 — REST API Endpoints

| Method | Endpoint | Description | Response |
|--------|----------|-------------|----------|
| POST | `/api/reports` | Create new report | 201 Created |
| GET | `/api/reports` | Get all reports | 200 OK |
| GET | `/api/reports/{id}` | Get report by ID | 200 / 404 |
| PATCH | `/api/reports/{id}/status` | Update status | 200 / 404 |
| DELETE | `/api/reports/{id}` | Delete report | 204 / 404 |

**Live API Documentation:**
https://campus-security-and-reporting-system.onrender.com/swagger-ui.html

---

# SLIDE 9 — Prototype Features Demo

**Student Portal:**
- Fill in: Title, Category, Location, Priority, Description
- Option to submit anonymously
- Real-time success/error feedback

**Admin Dashboard:**
- View all reports sorted by newest first
- Color-coded priority levels (Critical=Red, High=Orange)
- Status badges (Pending=Yellow, In Progress=Blue, Resolved=Green)
- Update report status with one click

**Live Demo:**
https://campus-security-and-reporting-syste.vercel.app

---

# ============================================================
# PHASE 3 — DOCKER AND VERSION CONTROL
# ============================================================

---

# SLIDE 10 — What is Docker?

**Definition:**
Docker is a platform for **containerization** — packaging an application and ALL its dependencies into a single portable unit called a **container**.

**The Problem Without Docker:**
To run CSRS, someone needs to manually:
1. Install Java 17
2. Install Node.js 20
3. Install and configure MongoDB
4. Set environment variables
5. Run 3 separate commands

**The Solution With Docker:**
```bash
docker-compose up --build
```
**One command. Everything starts automatically.**

---

# SLIDE 11 — Docker Key Concepts

| Concept | What It Is | Our Usage |
|---------|-----------|-----------|
| **Image** | Blueprint for a container | `maven:3.9.6`, `node:20-alpine`, `nginx` |
| **Container** | Running instance of an image | backend, frontend, mongodb |
| **Dockerfile** | Recipe to build an image | One per service |
| **docker-compose** | Runs multiple containers together | `docker-compose.yml` |
| **Volume** | Persistent storage | `mongodb_data` |
| **Network** | Private connection between containers | `csrs-network` |

---

# SLIDE 12 — Multi-Stage Dockerfile (Backend)

**Why Multi-Stage?**
Build in a large image, run in a small image.
Result: **600MB → 200MB** (saves 400MB)

```dockerfile
# STAGE 1: BUILD (uses Maven — large image)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src ./src
RUN mvn clean package -DskipTests

# STAGE 2: RUN (uses only JRE — small image)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Analogy:** You don't ship a factory to deliver a car.
You build the car in the factory, then ship only the car.

---

# SLIDE 13 — Multi-Stage Dockerfile (Frontend)

```dockerfile
# STAGE 1: BUILD (uses Node.js)
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build        # Creates /dist folder

# STAGE 2: SERVE (uses Nginx — tiny web server)
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Why Nginx?**
Vue.js builds to static HTML/CSS/JS files.
Nginx serves them fast and also proxies `/api/` calls to the backend.

---

# SLIDE 14 — docker-compose.yml

```yaml
services:
  mongodb:          # Database — starts first
    image: mongo:7.0
    volumes:
      - mongodb_data:/data/db    # Data persists on restart

  backend:          # Spring Boot — starts after MongoDB
    build: ./csrs-backend
    depends_on: [mongodb]
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://admin:admin123@mongodb:27017/csrs_db

  frontend:         # Vue.js + Nginx — starts after backend
    build: ./csrs-frontend
    depends_on: [backend]
    ports: ["80:80"]

networks:
  csrs-network:     # All containers communicate here

volumes:
  mongodb_data:     # Persistent database storage
```

---

# SLIDE 15 — Docker Architecture Diagram

```
┌─────────────────────────────────────────────────┐
│              Docker Host Machine                │
│                                                 │
│  ┌─────────────┐      ┌─────────────┐           │
│  │  Frontend   │      │   Backend   │           │
│  │ Nginx :80   │─────▶│ Spring :8080│           │
│  └─────────────┘      └──────┬──────┘           │
│         ▲                    │                  │
│    User Browser         ┌────▼────┐             │
│    localhost:80         │ MongoDB │             │
│                         │  :27017 │             │
│                         └─────────┘             │
│                                                 │
│  All connected via: csrs-network                │
└─────────────────────────────────────────────────┘
```

---

# SLIDE 16 — Version Control with Git

**What is Git?**
Git is the world's most popular **Version Control System (VCS)**.
It tracks every change to code — who changed what, when, and why.

**Why Git?**
- **History** — see every change ever made
- **Backup** — code is safe even if laptop dies
- **Collaboration** — multiple developers, no conflicts
- **Rollback** — go back to any previous version

**Commands Used:**
```bash
git init                    # Create repository
git config user.name "..."  # Set identity
git add .                   # Stage all files
git commit -m "message"     # Save snapshot
git push                    # Upload to GitHub
git log --oneline           # View history
```

---

# SLIDE 17 — Git Commit History

```
0e68dc0  Add final submission document covering all 4 phases
4d76fa9  Fix MongoDB connection string for production deployment
419913a  Update deployment config with correct repository name
ed5bbf8  Phase 3: Configure deployment for Render and Vercel
f0a37c2  Phase 4: Add test plan, JUnit unit tests, MockMvc tests
c702ef0  Phase 1: System analysis and design - all 5 UML diagrams
```

**GitHub Repository:**
https://github.com/uw-Adeline/Campus-Security-And-Reporting-System-

---

# SLIDE 18 — .gitignore — What We Exclude

| Excluded File/Folder | Why |
|---------------------|-----|
| `csrs-backend/target/` | Compiled Java — auto-generated by Maven |
| `csrs-frontend/node_modules/` | 300MB+ packages — reinstalled with `npm install` |
| `.env` | Passwords and secrets — NEVER commit |
| `*.log` | Log files — not useful in history |
| `.idea/` `.vscode/` | IDE settings — different per developer |

**Rule:** Only commit SOURCE CODE, not generated files or secrets.

---

# SLIDE 19 — Deployment Architecture

```
Developer pushes code
        ↓
    GitHub Repo
   /           \
  ↓             ↓
Render        Vercel
(Backend)    (Frontend)
Spring Boot   Vue.js
    ↓
MongoDB Atlas
(Database)
```

| Service | Platform | URL |
|---------|----------|-----|
| Frontend | Vercel | https://campus-security-and-reporting-syste.vercel.app |
| Backend | Render | https://campus-security-and-reporting-system.onrender.com |
| Database | MongoDB Atlas | Cloud hosted |

---

# ============================================================
# PHASE 4 — SOFTWARE TEST PLAN
# ============================================================

---

# SLIDE 20 — What is a Software Test Plan?

**Definition:**
A software test plan is a document that serves as a roadmap for all testing activities. It defines:
- **What** will be tested
- **How** it will be tested
- **Who** does the testing
- **When** testing happens
- **Expected results** for each test

**Why Testing Matters:**
- Proves the software works correctly
- Catches bugs before users find them
- Verifies business rules are enforced
- Gives confidence to stakeholders

---

# SLIDE 21 — Testing Strategy

**3 Types of Tests Used:**

| Type | Tool | What It Tests | Speed |
|------|------|--------------|-------|
| **Unit Tests** | JUnit 5 + Mockito | Service Layer logic in isolation | Very Fast |
| **Integration Tests** | Spring MockMvc | REST API endpoints (HTTP layer) | Fast |
| **Manual Tests** | Browser | Frontend UI behavior | Manual |

**AAA Pattern — Every test follows this structure:**
```
ARRANGE → Set up test data and mock dependencies
ACT     → Call the method or endpoint being tested
ASSERT  → Verify the result matches expectations
```

---

# SLIDE 22 — What is Mockito?

**Problem:** Unit tests should NOT connect to a real database.
**Solution:** Mockito creates **fake (mock) versions** of dependencies.

```java
@Mock
private ReportRepository reportRepository; // FAKE — no MongoDB needed

// Tell the fake what to return
when(reportRepository.findAll()).thenReturn(Arrays.asList(report1, report2));

// Verify it was called correctly
verify(reportRepository, times(1)).save(any());
```

**Benefits:**
- Tests run in milliseconds (no network)
- Tests are reliable (not affected by database state)
- Tests are isolated (only test OUR code)

---

# SLIDE 23 — Service Layer Test Results (12 Tests)

| Test ID | What Was Tested | Result |
|---------|----------------|--------|
| TC-SRV-001 | Create valid report → returns PENDING status | ✅ PASS |
| TC-SRV-002 | Null status → defaults to PENDING | ✅ PASS |
| TC-SRV-003 | Get all reports → returns full list | ✅ PASS |
| TC-SRV-004 | Get all reports → returns empty list | ✅ PASS |
| TC-SRV-005 | Get by existing ID → returns report | ✅ PASS |
| TC-SRV-006 | Get by non-existing ID → returns empty | ✅ PASS |
| TC-SRV-007 | Update status to IN_PROGRESS → succeeds | ✅ PASS |
| TC-SRV-008 | Update status to RESOLVED → succeeds | ✅ PASS |
| TC-SRV-009 | Invalid status → throws exception | ✅ PASS |
| TC-SRV-010 | Update non-existing report → returns empty | ✅ PASS |
| TC-SRV-011 | Delete existing report → returns true | ✅ PASS |
| TC-SRV-012 | Delete non-existing report → returns false | ✅ PASS |

---

# SLIDE 24 — Controller Layer Test Results (11 Tests)

| Test ID | Endpoint | Expected | Result |
|---------|----------|----------|--------|
| TC-CTRL-001 | POST /api/reports (valid) | 201 Created | ✅ PASS |
| TC-CTRL-002 | POST /api/reports (no title) | 400 Bad Request | ✅ PASS |
| TC-CTRL-003 | GET /api/reports (has data) | 200 OK + list | ✅ PASS |
| TC-CTRL-004 | GET /api/reports (empty) | 200 OK + [] | ✅ PASS |
| TC-CTRL-005 | GET /api/reports/{id} (found) | 200 OK | ✅ PASS |
| TC-CTRL-006 | GET /api/reports/{id} (not found) | 404 Not Found | ✅ PASS |
| TC-CTRL-007 | PATCH status (valid) | 200 OK | ✅ PASS |
| TC-CTRL-008 | PATCH status (invalid) | 400 Bad Request | ✅ PASS |
| TC-CTRL-009 | PATCH status (not found) | 404 Not Found | ✅ PASS |
| TC-CTRL-010 | DELETE (existing) | 204 No Content | ✅ PASS |
| TC-CTRL-011 | DELETE (not found) | 404 Not Found | ✅ PASS |

---

# SLIDE 25 — Manual Frontend Test Results (6 Tests)

| Test ID | What Was Tested | Result |
|---------|----------------|--------|
| TC-UI-001 | Student submits valid report → success message | ✅ PASS |
| TC-UI-002 | Submit with missing fields → validation error | ✅ PASS |
| TC-UI-003 | Admin views all reports on dashboard | ✅ PASS |
| TC-UI-004 | Admin updates report status → updates instantly | ✅ PASS |
| TC-UI-005 | Empty state message when no reports exist | ✅ PASS |
| TC-UI-006 | Anonymous report → no identity linked | ✅ PASS |

---

# SLIDE 26 — Live Test Results (Maven Output)

**Command to run:**
```bash
cd csrs-backend
mvn test
```

**Actual Output:**
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

---

# SLIDE 27 — Test Coverage Summary

| Layer | Tests Written | Passed | Failed | Pass Rate |
|-------|--------------|--------|--------|-----------|
| Service Layer | 12 | 12 | 0 | 100% |
| Controller Layer | 11 | 11 | 0 | 100% |
| Frontend (Manual) | 6 | 6 | 0 | 100% |
| **TOTAL** | **29** | **29** | **0** | **100%** |

---

# SLIDE 28 — CONCLUSION

**What was built:**
A fully functional Campus Security And Reporting System for AUCA

**What was achieved across all 4 phases:**

| Phase | Achievement |
|-------|------------|
| Phase 1 | Complete system analysis with 5 UML diagrams |
| Phase 2 | Working prototype with Service Layer Pattern + coding standards |
| Phase 3 | Dockerized app + Git version control + live deployment |
| Phase 4 | 29 tests, 100% pass rate |

**Live Application:**
https://campus-security-and-reporting-syste.vercel.app

**Source Code:**
https://github.com/uw-Adeline/Campus-Security-And-Reporting-System-

---

*Thank you*
