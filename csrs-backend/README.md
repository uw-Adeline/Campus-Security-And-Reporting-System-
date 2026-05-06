# AUCA Campus Security And Reporting System (CSRS) Backend

This is the backend API for the CSRS application, built with Spring Boot and MongoDB, demonstrating the MVC design pattern for Best Programming Practices and Design Patterns Final Exam.

## Features
- **MVC Architecture**: Clear separation of Models, Controllers, and Repositories.
- **Reporting System**: Create and view security reports.
- **Status Updates**: Admins can update the status of reports (PENDING, IN_PROGRESS, RESOLVED).
- **Embedded Evidence**: Support for handling media evidence metadata.
- **API Documentation**: Integrated with Swagger UI for comprehensive endpoint testing.

## Prerequisites
- Java 17
- Maven
- MongoDB Atlas account (configured in `application.properties`)

## Getting Started

### 1. Configure MongoDB
Update the `spring.data.mongodb.uri` in `src/main/resources/application.properties` with your actual MongoDB Atlas connection string.

### 2. Run the Application
Open a terminal in the `csrs-backend` directory and run:
```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`.

## The System Journey: How it Works

To demonstrate the system's flow according to the Phase 2 requirements, follow these steps:

### Step 1: Submitting a Security Report
The system uses **Swagger (OpenAPI 3.0)** to provide a user-friendly interface.
1. Open [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).
2. Locate the `POST /api/reports` endpoint.
3. Click "Try it out" and provide a JSON body:
   ```json
   {
     "title": "Suspicious Activity Near Gate 2",
     "category": "Suspicious Activity",
     "location": "Main Campus Gate 2",
     "priorityLevel": "HIGH",
     "description": "Unidentified person loitering near the student parking lot.",
     "isAnonymous": true
   }
   ```
4. Click "Execute". You will receive a **201 Created** response.

> **[SCREENSHOT 1: POST Request in Swagger showing 201 Response for new Report]**

### Step 2: Data Persistence in MongoDB
Once the report is created, it is stored in the **MongoDB Atlas** database.
- The `Report` model is mapped to the `reports` collection.
- Check your MongoDB Atlas dashboard to verify.

> **[SCREENSHOT 2: MongoDB Atlas Collection showing the saved report]**

### Step 3: Admin Review & Status Update
The Admin accesses the reports and updates their status.
1. In Swagger, locate the `PATCH /api/reports/{id}/status` endpoint.
2. Enter the ID of the report you just created.
3. Enter `IN_PROGRESS` as the status parameter.
4. Execute to see the updated record.

> **[SCREENSHOT 3: PATCH Request in Swagger showing updated status]**

---

## Design Patterns Used

### 1. Model-View-Controller (MVC)
The backend strictly follows the MVC pattern (Model-Controller-Repository architecture for APIs).
- **Models**: `Student.java`, `Report.java`, etc., represent the data and business logic.
- **Controllers**: `ReportController.java` acts as the interface handling HTTP requests.
- **Views**: The responses are JSON data acting as the model for the Vue.js Frontend view.

### 2. Singleton Pattern
The Spring Framework naturally applies the Singleton pattern. Controllers (`@RestController`) and Repositories (`@Repository`) are instantiated only once during the application lifecycle, conserving memory and ensuring consistent state management for database connections.
