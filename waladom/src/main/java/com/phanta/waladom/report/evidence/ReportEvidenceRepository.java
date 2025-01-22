package com.phanta.waladom.report.evidence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportEvidenceRepository extends JpaRepository<ReportEvidence, Long> {}

