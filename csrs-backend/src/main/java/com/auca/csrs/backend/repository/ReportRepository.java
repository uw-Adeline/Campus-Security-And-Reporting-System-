package com.auca.csrs.backend.repository;

import com.auca.csrs.backend.model.Report;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends MongoRepository<Report, String> {
    List<Report> findByStatus(String status);
    List<Report> findByStudentID(String studentID);
}
