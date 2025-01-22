package com.phanta.waladom.report.evidence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportEvidenceRepository extends JpaRepository<ReportEvidence, String> {
    @Query("SELECT e FROM ReportEvidence e JOIN FETCH e.report WHERE e.report.id = :reportId")
    List<ReportEvidence> findByReportId(@Param("reportId") String reportId);
}

