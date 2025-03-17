package com.example.jasperreports.service;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private DataSource dataSource; // Inyectamos el DataSource para la conexión a la BD

    @Autowired
    private ResourceLoader resourceLoader; // Cargar archivos desde classpath

    public byte[] generateReport(String reportName, Map<String, Object> parameters) throws Exception {
        // Ruta correcta del reporte dentro de resources
        String reportPath = "classpath:reports/" + reportName + ".jasper";

        Map<String, Object> parametros;
        parametros = new HashMap<>();
        parametros.put("id_registro", null);


        // Cargar el archivo .jasper
        Resource resource = resourceLoader.getResource(reportPath);
        if (!resource.exists()) {
            throw new FileNotFoundException("No se encontró el archivo del reporte: " + reportPath);
        }

        try (InputStream jasperStream = resource.getInputStream()) {
            // Cargar el reporte compilado
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(jasperStream);

            // Obtener conexión desde el DataSource
            Connection connection = DataSourceUtils.getConnection(dataSource);
            System.out.println("🔹 Valor:");
            System.out.println("Área: " + parameters);
            System.out.println("Área: " + parametros);
            // Llenar el reporte con datos desde la base de datos
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);

            // Exportar el reporte a PDF
            //return JasperExportManager.exportReportToPdf(jasperPrint);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, outputStream);
            return outputStream.toByteArray();
        }
    }
}
