# PHASE 2: Software Development Prototype

**Project:** AUCA Campus Security And Reporting System (CSRS)  
**Technology Stack:** Spring Boot 3 (Backend) + Vue.js 3 (Frontend) + MongoDB (Database)

---

## 1. Overview of the Software Prototype

The CSRS prototype is a fully functional web application that allows students to submit security incident reports and enables administrators to review and manage those reports in real-time. The system is built using modern web technologies and follows industry-standard software engineering best practices.

### Key Features Implemented:
- **Student Portal:** Submit security reports with title, category, location, priority, description, and anonymous option
- **Admin Dashboard:** View all submitted reports, update report status (PENDING → IN_PROGRESS → RESOLVED/DISMISSED)
- **Real-time Data Sync:** Frontend communicates with backend via RESTful API
- **Data Persistence:** All reports stored in MongoDB cloud database
- **Input Validation:** Both client-side (Vue.js) and server-side (Spring Boot Bean Validation)
- **API Documentation:** Swagger/OpenAPI automatically generated at `/swagger-ui.html`

---

## 2. Design Pattern Applied: **Service Layer Pattern (Layered Architecture)**

### Pattern Description:
The **Service Layer Pattern** is a structural design pattern that organizes application logic into distinct layers, each with a specific responsibility. This pattern promotes **separation of concerns**, making the codebase more maintainable, testable, and scalable.

### How It Was Applied in CSRS:

Our application follows a **3-tier layered architecture**:

```
┌─────────────────────────────────────┐
│   Presentation Layer (Controller)   │  ← Handles HTTP requests/responses
├─────────────────────────────────────┤
│   Business Logic Layer (Service)    │  ← Contains business rules & validation
├─────────────────────────────────────┤
│   Data Access Layer (Repository)    │  ← Interacts with MongoDB
└─────────────────────────────────────┘
```

#### Layer 1: **Controller Layer** (`ReportController.java`)
- **Responsibility:** Handle HTTP requests, validate input, return HTTP responses
- **Does NOT contain:** Business logic or database queries
- **Example:** `@PostMapping` receives a report, delegates to service, returns `201 Created`

```java
@PostMapping
public ResponseEntity<Report> createReport(@Valid @RequestBody Report report) {
    Report savedReport = reportService.createReport(report);
    return new ResponseEntity<>(savedReport, HttpStatus.CREATED);
}
```

#### Layer 2: **Service Layer** (`ReportService.java`)
- **Responsibility:** Business logic, validation rules, orchestration
- **Example:** Validates that status is one of `PENDING`, `IN_PROGRESS`, `RESOLVED`, `DISMISSED` before updating

```java
public Optional<Report> updateReportStatus(String reportId, String newStatus) {
    if (!isValidStatus(newStatus)) {
        throw new IllegalArgumentException("Invalid status: " + newStatus);
    }
    // ... update logic
}
```

#### Layer 3: **Repository Layer** (`ReportRepository.java`)
- **Responsibility:** Database operations (CRUD)
- **Uses:** Spring Data MongoDB's built-in methods + custom queries

```java
@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByStatus(String status);
    List<Report> findByStudentID(String studentID);
}
```

### Benefits of This Pattern in CSRS:
1. **Testability:** Each layer can be unit tested independently (mock the service in controller tests)
2. **Maintainability:** If we change the database from MongoDB to PostgreSQL, only the Repository layer changes
3. **Reusability:** The same `ReportService` can be used by multiple controllers (e.g., REST API + GraphQL)
4. **Clear Responsibilities:** Developers immediately know where to add new logic

---

## 3. Additional Design Pattern: **Repository Pattern**

The **Repository Pattern** abstracts data access logic and provides a collection-like interface for accessing domain objects. Spring Data MongoDB implements this pattern automatically.

**How it's used:**
- `ReportRepository` extends `MongoRepository<Report, String>`
- Spring generates implementations of `save()`, `findAll()`, `findById()`, etc. at runtime
- Custom queries like `findByStatus(String status)` are auto-implemented based on method naming conventions

---

## 4. Coding Standards Followed

### Google Java Style Guide & Spring Boot Best Practices

We adhered to the following industry-standard coding conventions:

#### **Naming Conventions:**
- **Classes:** PascalCase (e.g., `ReportService`, `ReportController`)
- **Methods:** camelCase (e.g., `createReport()`, `updateReportStatus()`)
- **Constants:** UPPER_SNAKE_CASE (e.g., `MAX_FILE_SIZE`)
- **Packages:** lowercase (e.g., `com.auca.csrs.backend.service`)

#### **Code Structure:**
- **One class per file** with the filename matching the class name
- **Package organization by feature** (not by layer):
  ```
  com.auca.csrs.backend
  ├── controller/
  ├── service/
  ├── repository/
  └── model/
  ```

#### **Documentation:**
- **Javadoc comments** for all public methods explaining purpose, parameters, and return values
- **Inline comments** for complex business logic

Example from `ReportService.java`:
```java
/**
 * Updates the status of an existing report.
 * Business logic: Validates that the new status is one of the allowed values.
 * 
 * @param reportId the unique identifier of the report
 * @param newStatus the new status to set
 * @return Optional containing the updated report if successful
 * @throws IllegalArgumentException if the status is invalid
 */
public Optional<Report> updateReportStatus(String reportId, String newStatus) { ... }
```

#### **Dependency Injection:**
- **Constructor injection** (recommended by Spring) instead of field injection
- All dependencies marked `final` and injected via constructor

```java
private final ReportService reportService;

@Autowired
public ReportController(ReportService reportService) {
    this.reportService = reportService;
}
```

