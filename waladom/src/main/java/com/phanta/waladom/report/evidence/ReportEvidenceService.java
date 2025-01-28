package com.phanta.waladom.report.evidence;

import com.phanta.waladom.fileUpload.S3Service;
import com.phanta.waladom.report.Report;
import com.phanta.waladom.report.ReportRepository;
import jakarta.transaction.Transactional;
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

    public ResponseEntity<?> getAllEvidenceByReport(String reportId) {
        Optional<Report> existingReportOpt = reportRepository.findById(reportId);
        if (existingReportOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Report not found!",
                            "message", "No report exists with ID: " + reportId,
                            "path", "/api/evidence/get/report/"+reportId
                    )
            );
        }
        List<ReportEvidence> evidences = reportEvidenceRepository.findByReportId(reportId);
        List<ReportEvidenceDTO> evidenceDTOList = new ArrayList<>();

        for (ReportEvidence evidence : evidences) {
            ReportEvidenceDTO dto = new ReportEvidenceDTO();
            dto.setId(evidence.getId());
            dto.setEvidenceType(evidence.getEvidenceType());
            dto.setUploadedAt(evidence.getUploadedAt());
            dto.setFileUrl(evidence.getFileUrl());
            dto.setDescription(evidence.getDescription());
            evidenceDTOList.add(dto);
        }

        return ResponseEntity.ok(evidenceDTOList);
    }


    @Transactional
    public ResponseEntity<?> createReportEvidence(ReportEvidenceDTO evidenceDTO) {
        // Check if the report exists
        Optional<Report> reportOpt = reportRepository.findById(evidenceDTO.getReportId());
        if (reportOpt.isEmpty()) {
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

        // Create new ReportEvidence entity
        ReportEvidence reportEvidence = new ReportEvidence();
        reportEvidence.setId("RPOEVD_" + UUID.randomUUID()); // Ensure unique ID
        reportEvidence.setReport(report);
        reportEvidence.setEvidenceType(evidenceDTO.getEvidenceType());
        reportEvidence.setFileUrl(evidenceDTO.getFileUrl());
        reportEvidence.setDescription(evidenceDTO.getDescription());
        reportEvidence.setUploadedAt(evidenceDTO.getUploadedAt() != null ? evidenceDTO.getUploadedAt() : LocalDateTime.now());

        // Save the new evidence
        ReportEvidence savedEvidence = reportEvidenceRepository.save(reportEvidence);

        // Convert to DTO for response
        ReportEvidenceDTO responseDTO = new ReportEvidenceDTO();
        responseDTO.setId(savedEvidence.getId());
        responseDTO.setEvidenceType(savedEvidence.getEvidenceType());
        responseDTO.setFileUrl(savedEvidence.getFileUrl());
        responseDTO.setDescription(savedEvidence.getDescription());
        responseDTO.setUploadedAt(savedEvidence.getUploadedAt());
        responseDTO.setReportId(savedEvidence.getReport().getId());

        return ResponseEntity.ok().body(responseDTO);
    }


    @Transactional
    public ResponseEntity<?> updateReportEvidence(String evidenceId, ReportEvidenceDTO evidenceDTO) {
        Optional<ReportEvidence> existingReportOpt = reportEvidenceRepository.findById(evidenceId);
        if (existingReportOpt.isEmpty()) {
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
        if (evidenceDTO.getEvidenceType() != null) evidence.setEvidenceType(evidenceDTO.getEvidenceType());
        if (evidenceDTO.getFileUrl() != null) evidence.setFileUrl(evidenceDTO.getFileUrl());
        if (evidenceDTO.getDescription() != null) evidence.setDescription(evidenceDTO.getDescription());

        reportEvidenceRepository.save(evidence);

        ReportEvidenceDTO responseDTO = new ReportEvidenceDTO();
        responseDTO.setId(evidence.getId());
        responseDTO.setEvidenceType(evidence.getEvidenceType());
        responseDTO.setFileUrl(evidence.getFileUrl());
        responseDTO.setUploadedAt(evidence.getUploadedAt());
        responseDTO.setDescription(evidence.getDescription());

        return ResponseEntity.ok(responseDTO);
    }

    @Transactional
    public ResponseEntity<?> deleteReportEvidence(String evidenceId) {
        Optional<ReportEvidence> existingReportOpt = reportEvidenceRepository.findById(evidenceId);
        if (existingReportOpt.isEmpty()) {
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

        ReportEvidence reportEvidence = existingReportOpt.get();
        s3Service.deletePhotoOrFolderFromS3(reportEvidence.getFileUrl());
        return ResponseEntity.noContent().build();
    }
}
