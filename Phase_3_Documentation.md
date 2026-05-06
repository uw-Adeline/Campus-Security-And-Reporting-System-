# PHASE 3: Docker & Version Control

**Project:** AUCA Campus Security And Reporting System (CSRS)

---

## PART A: Dockerizing the Application

### What is Docker?

Docker is a platform for **containerization** — a method of packaging an application and all its dependencies (libraries, runtime, OS packages) into a single portable unit called a **container**.

Think of a container like a shipping container: it holds everything the app needs to run, and it works the same way whether it's on your laptop, a colleague's computer, or a cloud server.

### Why Docker for CSRS?

Without Docker, running CSRS requires:
- Installing Java 17 manually
- Installing Node.js manually
- Installing and configuring MongoDB manually
- Configuring environment variables manually

**With Docker**, one command does everything:
```bash
docker-compose up --build
```

### Key Docker Concepts Used

| Concept | Description | Used In |
|---------|-------------|---------|
| **Image** | A blueprint/template for a container | `FROM maven:3.9.6`, `FROM node:20-alpine` |
| **Container** | A running instance of an image | `csrs-backend`, `csrs-frontend`, `csrs-mongodb` |
| **Dockerfile** | Recipe to build a custom image | `csrs-backend/Dockerfile`, `csrs-frontend/Dockerfile` |
| **docker-compose** | Tool to run multiple containers together | `docker-compose.yml` |
| **Volume** | Persistent storage that survives container restarts | `mongodb_data` volume |
| **Network** | Private network connecting containers | `csrs-network` |
| **Multi-stage Build** | Build in one image, run in a smaller one | Both Dockerfiles |

---

### The Dockerization Process

#### Step 1: Create the Backend Dockerfile (`csrs-backend/Dockerfile`)

We use a **multi-stage build** to keep the final image small:

- **Stage 1 (Build):** Uses `maven:3.9.6-eclipse-temurin-17` to compile the Spring Boot app into a `.jar` file
- **Stage 2 (Run):** Uses `eclipse-temurin:17-jre-alpine` (lightweight) to run the `.jar`

```dockerfile
# Stage 1: Build
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Why multi-stage?** The Maven build image is ~600MB. The final runtime image is only ~200MB. We don't need Maven to *run* the app, only to *build* it.

---

#### Step 2: Create the Frontend Dockerfile (`csrs-frontend/Dockerfile`)

- **Stage 1 (Build):** Uses `node:20-alpine` to run `npm run build`, producing optimized static files in `/dist`
- **Stage 2 (Serve):** Uses `nginx:stable-alpine` to serve those static files

```dockerfile
# Stage 1: Build
FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Serve
FROM nginx:stable-alpine
COPY --from=build /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
```

**Why Nginx?** Vue.js builds to static HTML/CSS/JS files. Nginx is a fast, lightweight web server perfect for serving them. It also acts as a **reverse proxy** — forwarding `/api/...` calls to the Spring Boot backend.

---

#### Step 3: Create Nginx Config (`csrs-frontend/nginx.conf`)

This config does two important things:
1. **Vue Router support:** Redirects all routes to `index.html` so page refreshes work
2. **API Proxy:** Forwards `/api/...` requests to the backend container

```nginx
location / {
    try_files $uri $uri/ /index.html;
}

location /api/ {
    proxy_pass http://backend:8080;
}
```

Note: `backend` here is the **Docker service name** — Docker's internal DNS resolves it automatically.

---

#### Step 4: Create `docker-compose.yml`

This file orchestrates all 3 services together:

```yaml
services:
  mongodb:    # Database container
  backend:    # Spring Boot API container
  frontend:   # Vue.js + Nginx container
```

**Key features:**
- `depends_on`: Ensures MongoDB starts before backend, backend starts before frontend
- `networks`: All containers share `csrs-network` so they can communicate
- `volumes`: `mongodb_data` persists database even when containers restart
- `environment`: Overrides `application.properties` to use the local MongoDB container

---

#### Step 5: Architecture After Dockerization

```
┌─────────────────────────────────────────────────────┐
│                   Docker Host Machine                │
│                                                     │
│  ┌──────────────┐    ┌──────────────┐               │
│  │   Frontend   │    │   Backend    │               │
│  │  (Nginx:80)  │───▶│(Spring:8080) │               │
│  └──────────────┘    └──────┬───────┘               │
│         ▲                   │                       │
│         │                   ▼                       │
│    User Browser      ┌──────────────┐               │
│    localhost:80      │   MongoDB    │               │
│                      │  (Port 27017)│               │
│                      └──────────────┘               │
│                                                     │
│  All containers connected via: csrs-network         │
└─────────────────────────────────────────────────────┘
```

---

### How to Run with Docker

#### Prerequisites:
- Docker Desktop installed ([https://www.docker.com/products/docker-desktop](https://www.docker.com/products/docker-desktop))

#### Commands:

```bash
# 1. Build and start all containers
docker-compose up --build

