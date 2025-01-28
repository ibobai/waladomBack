package com.phanta.waladom.report;

import com.phanta.waladom.utiles.UtilesMethods;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

    @Autowired
    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getReportById(@PathVariable String id) {
        logger.info("Request received to fetch report with ID: {}", id);
        try {
            ResponseEntity<?> response = reportService.getReportById(id);
            logger.info("Successfully fetched report with ID: {}", id);
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while fetching report with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal Server Error",
                            "message", "Error while fetching the report.",
                            "path", "/api/report/get/" + id
                    )
            );
        }
    }

    @PostMapping("/create")
    public ResponseEntity<?> createReport(@RequestBody ReportRequestDTO request) {
        logger.info("Request received to create a new report.");
        if (UtilesMethods.isReportRequestInvalid(request) || UtilesMethods.isReportRequestEmpty(request)) {
            logger.warn("Invalid or empty report request received.");
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
        try {
            ResponseEntity<?> response = reportService.createReport(request);
            logger.info("Successfully created report.");
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while creating report.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal Server Error",
                            "message", "Error while creating the report.",
                            "path", "/api/report/create"
                    )
            );
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateReport(@PathVariable String id, @RequestBody ReportRequestDTO request) {
        logger.info("Request received to update report with ID: {}", id);
        try {
            ResponseEntity<?> response = reportService.reportUpdate(id, request);
            logger.info("Successfully updated report with ID: {}", id);
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while updating report with ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal Server Error",
                            "message", "Error while updating the report.",
                            "path", "/api/report/update/" + id
                    )
            );
        }
    }

    @GetMapping("/get/all")
    public ResponseEntity<?> getAllReports(){
        logger.info("Request received to fetch all reports.");
        try {
            ResponseEntity<?> response = reportService.getAllReports();
            logger.info("Successfully fetched all reports.");
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while fetching all reports.", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "timestamp", LocalDateTime.now(),
                            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                            "error", "Internal Server Error",
                            "message", "Error while fetching the reports.",
                            "path", "/api/report/get/all"
                    )
            );
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteReport(@PathVariable String id) {
        logger.info("Request received to delete report with ID: {}", id);
        try {
            ResponseEntity<?> response = reportService.deleteReport(id);
            logger.info("Successfully deleted report with ID: {}", id);
            return response;
        } catch (Exception e) {
            logger.error("Error occurred while deleting report with ID: {}", id, e);
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
