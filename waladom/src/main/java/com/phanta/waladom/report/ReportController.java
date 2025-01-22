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


    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReportById(@PathVariable String id) {
        return reportService.getReportById(id);
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

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(@PathVariable String id, @RequestBody ReportRequestDTO request) {
        return reportService.reportUpdate(id, request);
    }


    @GetMapping("/get/all")
    public ResponseEntity<?> getAllReports(){
        return reportService.getAllReports();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable String id) {
        try {
            return reportService.deleteReport(id);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal Server Error",
                            "message", "Error while deleting the report.",
                            "path", "/api/report/delete/" + id
                    )
            );
        }
    }
}