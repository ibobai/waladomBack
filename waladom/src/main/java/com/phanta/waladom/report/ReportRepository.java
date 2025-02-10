package com.phanta.waladom.report;


import com.phanta.waladom.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, String> {
    @Query("SELECT DISTINCT r FROM Report r LEFT JOIN FETCH r.evidenceList ORDER BY r.createdAt DESC")
    List<Report> getAllReportsWithEvidence();
}

