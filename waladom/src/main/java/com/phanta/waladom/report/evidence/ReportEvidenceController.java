package com.phanta.waladom.report.evidence;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report/evidence")
public class ReportEvidenceController {

    private final ReportEvidenceService reportEvidenceService;

    public ReportEvidenceController(ReportEvidenceService reportEvidenceService) {
        this.reportEvidenceService = reportEvidenceService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReportEvidence(@RequestBody ReportEvidenceDTO evidenceDTO) {
        return reportEvidenceService.createReportEvidence(evidenceDTO);
    }

    @GetMapping("/get/report/{reportId}")
    public ResponseEntity<?> getAllEvidenceByReport(@PathVariable String reportId) {
        return reportEvidenceService.getAllEvidenceByReport(reportId);
    }

    @PutMapping("/update/{evidenceId}")
    public ResponseEntity<?> updateReportEvidence(@PathVariable String evidenceId, @RequestBody ReportEvidenceDTO evidenceDTO) {
        return reportEvidenceService.updateReportEvidence(evidenceId, evidenceDTO);
    }

    @DeleteMapping("/delete/{evidenceId}")
    public ResponseEntity<?> deleteReportEvidence(@PathVariable String evidenceId) {
        return reportEvidenceService.deleteReportEvidence(evidenceId);
    }
}
