package com.phanta.waladom.report.evidence;

import com.phanta.waladom.fileUpload.S3Service;
import com.phanta.waladom.report.Report;
import com.phanta.waladom.report.ReportRepository;
import com.phanta.waladom.report.ReportService;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class ReportEvidenceService {

    private final ReportEvidenceRepository reportEvidenceRepository;
    private final ReportRepository reportRepository;


    @Autowired
    private final S3Service s3Service;

    public ReportEvidenceService(ReportEvidenceRepository reportEvidenceRepository, ReportRepository reportRepository, S3Service s3Service) {
        this.reportEvidenceRepository = reportEvidenceRepository;
        this.reportRepository = reportRepository;
        this.s3Service = s3Service;
    }

    private static final Logger log = LogManager.getLogger(ReportService.class);


    public ResponseEntity<?> getAllEvidenceByReport(String reportId) {
        log.info("Starting to retrieve all evidence for report ID: {}", reportId);

        Optional<Report> existingReportOpt = reportRepository.findById(reportId);
        if (existingReportOpt.isEmpty()) {
            log.warn("Report not found for ID: {}", reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Report not found!",
                            "message", "No report exists with ID: " + reportId,
                            "path", "/api/evidence/get/report/" + reportId
                    )
            );
        }

        Report report = existingReportOpt.get();
        log.info("Found report with ID: {}", report.getId());

        List<ReportEvidence> evidences = reportEvidenceRepository.findByReportId(reportId);
        log.info("Found {} evidences for report ID: {}", evidences.size(), reportId);

        List<ReportEvidenceDTO> evidenceDTOList = new ArrayList<>();
        for (ReportEvidence evidence : evidences) {
            ReportEvidenceDTO dto = new ReportEvidenceDTO();
            dto.setId(evidence.getId());
            dto.setEvidenceType(evidence.getEvidenceType());
            dto.setUploadedAt(evidence.getUploadedAt());
            dto.setFileUrl(evidence.getFileUrl());
            dto.setDescription(evidence.getDescription());
            dto.setReportId(existingReportOpt.get().getId());
            evidenceDTOList.add(dto);
        }

        log.info("Returning {} evidence(s) for report ID: {}", evidenceDTOList.size(), reportId);

        return ResponseEntity.ok(evidenceDTOList);
    }


    @Transactional
    public ResponseEntity<?> createReportEvidence(ReportEvidenceDTO evidenceDTO) {
        log.info("Starting the process of creating report evidence for report ID: {}", evidenceDTO.getReportId());

        // Check if the report exists
        Optional<Report> reportOpt = reportRepository.findById(evidenceDTO.getReportId());
        if (reportOpt.isEmpty()) {
            log.warn("Report not found for ID: {}", evidenceDTO.getReportId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Report not found!",
                            "message", "No report exists with ID: " + evidenceDTO.getReportId(),
                            "path", "/api/report/evidence/create"
                    )
            );
        }

        Report report = reportOpt.get();
        log.info("Found report with ID: {}", report.getId());

        // Create new ReportEvidence entity
        ReportEvidence reportEvidence = new ReportEvidence();
        reportEvidence.setId("RPOEVD_" + UUID.randomUUID()); // Ensure unique ID
        reportEvidence.setReport(report);
        reportEvidence.setEvidenceType(evidenceDTO.getEvidenceType());
        reportEvidence.setFileUrl(evidenceDTO.getFileUrl());
        reportEvidence.setDescription(evidenceDTO.getDescription());
        reportEvidence.setUploadedAt(evidenceDTO.getUploadedAt() != null ? evidenceDTO.getUploadedAt() : LocalDateTime.now());

        log.info("Created new report evidence with ID: {}", reportEvidence.getId());

        // Save the new evidence
        ReportEvidence savedEvidence = reportEvidenceRepository.save(reportEvidence);
        log.info("Saved new report evidence with ID: {}", savedEvidence.getId());

        // Convert to DTO for response
        ReportEvidenceDTO responseDTO = new ReportEvidenceDTO();
        responseDTO.setId(savedEvidence.getId());
        responseDTO.setEvidenceType(savedEvidence.getEvidenceType());
        responseDTO.setFileUrl(savedEvidence.getFileUrl());
        responseDTO.setDescription(savedEvidence.getDescription());
        responseDTO.setUploadedAt(savedEvidence.getUploadedAt());
        responseDTO.setReportId(savedEvidence.getReport().getId());

        log.info("Returning response with evidence ID: {}", responseDTO.getId());

        return ResponseEntity.ok().body(responseDTO);
    }


    @Transactional
    public ResponseEntity<?> updateReportEvidence(String evidenceId, ReportEvidenceDTO evidenceDTO) {
        log.info("Starting the process to update report evidence with ID: {}", evidenceId);

        Optional<ReportEvidence> existingReportOpt = reportEvidenceRepository.findById(evidenceId);
        if (existingReportOpt.isEmpty()) {
            log.warn("ReportEvidence not found for ID: {}", evidenceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "ReportEvidence not found!",
                            "message", "No report evidence exists with ID: " + evidenceId,
                            "path", "/api/evidence/update/" + evidenceId
                    )
            );
        }

        ReportEvidence evidence = existingReportOpt.get();
        log.info("Found report evidence with ID: {}", evidence.getId());

        // Update the fields if they are provided
        if (evidenceDTO.getEvidenceType() != null) {
            log.info("Updating evidence type for evidence ID: {}", evidence.getId());
            evidence.setEvidenceType(evidenceDTO.getEvidenceType());
        }
        if (evidenceDTO.getFileUrl() != null) {
            log.info("Updating file URL for evidence ID: {}", evidence.getId());
            evidence.setFileUrl(evidenceDTO.getFileUrl());
        }
        if (evidenceDTO.getDescription() != null) {
            log.info("Updating description for evidence ID: {}", evidence.getId());
            evidence.setDescription(evidenceDTO.getDescription());
        }

        reportEvidenceRepository.save(evidence);
        log.info("Saved updated report evidence with ID: {}", evidence.getId());

        // Convert to DTO for response
        ReportEvidenceDTO responseDTO = new ReportEvidenceDTO();
        responseDTO.setId(evidence.getId());
        responseDTO.setEvidenceType(evidence.getEvidenceType());
        responseDTO.setFileUrl(evidence.getFileUrl());
        responseDTO.setUploadedAt(evidence.getUploadedAt());
        responseDTO.setDescription(evidence.getDescription());
        responseDTO.setReportId(evidence.getReport().getId());

        log.info("Returning updated evidence with ID: {}", responseDTO.getId());

        return ResponseEntity.ok(responseDTO);
    }

    @Transactional
    public ResponseEntity<?> deleteReportEvidence(String evidenceId) {
        log.info("Starting the process to delete report evidence with ID: {}", evidenceId);

        Optional<ReportEvidence> existingReportOpt = reportEvidenceRepository.findById(evidenceId);
        if (existingReportOpt.isEmpty()) {
            log.warn("ReportEvidence not found for ID: {}", evidenceId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "ReportEvidence not found!",
                            "message", "No report evidence exists with ID: " + evidenceId,
                            "path", "/api/evidence/update/" + evidenceId
                    )
            );
        }

        reportEvidenceRepository.delete(existingReportOpt.get());
        log.info("Deleted report evidence with ID: {}", evidenceId);

        ReportEvidence reportEvidence = existingReportOpt.get();
        s3Service.deletePhotoOrFolderFromS3(reportEvidence.getFileUrl());
        log.info("Deleted associated file from S3 for evidence ID: {}", evidenceId);

        return ResponseEntity.noContent().build();
    }

}
