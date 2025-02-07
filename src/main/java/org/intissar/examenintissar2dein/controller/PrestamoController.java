package org.intissar.examenintissar2dein.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.intissar.examenintissar2dein.Main;
import org.intissar.examenintissar2dein.dao.PrestamoDAO;
import org.intissar.examenintissar2dein.model.Prestamo;
import org.intissar.examenintissar2dein.util.DBConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class PrestamoController {
    private static final Logger logger = LoggerFactory.getLogger(PrestamoController.class);

    @FXML private TextField dniAlumnoField;
    @FXML private TextField codigoLibroField;
    @FXML private DatePicker fechaPrestamoPicker;
    @FXML private TableView<Prestamo> prestamosTable;
    @FXML private TableColumn<Prestamo, Integer> idPrestamoColumn;
    @FXML private TableColumn<Prestamo, String> dniAlumnoColumn;
    @FXML private TableColumn<Prestamo, Integer> codigoLibroColumn;
    @FXML private TableColumn<Prestamo, LocalDate> fechaPrestamoColumn;
    @FXML private ComboBox<String> idiomaComboBox;
    @FXML private Label tituloLabel;
    @FXML private Button registrarPrestamoButton;
    @FXML private Button eliminarPrestamoButton;
    @FXML private Button devolverPrestamoButton;
    @FXML private Button verHistoricoButton;
    @FXML private Button helpButton;
    @FXML private Button informeButton;
    @FXML private Button MenuButton;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    private Prestamo prestamo; // Se agrega para validar su estado antes de generar informes

    public PrestamoController() throws SQLException {}

    @FXML
    public void initialize() {
        if (idiomaComboBox != null) { // Verificar que no sea null
            idiomaComboBox.getItems().addAll("Español", "English");
            idiomaComboBox.setValue(Main.getLocale().getLanguage().equals("en") ? "English" : "Español");
            idiomaComboBox.setOnAction(event -> cambiarIdioma());
        } else {
            logger.warn("⚠ Warning: idiomaComboBox es null. Verifica que el fx:id coincida en prestamo_view.fxml.");
        }

        // Cargar textos desde el archivo de idiomas
        actualizarTextos();

        try {
            idPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().idPrestamoProperty().asObject());
            dniAlumnoColumn.setCellValueFactory(cellData -> cellData.getValue().dniAlumnoProperty());
            codigoLibroColumn.setCellValueFactory(cellData -> cellData.getValue().codigoLibroProperty().asObject());
            fechaPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().fechaPrestamoProperty());

            cargarPrestamosActivos();

        } catch (Exception e) {
            logger.error("Error en la inicialización del controlador de préstamos", e);
        }
    }

    @FXML
    private void cargarPrestamosActivos() {
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosActivos();
        prestamosTable.getItems().setAll(prestamos);
        logger.info("Préstamos activos cargados: {}", prestamos.size());
    }

    /**
     * Configura el ComboBox de idioma y su evento de cambio.
     */
    @FXML
    private void cambiarIdioma() {
        String idiomaSeleccionado = idiomaComboBox.getSelectionModel().getSelectedItem();
        Locale nuevoIdioma = idiomaSeleccionado.equals("English") ? new Locale("en") : new Locale("es");

        // Cambiar el idioma global en Main SIN recargar la vista
        Main.cambiarIdioma(nuevoIdioma, false);

        // Actualizar los textos en la UI sin recargar la vista
        actualizarTextos();
    }


    private void actualizarTextos() {
        ResourceBundle bundle = Main.getBundle();
        if (tituloLabel != null) tituloLabel.setText(bundle.getString("titulo.prestamos"));

// Actualizar botones
        if (registrarPrestamoButton != null) registrarPrestamoButton.setText(bundle.getString("boton.registrarPrestamo"));
        if (eliminarPrestamoButton != null) eliminarPrestamoButton.setText(bundle.getString("boton.eliminarPrestamo"));
        if (devolverPrestamoButton != null) devolverPrestamoButton.setText(bundle.getString("boton.devolverPrestamo"));
        if (verHistoricoButton != null) verHistoricoButton.setText(bundle.getString("boton.verHistorico"));
        if (helpButton != null) helpButton.setText(bundle.getString("boton.ayuda"));
        if (informeButton != null) informeButton.setText(bundle.getString("boton.generarInforme"));
        if (MenuButton != null) MenuButton.setText(bundle.getString("boton.menu"));

// Actualizar placeholders de los campos de entrada
        if (dniAlumnoField != null) dniAlumnoField.setPromptText(bundle.getString("placeholder.dniAlumno"));
        if (codigoLibroField != null) codigoLibroField.setPromptText(bundle.getString("placeholder.codigoLibro"));
        if (fechaPrestamoPicker != null) fechaPrestamoPicker.setPromptText(bundle.getString("placeholder.fechaPrestamo"));

// Actualizar los nombres de las columnas de la tabla
        if (idPrestamoColumn != null) idPrestamoColumn.setText(bundle.getString("columna.idPrestamo"));
        if (dniAlumnoColumn != null) dniAlumnoColumn.setText(bundle.getString("columna.dniAlumno"));
        if (codigoLibroColumn != null) codigoLibroColumn.setText(bundle.getString("columna.codigoLibro"));
        if (fechaPrestamoColumn != null) fechaPrestamoColumn.setText(bundle.getString("columna.fechaPrestamo"));

    }

    @FXML
    private void cargarPrestamosHistorico() {
        try {
            List<Prestamo> prestamosHistoricos = prestamoDAO.obtenerPrestamosHistorico();
            prestamosTable.getItems().setAll(prestamosHistoricos);
        } catch (Exception e) {
            logger.error("Error al cargar los Prestamos", e);
        }
    }

    @FXML
    private void registrarPrestamo() {
        try {
            if (dniAlumnoField.getText().isEmpty() || codigoLibroField.getText().isEmpty() || fechaPrestamoPicker.getValue() == null) {
                mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
                return;
            }

            int codigoLibro = Integer.parseInt(codigoLibroField.getText());
            String dniAlumno = dniAlumnoField.getText();
            LocalDate fechaPrestamo = fechaPrestamoPicker.getValue();

            if (prestamoDAO.esLibroPrestado(codigoLibro)) {
                mostrarAlerta("Error", "El libro ya está prestado.", Alert.AlertType.ERROR);
                return;
            }

            Prestamo nuevoPrestamo = new Prestamo(0, dniAlumno, codigoLibro, fechaPrestamo);
            prestamoDAO.registrarPrestamo(nuevoPrestamo);
            cargarPrestamosActivos();
            mostrarAlerta("Éxito", "Préstamo registrado correctamente.", Alert.AlertType.INFORMATION);

        } catch (NumberFormatException e) {
            logger.error("Error, el código del libro debe ser válido ", e);
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo registrar el préstamo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void eliminarPrestamo() throws SQLException {
        Prestamo prestamoSeleccionado = prestamosTable.getSelectionModel().getSelectedItem();
        if (prestamoSeleccionado != null) {
            boolean eliminado = prestamoDAO.eliminarPrestamo(prestamoSeleccionado.getIdPrestamo());
            if (eliminado) {
                prestamosTable.getItems().remove(prestamoSeleccionado); // Eliminar de la UI
                logger.info("✅ Préstamo eliminado correctamente.");
            } else {
                logger.warn("⚠ No se pudo eliminar el préstamo.");
            }
        } else {
            mostrarAlerta("Error", "Debe seleccionar un préstamo para eliminar.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void devolverPrestamo() {
        Prestamo prestamoSeleccionado = prestamosTable.getSelectionModel().getSelectedItem();
        if (prestamoSeleccionado != null) {
            LocalDate fechaDevolucion = LocalDate.now();
            try {
                // Registrar en el histórico
                prestamoDAO.marcarComoDevuelto(prestamoSeleccionado.getIdPrestamo(), java.sql.Date.valueOf(fechaDevolucion));

                // Actualizar la vista
                cargarPrestamosActivos();
                logger.info("devolucion exitosa !!");
            } catch (SQLException e) {
                mostrarAlerta("Error", "No se pudo procesar la devolución: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            mostrarAlerta("Selección Incorrecta", "Por favor, seleccione un préstamo para devolver.", Alert.AlertType.WARNING);
        }
    }

    @FXML
    private void abrirAyuda() {
        try {
            // Establecer el archivo de ayuda de préstamos
            AyudaHTMLController.setArchivoAyuda("prestamo.html");

            // Cargar la ventana de ayuda
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/AyudaHTML.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ayuda - Préstamos");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.error("no se puede abrir la ayuda ", e);

        }
    }

    @FXML
    private void generarInforme() {
        // Obtener el préstamo seleccionado de la tabla
        Prestamo prestamoSeleccionado = prestamosTable.getSelectionModel().getSelectedItem();

        if (prestamoSeleccionado == null) {
            mostrarAlerta("Error", "Debe seleccionar un préstamo para generar el informe.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Parámetros ajustados al modelo Prestamo
            HashMap<String, Object> parameters = new HashMap<>();
            parameters.put("idPrestamo", prestamoSeleccionado.getIdPrestamo());
            parameters.put("dni_estudiante", prestamoSeleccionado.getDniAlumno()); // Corregido nombre de parámetro
            parameters.put("isbn", prestamoSeleccionado.getCodigoLibro()); // Corregido nombre de parámetro
            parameters.put("fechaPrestamo", java.sql.Date.valueOf(prestamoSeleccionado.getFechaPrestamo()));

            // Verificar si el préstamo tiene una fecha de devolución registrada
            LocalDate fechaDevolucion = prestamoSeleccionado.getFechaDevolucion() != null
                    ? prestamoSeleccionado.getFechaDevolucion()
                    : prestamoSeleccionado.getFechaPrestamo().plusDays(15);
            parameters.put("fechaDevolucion", java.sql.Date.valueOf(fechaDevolucion));

            // Cargar el informe de JasperReports
            InputStream reportStream = getClass().getResourceAsStream("/org/intissar/proyecto2/reports/prestamo_informe.jasper");

            if (reportStream == null) {
                logger.error("❌ No se encontró el archivo de informe.");
                mostrarAlerta("Error", "No se pudo cargar el archivo de informe.", Alert.AlertType.ERROR);
                return;
            }

            try (Connection conn = DBConnection.getConnection()) {
                JasperReport report = (JasperReport) JRLoader.loadObject(reportStream);
                JasperPrint jprint = JasperFillManager.fillReport(report, parameters, conn);

                // Mostrar el informe
                JasperViewer.viewReport(jprint, false);
                logger.info("✅ Informe de préstamo generado correctamente.");
            }

        } catch (JRException e) {
            logger.error("❌ Error al generar el informe de préstamo.", e);
            mostrarAlerta("Error", "No se pudo generar el informe.", Alert.AlertType.ERROR);
        } catch (SQLException e) {
            logger.error("❌ Error en la conexión a la base de datos para generar el informe.", e);
            mostrarAlerta("Error", "No se pudo conectar a la base de datos.", Alert.AlertType.ERROR);
        }
    }




    @FXML
    private void verHistorico() {
        // Cargar la vista de los préstamos históricos
        cargarPrestamosHistorico();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    /**
     * Metodo para volver al menú principal cargando el archivo FXML de inicio.
     *
     * @param actionEvent Evento de acción que desencadena la navegación.
     */
    @FXML
    public void irAmenu(ActionEvent actionEvent) {
        logger.info("🔄 Volviendo al menú principal...");
        Main.cargarVista("/org/intissar/proyecto2/view/inicio.fxml");
    }

}
