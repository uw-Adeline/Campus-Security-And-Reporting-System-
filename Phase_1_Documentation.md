# PHASE 1: System Analysis and Design

**Topic:** AUCA-Campus Security And Reporting System (CSRS)
**Case Study:** Adventist University of Central Africa (AUCA)

---

### i. General Description and Analysis of the Chosen Case Study
**Adventist University of Central Africa (AUCA)** is a prominent higher education institution. Like any large campus with a high volume of students, faculty, and administrative staff moving daily across its premises, maintaining a safe and secure environment is a top priority. The campus includes lecture halls, administrative blocks, student centers, and extensive outdoor areas. 

Currently, security management relies heavily on manual patrols, physical security guards stationed at key points, and traditional reporting methods such as phone calls or physical visits to the security office. While functional, this traditional approach lacks a centralized, real-time digital mechanism for the university community to seamlessly interact with the security department.

### ii. Functional Diagram
This diagram illustrates the internal workings and data flow of the proposed CSRS within the AUCA environment.

```mermaid
graph TD
    A[Student / Lecturer App] -->|Submits Report & Evidence| B(API Gateway / Web Server)
    C[Admin / Security Dashboard] -->|Reviews & Updates Status| B
    
    B --> E{CSRS Core System Backend}
    
    E -->|Reads/Writes| F[(Database: MongoDB)]
    E -->|Uploads Media| G[Evidence File Storage]
    E -->|Triggers| H((Notification Service))
    
    H -->|Push/Email Alerts| A
```

### iii. Problem Faced by AUCA
Despite having a dedicated security team, AUCA faces several critical challenges with its current manual reporting system:
1. **Delayed Incident Reporting & Response:** Students and staff must physically locate a guard or call an office to report an issue, leading to dangerous delays during critical incidents.
2. **Lack of Anonymity:** Fear of retaliation or exposure often discourages students from reporting suspicious activities, vandalism, or bullying.
3. **Loss of Critical Evidence:** In a manual system, physical evidence (like a photo or video taken on a phone) is difficult to formally attach to an incident report and is often lost or mishandled.
4. **No Centralized Tracking & Analytics:** The administration has no automated way to track the status of reported incidents in real-time, nor can they easily generate analytical reports to identify "hotspots" or recurring security threats on campus.

---

### iv. Object-Oriented System Analysis and Design Diagrams

#### 1. Use Case Diagram
This diagram maps out exactly what actions each user can perform within the system.

```mermaid
graph LR
    subgraph Actors
        S((Student))
        L((Lecturer))
        A((Admin))
    end

    subgraph "AUCA CSRS System"
        UC1(Login / Register)
        UC2(Submit Report)
        UC3(Upload Evidence)
        UC4(Track Report Status)
        UC5(View Relevant Reports)
        UC6(Receive Notifications)
        UC7(Update Report Status)
        UC8(Generate Analytics)
    end

    S --> UC1
    S --> UC2
    S --> UC3
    S --> UC4
    S --> UC6

    L --> UC1
    L --> UC5
    L --> UC6

    A --> UC1
    A --> UC5
    A --> UC7
    A --> UC8
```

#### 2. Class Diagram
This diagram models all entities in the CSRS system and their relationships, based on the implemented Java models.

```mermaid
classDiagram
    class Student {
        +String studentID
        +String firstName
        +String lastName
        +String email
        +String phoneNumber
        +getStudentID() String
        +getFullName() String
    }

    class Lecturer {
        +String lecturerID
        +String name
        +String email
        +String department
        +getLecturerID() String
        +getName() String
    }

    class Admin {
        +String adminID
        +String username
        +String password
        +String role
        +getAdminID() String
        +getRole() String
    }

    class Report {
        +String reportID
        +String title
        +String category
        +String location
        +String priorityLevel
        +String description
        +String status
        +LocalDateTime submittedAt
        +Boolean isAnonymous
        +String studentID
        +List~Evidence~ evidenceList
        +getReportID() String
        +getStatus() String
        +setStatus(String) void
    }

    class Evidence {
        +String evidenceID
        +String fileType
        +String fileName
        +String filePath
        +LocalDateTime uploadedAt
        +getEvidenceID() String
        +getFileName() String
    }

    Student "1" --> "0..*" Report : submits
    Lecturer "1" --> "0..*" Report : views
    Admin "1" --> "0..*" Report : manages
    Report "1" *-- "0..*" Evidence : contains
```