#### **Validation:**
- **Bean Validation annotations** (`@NotBlank`, `@Email`) on model fields
- **`@Valid`** annotation in controller to trigger validation

```java
@NotBlank(message = "Title is required")
private String title;
```

#### **Error Handling:**
- **HTTP status codes** used correctly:
  - `201 Created` for successful POST
  - `200 OK` for successful GET/PATCH
  - `404 Not Found` when resource doesn't exist
  - `400 Bad Request` for invalid input

#### **RESTful API Design:**
- **Resource-based URLs:** `/api/reports` (not `/api/getReports`)
- **HTTP verbs match operations:**
  - `POST /api/reports` → Create
  - `GET /api/reports` → Read all
  - `GET /api/reports/{id}` → Read one
  - `PATCH /api/reports/{id}/status` → Update status
  - `DELETE /api/reports/{id}` → Delete

---

## 5. Technology Stack Details

### Backend (Spring Boot 3.2.5)
- **Framework:** Spring Boot with embedded Tomcat server
- **Database:** MongoDB Atlas (cloud-hosted)
- **Dependencies:**
  - `spring-boot-starter-web` → REST API
  - `spring-boot-starter-data-mongodb` → MongoDB integration
  - `spring-boot-starter-validation` → Bean validation
  - `springdoc-openapi-starter-webmvc-ui` → Swagger documentation
- **Build Tool:** Maven
- **Java Version:** 17

### Frontend (Vue.js 3)
- **Framework:** Vue.js 3 (Composition API)
- **Build Tool:** Vite
- **HTTP Client:** Axios
- **Styling:** Custom CSS with CSS variables for theming

### Database (MongoDB)
- **Type:** NoSQL document database
- **Collections:**
  - `reports` → Stores all security reports
  - `students`, `lecturers`, `admins` → User data (models defined, not yet fully integrated)
- **Connection:** MongoDB Atlas cloud cluster

---

## 6. Project Structure

### Backend Structure:
```
csrs-backend/
├── src/main/java/com/auca/csrs/backend/
│   ├── CsrsBackendApplication.java       # Main Spring Boot entry point
│   ├── controller/
│   │   └── ReportController.java         # REST API endpoints
│   ├── service/
│   │   └── ReportService.java            # Business logic layer
│   ├── repository/
│   │   └── ReportRepository.java         # Data access layer
│   └── model/
│       ├── Report.java                   # Report entity
│       ├── Evidence.java                 # Evidence embedded document
│       ├── Student.java                  # Student entity
│       ├── Lecturer.java                 # Lecturer entity
│       └── Admin.java                    # Admin entity
├── src/main/resources/
│   └── application.properties            # Database config, server port
└── pom.xml                               # Maven dependencies
```

### Frontend Structure:
```
csrs-frontend/
├── src/
│   ├── App.vue                           # Root component with navigation
│   ├── main.js                           # Vue app initialization
│   ├── style.css                         # Global styles
│   └── components/
│       ├── ReportForm.vue                # Student report submission form
│       └── AdminDashboard.vue            # Admin report management view
├── public/                               # Static assets
├── package.json                          # npm dependencies
└── vite.config.js                        # Vite build configuration
```

---

## 7. API Endpoints Implemented

| Method | Endpoint | Description | Request Body | Response |
|--------|----------|-------------|--------------|----------|
| POST | `/api/reports` | Create a new report | `Report` JSON | `201 Created` + Report |
| GET | `/api/reports` | Get all reports | None | `200 OK` + Report[] |
| GET | `/api/reports/{id}` | Get report by ID | None | `200 OK` + Report or `404` |
| PATCH | `/api/reports/{id}/status?status=X` | Update report status | None | `200 OK` + Report or `404` |
| DELETE | `/api/reports/{id}` | Delete a report | None | `204 No Content` or `404` |

---

## 8. How to Run the Prototype

### Backend:
```bash
cd csrs-backend
mvn spring-boot:run
```
Server runs at: `http://localhost:8080`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

### Frontend:
```bash
cd csrs-frontend
npm install
npm run dev
```
App runs at: `http://localhost:5173`

---

## 9. Screenshots & Demo Flow

### Student Portal Flow:
1. User opens the app → sees "Student Portal" button
2. Fills in report form (title, category, location, priority, description)
3. Optionally checks "Submit Anonymously"
4. Clicks "Submit Report"
5. Backend validates and saves to MongoDB
6. Success message displayed: "Report submitted successfully"

### Admin Dashboard Flow:
1. Admin clicks "Admin Dashboard" button
2. System fetches all reports from backend
3. Reports displayed as cards with status badges (color-coded)
4. Admin reviews report details
5. Admin selects new status from dropdown (PENDING → IN_PROGRESS → RESOLVED)
6. Status updated in database via PATCH request
7. UI updates immediately

---

## 10. Future Enhancements (Not Yet Implemented)

- **Authentication & Authorization:** JWT-based login for students/admins
- **File Upload:** Allow students to attach photos/videos as evidence
- **Real-time Notifications:** WebSocket integration for instant alerts
- **Analytics Dashboard:** Charts showing incident trends by location/category
- **Email Notifications:** Automated emails when report status changes

---

## Conclusion

The CSRS prototype successfully demonstrates a production-ready architecture following industry best practices. The **Service Layer Pattern** ensures clean separation of concerns, making the codebase maintainable and testable. Adherence to **Google Java Style Guide** and **Spring Boot conventions** guarantees code quality and readability. The application is fully functional and ready for Phase 3 (Dockerization) and Phase 4 (Testing).
