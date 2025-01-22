package com.phanta.waladom.report;


import com.phanta.waladom.report.evidence.ReportEvidence;
import com.phanta.waladom.report.evidence.ReportEvidenceDTO;
import com.phanta.waladom.report.evidence.ReportEvidenceRepository;
import com.phanta.waladom.user.User;
import com.phanta.waladom.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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


    public ReportService(ReportRepository reportRepository, UserRepository userRepository, ReportEvidenceRepository reportEvidenceRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.reportEvidenceRepository = reportEvidenceRepository;
    }

//    public List<ReportResponseDTO> getAllReports() {
//        return reportRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
//    }



    public ReportResponseDTO getReportById(String reportId) {
        // Assuming reportId is already defined
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found for id: " + reportId));

        List<ReportEvidenceDTO> evidenceDTOs = report.getEvidenceList().stream()
                .map(evidence -> new ReportEvidenceDTO(
                        evidence.getId(),
                        evidence.getEvidenceType(),
                        evidence.getFileUrl(),
                        evidence.getDescription()
                ))
                .collect(Collectors.toList());

        return new ReportResponseDTO(
                report.getId(),
                report.getUser().getId(),
                report.getType().toString(),
                report.getDescription(),
                report.getCountry(),
                report.getCity(),
                report.getActor().toString(),
                report.getActorName(),
                report.getActorDesc(),
                report.getActorAccount(),
                report.getVictim().toString(),
                report.getGoogleMapLink(),
                report.getStatus().toString(),
                report.getCreatedAt(),
                report.getUpdatedAt(),
                evidenceDTOs
        );
    }


    @Transactional
    public ResponseEntity<?> createReport(ReportRequestDTO request) {
        try {

            Optional<User> existingUser = userRepository.findById(request.getUserId());
            if (!existingUser.isPresent()) {
                // Return a bad request response with the existing user's ID
                return ResponseEntity.badRequest().body(
                        Map.of(
                                "timestamp", LocalDateTime.now(),
                                "status", HttpStatus.BAD_REQUEST.value(),
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
            reportResponseDTO.setUpdatedAt(report.getUpdatedAt());

            List<ReportEvidenceDTO> evidenceDTOS = new ArrayList<>();
            for (ReportEvidence reportEvidence : report.getEvidenceList()) {
                ReportEvidenceDTO reportEvidenceDTO = new ReportEvidenceDTO();
                reportEvidenceDTO.setId(reportEvidence.getId());
                reportEvidenceDTO.setFileUrl(reportEvidence.getFileUrl());
                reportEvidenceDTO.setDescription(reportEvidence.getDescription());
                reportEvidenceDTO.setEvidenceType(reportEvidence.getEvidenceType());
                evidenceDTOS.add(reportEvidenceDTO);
            }
            reportResponseDTO.setReportEvidences(evidenceDTOS);

            return ResponseEntity.ok(reportResponseDTO);
        } catch (Exception ex) {
            throw new RuntimeException();
        }
    }

    public void deleteReport(String id) {
        reportRepository.deleteById(id);
    }

//    private ReportResponseDTO mapToDTO(Report report) {
//        return new ReportResponseDTO(
//                report.getId(),
//                report.getUserId(),
//                report.getType(),
//                report.getDescription(),
//                report.getStatus(),
//                report.getCountry(),
//                report.getCity(),
//                report.getGoogleLocationLink(),
//                report.getEvidenceList().stream().map(ReportEvidence::getFileUrl).collect(Collectors.toList())
//        );
//    }
}