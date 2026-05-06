package com.auca.csrs.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lecturers")
public class Lecturer {

    @Id
    private String lecturerID;
    private String name;
    private String email;
    private String department;

    public Lecturer() {}

    public Lecturer(String name, String email, String department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }

    public String getLecturerID() { return lecturerID; }
    public void setLecturerID(String lecturerID) { this.lecturerID = lecturerID; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
