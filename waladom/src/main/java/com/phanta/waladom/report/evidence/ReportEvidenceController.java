package com.phanta.waladom.report.evidence;

import com.phanta.waladom.report.ReportService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report/evidence")
public class ReportEvidenceController {

    private static final Logger logger = LogManager.getLogger(ReportService.class);

    private final ReportEvidenceService reportEvidenceService;


    public ReportEvidenceController(ReportEvidenceService reportEvidenceService) {
        this.reportEvidenceService = reportEvidenceService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReportEvidence(@RequestBody ReportEvidenceDTO evidenceDTO) {
        logger.info("Received request to create report evidence for report ID: {}", evidenceDTO.getReportId());
        ResponseEntity<?> response = reportEvidenceService.createReportEvidence(evidenceDTO);
        logger.info("Report evidence creation response: {}", response.getStatusCode());
        return response;
    }

    @GetMapping("/get/report/{reportId}")
    public ResponseEntity<?> getAllEvidenceByReport(@PathVariable String reportId) {
        logger.info("Received request to get all evidence for report ID: {}", reportId);
        ResponseEntity<?> response = reportEvidenceService.getAllEvidenceByReport(reportId);
        logger.info("Retrieved evidence for report ID {} with response: {}", reportId, response.getStatusCode());
        return response;
    }

    @PutMapping("/update/{evidenceId}")
    public ResponseEntity<?> updateReportEvidence(@PathVariable String evidenceId, @RequestBody ReportEvidenceDTO evidenceDTO) {
        logger.info("Received request to update report evidence with ID: {}", evidenceId);
        ResponseEntity<?> response = reportEvidenceService.updateReportEvidence(evidenceId, evidenceDTO);
        logger.info("Update response for evidence ID {}: {}", evidenceId, response.getStatusCode());
        return response;
    }

    @DeleteMapping("/delete/{evidenceId}")
    public ResponseEntity<?> deleteReportEvidence(@PathVariable String evidenceId) {
        logger.info("Received request to delete report evidence with ID: {}", evidenceId);
        ResponseEntity<?> response = reportEvidenceService.deleteReportEvidence(evidenceId);
        logger.info("Delete response for evidence ID {}: {}", evidenceId, response.getStatusCode());
        return response;
    }
}