#### 3. Activity Diagrams

**Activity Diagram 1 — Student Submits a Security Report**

This diagram shows the step-by-step flow of actions when a student submits an incident report through the system.

```mermaid
flowchart TD
    A([Start]) --> B[Student Opens CSRS App]
    B --> C[Navigates to Student Portal]
    C --> D[Fills in Report Form\nTitle, Category, Location,\nPriority, Description]
    D --> E{Choose to submit\nanonymously?}
    E -->|Yes| F[Sets isAnonymous = true]
    E -->|No| G[Identity linked to report]
    F --> H[Clicks Submit Report]
    G --> H
    H --> I{Form Validation\nPassed?}
    I -->|No - Fields missing| J[Show Validation Error]
    J --> D
    I -->|Yes| K[POST /api/reports\nSent to Backend]
    K --> L{Backend saves\nto MongoDB?}
    L -->|Error| M[Display Error Message\nto Student]
    M --> H
    L -->|Success - 201 Created| N[Display Success\nConfirmation Message]
    N --> O[Form is Reset]
    O --> P([End])
```

---

**Activity Diagram 2 — Admin Reviews and Updates a Report**

This diagram shows the flow of actions when an Admin logs into the dashboard and manages incoming reports.

```mermaid
flowchart TD
    A([Start]) --> B[Admin Opens CSRS App]
    B --> C[Navigates to Admin Dashboard]
    C --> D[System Fetches All Reports\nGET /api/reports]
    D --> E{Reports found\nin database?}
    E -->|No| F[Display: No reports found]
    F --> G[Admin Waits / Refreshes]
    G --> D
    E -->|Yes| H[Reports Displayed\nSorted by Newest First]
    H --> I[Admin Reviews Report Details\nTitle, Category, Priority, Description]
    I --> J[Admin Selects New Status\nfrom Dropdown]
    J --> K[PATCH /api/reports/id/status\nSent to Backend]
    K --> L{Status Updated\nSuccessfully?}
    L -->|No| M[Show Alert: Failed to update\nRevert to old status]
    M --> I
    L -->|Yes| N[Report Card Updated\nwith New Status Badge]
    N --> O{Review more\nreports?}
    O -->|Yes| I
    O -->|No| P([End])
```

#### 4. Sequence Diagram
This diagram shows the chronological, step-by-step interaction of objects when a Student submits a new security report.

```mermaid
sequenceDiagram
    actor Student
    participant UI as Frontend UI (Vue.js)
    participant Ctrl as Backend API (Spring Boot)
    participant DB as Database (MongoDB)
    actor Admin

    Student->>UI: Fills Report Details & Uploads Evidence
    UI->>Ctrl: POST /api/reports
    Ctrl->>Ctrl: Validate Data
    Ctrl->>DB: Save Report & Evidence
    DB-->>Ctrl: Returns saved Report ID
    Ctrl-->>UI: 201 Created (Success)
    UI-->>Student: Displays Confirmation Message
    
    Ctrl->>Admin: System Notification: New Report Alert
    Admin->>UI: Access Dashboard
    UI->>Ctrl: GET /api/reports
    Ctrl->>DB: Fetch Reports
    DB-->>Ctrl: Returns List of Reports
    Ctrl-->>UI: Display List
    UI-->>Admin: Views the new Report details
```

#### 5. Component Diagram
This diagram outlines the physical and logical software components of the CSRS application.

```mermaid
graph TD
    subgraph "Frontend Layer (Vue.js)"
        UI[User Interface Component]
        Auth[Authentication View]
        Dashboard[Dashboard View]
        Form[Report Form Component]
    end

    subgraph "Backend Layer (Spring Boot API)"
        RC[Report Controller]
        AC[Auth Controller]
        RS[Report Service]
        AS[Auth Service]
    end

    subgraph "Data Layer"
        DB[(MongoDB Database)]
    end

    UI --> Auth
    UI --> Dashboard
    UI --> Form

    Auth -->|HTTP POST| AC
    Dashboard -->|HTTP GET| RC
    Form -->|HTTP POST| RC

    AC --> AS
    RC --> RS

    AS -->|Read/Write| DB
    RS -->|Read/Write| DB
```
