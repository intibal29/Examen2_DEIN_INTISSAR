package org.intissar.examenintissar2dein.controller;

import javafx.scene.control.Alert;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.intissar.examenintissar2dein.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * Clase responsable de generar distintos informes relacionados con la gestión de préstamos y libros en la biblioteca.
 * Utiliza JasperReports para la generación de informes.
 */
public class InformesController {

    /**
     * Logger para registrar eventos y errores en la generación de informes.
     */
    private static final Logger logger = LoggerFactory.getLogger(InformesController.class);

    /**
     * Genera un informe sobre los alumnos y los préstamos que han realizado.
     */
    public static void generarReporteAlumnos() {
        generarInforme("/org/intissar/proyecto2/reports/informe_alumnos.jasper", "Informe de Alumnos");
    }

    /**
     * Genera un informe sobre los libros disponibles en la biblioteca.
     */
    public static void generarReporteLibros() {
        generarInforme("/org/intissar/proyecto2/reports/informeLibros.jasper", "Informe de Libros");
    }

    /**
     * Abre el informe de historial de préstamos.
     */
    public static void abrirInformeHistorialPrestamos() {
        generarInforme("/org/intissar/proyecto2/reports/historial.jasper", "Historial de Préstamos");
    }

    /**
     * Metodo genérico para generar informes de JasperReports.
     *
     * @param reportPath Ruta del archivo `.jasper`.
     * @param reportTitle Título del informe.
     */
    private static void generarInforme(String reportPath, String reportTitle) {
        try {
            // 📌 Cargar el archivo compilado de JasperReports
            InputStream reportStream = InformesController.class.getResourceAsStream(reportPath);
            if (reportStream == null) {
                logger.error("❌ No se encontró el archivo de informe '{}'.", reportPath);
                mostrarAlerta("Error", "No se pudo cargar el archivo de informe: " + reportTitle, Alert.AlertType.ERROR);
                return;
            }

            // 📌 Conectar con la base de datos
            Connection connection = DBConnection.getConnection();

            // 📌 Llenar el informe con los datos de la base de datos
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, new HashMap<>(), connection);

            // 📌 Mostrar el informe en JasperViewer
            JasperViewer.viewReport(jasperPrint, false);
            logger.info("✅ Informe '{}' generado y mostrado correctamente.", reportTitle);

        } catch (JRException | SQLException e) {
            logger.error("❌ Error al generar el informe '{}'.", reportTitle, e);
            mostrarAlerta("Error", "No se pudo generar el informe: " + reportTitle, Alert.AlertType.ERROR);
        }
    }

    /**
     * Muestra una alerta en pantalla.
     *
     * @param titulo Título de la alerta.
     * @param mensaje Mensaje a mostrar en la alerta.
     * @param tipo Tipo de alerta (Información, Advertencia o Error).
     */
    private static void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
