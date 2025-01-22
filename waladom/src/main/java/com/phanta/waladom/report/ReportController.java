package com.phanta.waladom.report;


import com.phanta.waladom.utiles.UtilesMethods;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
public class ReportController {

    @Autowired
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

//    @GetMapping("/get/all")
//    public ResponseEntity<List<ReportResponseDTO>> getAllReports() {
//        return ResponseEntity.ok(reportService.getAllReports());
//    }

    @GetMapping("/get/{id}")
    public ResponseEntity<ReportResponseDTO> getReportById(@PathVariable String id) {
        return ResponseEntity.ok(reportService.getReportById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody ReportRequestDTO request) {
        if (UtilesMethods.isReportRequestInvalid(request) || UtilesMethods.isReportRequestEmpty(request)) {
            return ResponseEntity.badRequest().body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.BAD_REQUEST.value(),
                            "error", "Invalid report request",
                            "message", "Some mandatory fields are missing or empty",
                            "path", "/api/report/create"
                    )
            );
        }
        return reportService.createReport(request);
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteReport(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok().build();
    }
}