# 2. Access the application
#    Frontend:  http://localhost
#    Backend:   http://localhost:8080
#    Swagger:   http://localhost:8080/swagger-ui.html

# 3. Stop all containers
docker-compose down

# 4. Stop and remove all data (including database)
docker-compose down -v

# 5. View running containers
docker ps

# 6. View container logs
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mongodb
```

---

## PART B: Version Control with Git

### What is a Version Control System (VCS)?

A **Version Control System** tracks and manages changes to software code over time. It allows:
- **History tracking:** See every change ever made, who made it, and when
- **Collaboration:** Multiple developers can work on the same project without overwriting each other
- **Rollback:** Revert to any previous version if something breaks
- **Branching:** Work on new features without affecting the main codebase

### Why Git?

**Git** is the most widely used VCS in the world. It is:
- **Distributed:** Every developer has a full copy of the repository
- **Fast:** Operations are performed locally
- **Free and open source**
- **Industry standard:** Used by companies like Google, Microsoft, and Meta

### Git Setup for CSRS

#### Step 1: Initialize the Repository

```bash
# Navigate to the project root
cd csrs-project

# Initialize a new Git repository
git init

# Verify initialization
git status
```

#### Step 2: Configure Git Identity

```bash
git config --global user.name "Your Full Name"
git config --global user.email "your.email@auca.ac.rw"
```

#### Step 3: Stage and Commit All Files

```bash
# Stage all files (respects .gitignore — won't add node_modules, target/, etc.)
git add .

# Verify what will be committed
git status

# Create the first commit
git commit -m "Initial commit: CSRS Phase 1 & 2 - System design and prototype"
```

#### Step 4: Create a Remote Repository (GitHub)

```bash
# Create a new repository on GitHub (github.com), then:
git remote add origin https://github.com/YOUR_USERNAME/auca-csrs.git

# Push to GitHub
git push -u origin main
```

#### Step 5: Ongoing Development Workflow

```bash
# After making changes, follow this workflow:

# 1. Check what changed
git status

# 2. Stage specific files
git add csrs-backend/src/main/java/com/auca/csrs/backend/service/ReportService.java

# 3. Or stage all changes
git add .

# 4. Commit with a descriptive message
git commit -m "Phase 2: Add Service Layer and design pattern documentation"

# 5. Push to remote
git push
```

### Commit History for This Project

| Commit | Message | Files Changed |
|--------|---------|---------------|
| `1st` | `Initial commit: CSRS Phase 1 - System analysis and design` | `Phase_1_Documentation.md` |
| `2nd` | `Phase 2: Add Spring Boot backend with models, repository, controller` | All backend Java files |
| `3rd` | `Phase 2: Add Vue.js frontend with ReportForm and AdminDashboard` | All frontend Vue files |
| `4th` | `Phase 2: Add Service Layer and refactor controller` | `ReportService.java`, `ReportController.java` |
| `5th` | `Phase 3: Add Dockerfiles, docker-compose, and nginx config` | `Dockerfile` files, `docker-compose.yml`, `nginx.conf` |

### `.gitignore` Configuration

The `.gitignore` file at the project root tells Git which files to **never track**:

```
csrs-backend/target/     ← Compiled Java classes (auto-generated, not needed)
csrs-frontend/node_modules/  ← 300MB+ of npm packages (reinstalled via npm install)
.env                     ← Secret keys and passwords (NEVER commit these)
*.log                    ← Log files (not useful in version history)
.idea/ .vscode/          ← IDE-specific settings (different per developer)
```

---

## Summary

| Component | Technology | Purpose |
|-----------|-----------|---------|
| Backend Container | `eclipse-temurin:17-jre-alpine` | Runs Spring Boot JAR |
| Frontend Container | `nginx:stable-alpine` | Serves Vue.js + proxies API |
| Database Container | `mongo:7.0` | Stores all reports |
| Orchestration | `docker-compose` | Manages all 3 containers |
| Version Control | `Git` | Tracks all code changes |
| Remote Repository | `GitHub` | Cloud backup + collaboration |

### Files Created in Phase 3:

```
csrs-project/
├── .gitignore                    ← Root Git ignore rules
├── docker-compose.yml            ← Orchestrates all 3 containers
├── csrs-backend/
│   └── Dockerfile                ← Builds Spring Boot container
└── csrs-frontend/
    ├── Dockerfile                ← Builds Vue.js + Nginx container
    ├── nginx.conf                ← Nginx web server configuration
    └── vite.config.js            ← Updated with API proxy for local dev
```
