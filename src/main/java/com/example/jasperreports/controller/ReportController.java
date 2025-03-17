package com.example.jasperreports.controller;

import com.example.jasperreports.service.ReportService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping("/generate")
    public ResponseEntity<byte[]> generateReport(
            @RequestParam String reportName,
            @RequestBody Map<String, Object> requestBody) {

        try {
            // Extraer parámetros del request
            Map<String, Object> parameters = (Map<String, Object>) requestBody.get("parameters");

            // Generar el reporte en PDF
            byte[] reportData = reportService.generateReport(reportName, parameters);

            // Configurar la respuesta con el PDF
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + reportName + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(reportData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(("Error generando el reporte: " + e.getMessage()).getBytes());
        }
    }
}
