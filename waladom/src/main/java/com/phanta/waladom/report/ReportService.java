package com.phanta.waladom.report;


import com.phanta.waladom.report.evidence.ReportEvidence;
import com.phanta.waladom.report.evidence.ReportEvidenceDTO;
import com.phanta.waladom.report.evidence.ReportEvidenceRepository;
import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    @Autowired
    private final ReportRepository reportRepository;

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ReportEvidenceRepository reportEvidenceRepository;


    public ReportService(ReportRepository reportRepository, UserRepository userRepository, ReportEvidenceRepository reportEvidenceRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportEvidenceRepository = reportEvidenceRepository;
    }

    public ResponseEntity<?> getAllReports() {
        List<Report> reports = reportRepository.findAll();

        if (reports.isEmpty()) {
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

        List<ReportResponseDTO> reportResponseDTOs = new ArrayList<>();

        for (Report report : reports) {
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

        return ResponseEntity.ok(reportResponseDTOs);
    }



    public ResponseEntity<?> getReportById(String reportId) {
        // Assuming reportId is already defined
        Optional<Report> reportFound = reportRepository.findById(reportId);
        if (reportFound.isEmpty()) {
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

        Report report = reportFound.get();
        List<ReportEvidence> reportEvidences = report.getEvidenceList();
        List<ReportEvidenceDTO> evidenceDTOs  = new ArrayList<>();

        for (ReportEvidence reportEvidence : reportEvidences){
            ReportEvidenceDTO reportEvidenceDTO = new ReportEvidenceDTO();
            reportEvidenceDTO.setEvidenceType(reportEvidence.getEvidenceType());
            reportEvidenceDTO.setDescription(reportEvidence.getDescription());
            reportEvidenceDTO.setId(reportEvidence.getId());
            reportEvidenceDTO.setFileUrl(reportEvidence.getFileUrl());
            reportEvidenceDTO.setUploadedAt(reportEvidence.getUploadedAt());
            evidenceDTOs.add(reportEvidenceDTO);
        }

        return  ResponseEntity.ok( new ReportResponseDTO(
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
        try {

            Optional<User> existingUser = userRepository.findById(request.getUserId());
            if (existingUser.isEmpty()) {
                // Return a bad request response with the existing user's ID
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

            // Fetch the user based on userId
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));


            // Create a new report and set its fields
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
            report.setUser(user);  // Set the user

            // Persist the report
            report = reportRepository.save(report);

            List<ReportEvidence> evidenceList = new ArrayList<>();
            if (request.getEvidenceList() != null && !request.getEvidenceList().isEmpty()) {

                for (ReportEvidenceDTO evidenceDTO : request.getEvidenceList()) {
                    ReportEvidence evidence = new ReportEvidence();

                    evidence.setId("RPOEVD_" + UUID.randomUUID()); // Ensure unique ID
                    evidence.setEvidenceType(evidenceDTO.getEvidenceType());
                    evidence.setFileUrl(evidenceDTO.getFileUrl());
                    evidence.setDescription(evidenceDTO.getDescription());
                    evidence.setReport(report); // Associate the evidence with the report

                    reportEvidenceRepository.save(evidence);
                    evidenceList.add(evidence);
                }
            }

            report.setEvidenceList(evidenceList);
            report = reportRepository.save(report);


            ReportResponseDTO reportResponseDTO = new ReportResponseDTO();
            reportResponseDTO.setUserId(report.getId());
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

            return ResponseEntity.ok(reportResponseDTO);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public ResponseEntity<?> deleteReport(String reportId) {
        Optional<Report> reportFound = reportRepository.findById(reportId);
        if (reportFound.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.NOT_FOUND.value(),
                            "error", "Report does not exist!",
                            "message", "No report found with ID: " + reportId,
                            "path", "/api/report/get/"+reportId
                    )
            );
        }
        reportRepository.deleteById(reportId);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<?> reportUpdate(String reportId, ReportRequestDTO request) {
        try {
            Optional<Report> existingReportOpt = reportRepository.findById(reportId);
            if (existingReportOpt.isEmpty()) {
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

            List<ReportEvidence> evidenceList = new ArrayList<>();
            if (request.getEvidenceList() != null && !request.getEvidenceList().isEmpty()) {
                for (ReportEvidenceDTO evidenceDTO : request.getEvidenceList()) {
                    Optional<ReportEvidence> existingEvidenceOpt = reportEvidenceRepository.findById(evidenceDTO.getId());
                    ReportEvidence evidence = existingEvidenceOpt.orElse(new ReportEvidence());

                    if (evidenceDTO.getEvidenceType() != null) evidence.setEvidenceType(evidenceDTO.getEvidenceType());
                    if (evidenceDTO.getFileUrl() != null) evidence.setFileUrl(evidenceDTO.getFileUrl());
                    if (evidenceDTO.getDescription() != null) evidence.setDescription(evidenceDTO.getDescription());
                    evidence.setReport(report);

                    reportEvidenceRepository.save(evidence);
                    ///evidenceList.add(evidence);
                    report.getEvidenceList().add(evidence);
                }
            }

            //report.setEvidenceList(evidenceList);
            report = reportRepository.save(report);

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

            return ResponseEntity.ok(reportResponseDTO);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal Server Error",
                            "message", "An error occurred while updating the report",
                            "path", "/api/report/update/" + reportId
                    )
            );        }
    }

}