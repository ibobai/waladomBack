package com.phanta.waladom.report;


import com.phanta.waladom.fileUpload.S3Service;
import com.phanta.waladom.report.evidence.ReportEvidence;
import com.phanta.waladom.report.evidence.ReportEvidenceDTO;
import com.phanta.waladom.report.evidence.ReportEvidenceRepository;
import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    @Autowired
    private final ReportRepository reportRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ReportEvidenceRepository reportEvidenceRepository;

    @Autowired
    private final S3Service s3Service;


    // Initialize the logger
    private static final Logger logger = LogManager.getLogger(ReportService.class);


    public ReportService(ReportRepository reportRepository, UserRepository userRepository, ReportEvidenceRepository reportEvidenceRepository, S3Service s3Service) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportEvidenceRepository = reportEvidenceRepository;
        this.s3Service = s3Service;
    }


        public ResponseEntity<?> getAllReports() {
            // Log the start of the method
            logger.info("Fetching all reports...");

            // Fetch all reports from the repository
            List<Report> reports = reportRepository.findAll();

            // Log the number of reports found
            logger.debug("Number of reports found: {}", reports.size());

            // Check if no reports are found
            if (reports.isEmpty()) {
                // Log a warning if no reports are found
                logger.warn("No reports found in the system.");

                return ResponseEntity.badRequest().body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.NOT_FOUND.value(),
                                "error", "No reports found!",
                                "message", "No reports available in the system.",
                                "path", "/api/report/getAll"
                        )
                );
            }

            // Log the start of report processing
            logger.info("Processing {} reports...", reports.size());

            // Convert reports to DTOs
            List<ReportResponseDTO> reportResponseDTOs = new ArrayList<>();

            for (Report report : reports) {
                // Log the current report being processed
                logger.debug("Processing report with ID: {}", report.getId());

                List<ReportEvidenceDTO> evidenceDTOs = new ArrayList<>();

                for (ReportEvidence reportEvidence : report.getEvidenceList()) {
                    ReportEvidenceDTO reportEvidenceDTO = new ReportEvidenceDTO();
                    reportEvidenceDTO.setEvidenceType(reportEvidence.getEvidenceType());
                    reportEvidenceDTO.setDescription(reportEvidence.getDescription());
                    reportEvidenceDTO.setId(reportEvidence.getId());
                    reportEvidenceDTO.setFileUrl(reportEvidence.getFileUrl());
                    reportEvidenceDTO.setUploadedAt(reportEvidence.getUploadedAt());
                    evidenceDTOs.add(reportEvidenceDTO);
                }

                ReportResponseDTO reportResponseDTO = new ReportResponseDTO(
                        report.getId(),
                        report.getUser().getId(),
                        report.getType(),
                        report.getDescription(),
                        report.getCountry(),
                        report.getCity(),
                        report.getActor(),
                        report.getActorName(),
                        report.getActorDesc(),
                        report.getActorAccount(),
                        report.getVictim(),
                        report.getGoogleMapLink(),
                        report.getStatus(),
                        report.getCreatedAt(),
                        report.getUpdatedAt(),
                        evidenceDTOs,
                        report.getVerifierComment(),
                        report.getVerified()
                );
                reportResponseDTOs.add(reportResponseDTO);
            }

            // Log the successful completion of the method
            logger.info("Successfully fetched and processed {} reports.", reportResponseDTOs.size());

            return ResponseEntity.ok(reportResponseDTOs);
        }


    public ResponseEntity<?> getReportById(String reportId) {
        logger.info("Fetching report with ID: {}", reportId);

        Optional<Report> reportFound = reportRepository.findById(reportId);
        if (reportFound.isEmpty()) {
            logger.warn("Report not found with ID: {}", reportId);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Report does not exist!",
                            "message", "No report found with ID: " + reportId,
                            "path", "/api/report/get/{id}"
                    )
            );
        }

        logger.debug("Report found with ID: {}", reportId);

        Report report = reportFound.get();
        List<ReportEvidence> reportEvidences = report.getEvidenceList();
        List<ReportEvidenceDTO> evidenceDTOs = new ArrayList<>();

        logger.debug("Processing {} evidence items for report ID: {}", reportEvidences.size(), reportId);

        for (ReportEvidence reportEvidence : reportEvidences) {
            ReportEvidenceDTO reportEvidenceDTO = new ReportEvidenceDTO();
            reportEvidenceDTO.setEvidenceType(reportEvidence.getEvidenceType());
            reportEvidenceDTO.setDescription(reportEvidence.getDescription());
            reportEvidenceDTO.setId(reportEvidence.getId());
            reportEvidenceDTO.setFileUrl(reportEvidence.getFileUrl());
            reportEvidenceDTO.setUploadedAt(reportEvidence.getUploadedAt());
            evidenceDTOs.add(reportEvidenceDTO);
        }

        logger.info("Successfully fetched and processed report with ID: {}", reportId);

        return ResponseEntity.ok(new ReportResponseDTO(
                report.getId(),
                report.getUser().getId(),
                report.getType(),
                report.getDescription(),
                report.getCountry(),
                report.getCity(),
                report.getActor(),
                report.getActorName(),
                report.getActorDesc(),
                report.getActorAccount(),
                report.getVictim(),
                report.getGoogleMapLink(),
                report.getStatus(),
                report.getCreatedAt(),
                report.getUpdatedAt(),
                evidenceDTOs,
                report.getVerifierComment(),
                report.getVerified()
        ));
    }


    @Transactional
    public ResponseEntity<?> createReport(ReportRequestDTO request) {
        logger.info("Creating a new report for user ID: {}", request.getUserId());

        try {
            Optional<User> existingUser = userRepository.findById(request.getUserId());
            if (existingUser.isEmpty()) {
                logger.warn("User not found with ID: {}", request.getUserId());

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.NOT_FOUND.value(),
                                "error", "User does not exist!",
                                "message", "No user exists with ID: " + request.getUserId(),
                                "path", "/api/report/create"
                        )
                );
            }

            logger.debug("User found with ID: {}", request.getUserId());

            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            logger.debug("Creating report for user: {}", user.getId());

            Report report = new Report();
            report.setType(request.getType());
            report.setDescription(request.getDescription());
            report.setCountry(request.getCountry());
            report.setCity(request.getCity());
            report.setActor(request.getActor());
            report.setActorName(request.getActorName());
            report.setActorDesc(request.getActorDesc());
            report.setActorAccount(request.getActorAccount());
            report.setVictim(request.getVictim());
            report.setGoogleMapLink(request.getGoogleMapLink());
            report.setStatus(request.getStatus());
            report.setVerified(request.getVerified());
            report.setVerifierComment(request.getVerifierComment());
            report.setUser(user);

            logger.debug("Saving report to the database...");
            report = reportRepository.save(report);
            logger.info("Report saved successfully with ID: {}", report.getId());

            List<ReportEvidence> evidenceList = new ArrayList<>();
            if (request.getEvidenceList() != null && !request.getEvidenceList().isEmpty()) {
                logger.debug("Processing {} evidence items for report ID: {}", request.getEvidenceList().size(), report.getId());

                for (ReportEvidenceDTO evidenceDTO : request.getEvidenceList()) {
                    ReportEvidence evidence = new ReportEvidence();
                    evidence.setId("RPOEVD_" + UUID.randomUUID());
                    evidence.setEvidenceType(evidenceDTO.getEvidenceType());
                    evidence.setFileUrl(evidenceDTO.getFileUrl());
                    evidence.setDescription(evidenceDTO.getDescription());
                    evidence.setReport(report);

                    logger.debug("Saving evidence with ID: {}", evidence.getId());
                    reportEvidenceRepository.save(evidence);
                    evidenceList.add(evidence);
                }
            }

            report.setEvidenceList(evidenceList);
            logger.debug("Updating report with evidence list...");
            report = reportRepository.save(report);
            logger.info("Report updated successfully with evidence list.");

            ReportResponseDTO reportResponseDTO = new ReportResponseDTO();
            reportResponseDTO.setUserId(report.getId());
            reportResponseDTO.setId(report.getUser().getId());
            reportResponseDTO.setType(report.getType());
            reportResponseDTO.setDescription(report.getDescription());
            reportResponseDTO.setCountry(report.getCountry());
            reportResponseDTO.setCity(report.getCity());
            reportResponseDTO.setActor(report.getActor());
            reportResponseDTO.setActorName(report.getActorName());
            reportResponseDTO.setActorDesc(report.getActorDesc());
            reportResponseDTO.setActorAccount(report.getActorAccount());
            reportResponseDTO.setVictim(report.getVictim());
            reportResponseDTO.setGoogleMapLink(report.getGoogleMapLink());
            reportResponseDTO.setStatus(report.getStatus());
            reportResponseDTO.setCreatedAt(report.getCreatedAt());
            reportResponseDTO.setVerified(report.getVerified());
            reportResponseDTO.setVerifierComment(report.getVerifierComment());
            reportResponseDTO.setUpdatedAt(report.getUpdatedAt());

            List<ReportEvidenceDTO> evidenceDTOS = new ArrayList<>();
            for (ReportEvidence reportEvidence : report.getEvidenceList()) {
                ReportEvidenceDTO reportEvidenceDTO = new ReportEvidenceDTO();
                reportEvidenceDTO.setId(reportEvidence.getId());
                reportEvidenceDTO.setFileUrl(reportEvidence.getFileUrl());
                reportEvidenceDTO.setDescription(reportEvidence.getDescription());
                reportEvidenceDTO.setEvidenceType(reportEvidence.getEvidenceType());
                reportEvidenceDTO.setUploadedAt(reportEvidence.getUploadedAt());
                evidenceDTOS.add(reportEvidenceDTO);
            }
            reportResponseDTO.setReportEvidences(evidenceDTOS);

            logger.info("Successfully created and processed report with ID: {}", report.getId());
            return ResponseEntity.ok(reportResponseDTO);
        } catch (Exception ex) {
            logger.error("Error occurred while creating report: {}", ex.getMessage(), ex);
            throw new RuntimeException("Failed to create report", ex);
        }
    }

    public ResponseEntity<?> deleteReport(String reportId) {
        logger.info("Request received to delete report with ID: {}", reportId);

        Optional<Report> reportFound = reportRepository.findById(reportId);
        if (reportFound.isEmpty()) {
            logger.warn("Report with ID {} not found.", reportId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Report does not exist!",
                            "message", "No report found with ID: " + reportId,
                            "path", "/api/report/get/" + reportId
                    )
            );
        }

        logger.info("Report with ID {} found. Proceeding with deletion.", reportId);
        reportRepository.deleteById(reportId);

        Report report = reportFound.get();
        for (ReportEvidence reportEvidence : report.getEvidenceList()) {
            logger.info("Deleting evidence file from S3: {}", reportEvidence.getFileUrl());
            s3Service.deletePhotoOrFolderFromS3(reportEvidence.getFileUrl());
        }

        logger.info("Successfully deleted report with ID: {}", reportId);
        return ResponseEntity.noContent().build();
    }


    @Transactional
    public ResponseEntity<?> reportUpdate(String reportId, ReportRequestDTO request) {
        logger.info("Updating report with ID: {}", reportId);

        try {
            Optional<Report> existingReportOpt = reportRepository.findById(reportId);
            if (existingReportOpt.isEmpty()) {
                logger.warn("Report not found with ID: {}", reportId);

                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.NOT_FOUND.value(),
                                "error", "Report not found!",
                                "message", "No report exists with ID: " + reportId,
                                "path", "/api/report/update"
                        )
                );
            }

            logger.debug("Report found with ID: {}", reportId);

            Report report = existingReportOpt.get();
            if (request.getType() != null) report.setType(request.getType());
            if (request.getDescription() != null) report.setDescription(request.getDescription());
            if (request.getCountry() != null) report.setCountry(request.getCountry());
            if (request.getCity() != null) report.setCity(request.getCity());
            if (request.getActor() != null) report.setActor(request.getActor());
            if (request.getActorName() != null) report.setActorName(request.getActorName());
            if (request.getActorDesc() != null) report.setActorDesc(request.getActorDesc());
            if (request.getActorAccount() != null) report.setActorAccount(request.getActorAccount());
            if (request.getVictim() != null) report.setVictim(request.getVictim());
            if (request.getGoogleMapLink() != null) report.setGoogleMapLink(request.getGoogleMapLink());
            if (request.getStatus() != null) report.setStatus(request.getStatus());
            if (request.getVerified() != null) report.setVerified(request.getVerified());
            if (request.getVerifierComment() != null) report.setVerifierComment(request.getVerifierComment());

            logger.debug("Updated report fields for ID: {}", reportId);

            List<ReportEvidence> existingEvidenceList = report.getEvidenceList();

            // Create a map of existing evidence for quick lookup
            Map<String, ReportEvidence> existingEvidenceMap = existingEvidenceList.stream()
                    .collect(Collectors.toMap(ReportEvidence::getId, evidence -> evidence));

            // Create a new list to hold the updated evidence items
            List<ReportEvidence> updatedEvidenceList = new ArrayList<>();

            if (request.getEvidenceList() != null && !request.getEvidenceList().isEmpty()) {
                logger.debug("Processing {} evidence items for report ID: {}", request.getEvidenceList().size(), reportId);

                for (ReportEvidenceDTO evidenceDTO : request.getEvidenceList()) {
                    ReportEvidence evidence;

                    // Check if the evidence already exists
                    if (evidenceDTO.getId() != null && existingEvidenceMap.containsKey(evidenceDTO.getId())) {
                        logger.debug("Updating existing evidence with ID: {}", evidenceDTO.getId());
                        evidence = existingEvidenceMap.get(evidenceDTO.getId());
                    } else {
                        logger.debug("Creating new evidence for report ID: {}", reportId);
                        evidence = new ReportEvidence();
                        evidence.setId("RPOEVD_" + UUID.randomUUID()); // Ensure unique ID
                    }

                    // Update the evidence fields
                    if (evidenceDTO.getEvidenceType() != null) evidence.setEvidenceType(evidenceDTO.getEvidenceType());
                    if (evidenceDTO.getFileUrl() != null) evidence.setFileUrl(evidenceDTO.getFileUrl());
                    if (evidenceDTO.getDescription() != null) evidence.setDescription(evidenceDTO.getDescription());
                    evidence.setReport(report);

                    // Add the evidence to the updated list
                    updatedEvidenceList.add(evidence);
                }
            }

            // Replace the existing list with the updated list
            existingEvidenceList.clear();
            existingEvidenceList.addAll(updatedEvidenceList);

            logger.debug("Saving updated report with ID: {}", reportId);
            reportRepository.save(report);
            logger.info("Successfully updated report with ID: {}", reportId);

            ReportResponseDTO reportResponseDTO = new ReportResponseDTO();
            reportResponseDTO.setUserId(report.getUser().getId());
            reportResponseDTO.setId(report.getId());
            reportResponseDTO.setType(report.getType());
            reportResponseDTO.setDescription(report.getDescription());
            reportResponseDTO.setCountry(report.getCountry());
            reportResponseDTO.setCity(report.getCity());
            reportResponseDTO.setActor(report.getActor());
            reportResponseDTO.setActorName(report.getActorName());
            reportResponseDTO.setActorDesc(report.getActorDesc());
            reportResponseDTO.setActorAccount(report.getActorAccount());
            reportResponseDTO.setVictim(report.getVictim());
            reportResponseDTO.setGoogleMapLink(report.getGoogleMapLink());
            reportResponseDTO.setStatus(report.getStatus());
            reportResponseDTO.setCreatedAt(report.getCreatedAt());
            reportResponseDTO.setVerified(report.getVerified());
            reportResponseDTO.setVerifierComment(report.getVerifierComment());
            reportResponseDTO.setUpdatedAt(report.getUpdatedAt());

            List<ReportEvidenceDTO> evidenceDTOS = new ArrayList<>();
            for (ReportEvidence reportEvidence : report.getEvidenceList()) {
                ReportEvidenceDTO reportEvidenceDTO = new ReportEvidenceDTO();
                reportEvidenceDTO.setId(reportEvidence.getId());
                reportEvidenceDTO.setFileUrl(reportEvidence.getFileUrl());
                reportEvidenceDTO.setDescription(reportEvidence.getDescription());
                reportEvidenceDTO.setEvidenceType(reportEvidence.getEvidenceType());
                reportEvidenceDTO.setUploadedAt(reportEvidence.getUploadedAt());
                evidenceDTOS.add(reportEvidenceDTO);
            }
            reportResponseDTO.setReportEvidences(evidenceDTOS);

            logger.info("Successfully processed and returned updated report with ID: {}", reportId);
            return ResponseEntity.ok(reportResponseDTO);
        } catch (Exception ex) {
            logger.error("Error occurred while updating report with ID: {}: {}", reportId, ex.getMessage(), ex);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal Server Error",
                            "message", "An error occurred while updating the report",
                            "path", "/api/report/update/" + reportId
                    )
            );
        }
    }

}