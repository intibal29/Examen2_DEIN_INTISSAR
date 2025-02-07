package org.intissar.examenintissar2dein.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.intissar.examenintissar2dein.Main;
import org.intissar.examenintissar2dein.dao.HistoricoPrestamoDAO;
import org.intissar.examenintissar2dein.model.HistoricoPrestamo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controlador para la gestión del historial de préstamos de libros.
 * Permite la consulta de préstamos históricos mediante filtros.
 */
public class HistoricoController {

    /**
     * Logger para registrar eventos y errores.
     */
    private static final Logger logger = LoggerFactory.getLogger(HistoricoController.class);

    @FXML private TextField dniAlumnoField;
    @FXML private TextField codigoLibroField;
    @FXML private DatePicker fechaInicioPicker;
    @FXML private DatePicker fechaFinPicker;
    @FXML private TableView<HistoricoPrestamo> historicoTable;
    @FXML private TableColumn<HistoricoPrestamo, Integer> idPrestamoColumn;
    @FXML private TableColumn<HistoricoPrestamo, String> dniAlumnoColumn;
    @FXML private TableColumn<HistoricoPrestamo, Integer> codigoLibroColumn;
    @FXML private TableColumn<HistoricoPrestamo, LocalDate> fechaPrestamoColumn;
    @FXML private TableColumn<HistoricoPrestamo, LocalDate> fechaDevolucionColumn;
    @FXML private TableColumn<HistoricoPrestamo, Boolean> prestamoOnlineColumn;


    @FXML
    private ComboBox<String> idiomaComboBox;
    @FXML
    private Label tituloLabel;
    @FXML
    private Button filtrarHistoricoButton, MenuButton;

    private final HistoricoPrestamoDAO historicoDAO;

    /**
     * Constructor que inicializa el DAO para gestionar los préstamos históricos.
     * @throws SQLException si ocurre un error en la conexión con la base de datos.
     */
    public HistoricoController() throws SQLException {
        historicoDAO = new HistoricoPrestamoDAO();
    }

    /**
     * Metodo de inicialización del controlador.
     * Configura las columnas de la tabla y carga los datos del historial sin filtros.
     */
    @FXML
    public void initialize() {
        // Configurar las columnas de la tabla con las propiedades del modelo
        idPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().idPrestamoProperty().asObject());
        dniAlumnoColumn.setCellValueFactory(cellData -> cellData.getValue().dniAlumnoProperty());
        codigoLibroColumn.setCellValueFactory(cellData -> cellData.getValue().codigoLibroProperty().asObject());
        fechaPrestamoColumn.setCellValueFactory(cellData -> cellData.getValue().fechaPrestamoProperty());

        prestamoOnlineColumn.setCellValueFactory(cellData -> cellData.getValue().prestamoOnlineProperty().asObject()); // Corregido el enlace


        // Cargar todos los préstamos sin filtros al inicio
        cargarHistorico(null, null, null, null);
        // Configurar opciones de idioma
        idiomaComboBox.getItems().addAll("Español", "English");
        idiomaComboBox.setValue(Main.getLocale().getLanguage().equals("en") ? "English" : "Español");
        idiomaComboBox.setOnAction(event -> cambiarIdioma());

        // Cargar textos desde el archivo de idiomas
        actualizarTextos();
    }
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

        // Actualizar título
        if (tituloLabel != null) tituloLabel.setText(bundle.getString("titulo.historico"));

        // Actualizar botones
        if (filtrarHistoricoButton != null) filtrarHistoricoButton.setText(bundle.getString("boton.filtrar"));
        if (MenuButton != null) MenuButton.setText(bundle.getString("boton.menu"));

        // Actualizar columnas de la tabla
        if (idPrestamoColumn != null) idPrestamoColumn.setText(bundle.getString("columna.idPrestamo"));
        if (dniAlumnoColumn != null) dniAlumnoColumn.setText(bundle.getString("columna.dniAlumno"));
        if (codigoLibroColumn != null) codigoLibroColumn.setText(bundle.getString("columna.codigoLibro"));
        if (fechaPrestamoColumn != null) fechaPrestamoColumn.setText(bundle.getString("columna.fechaPrestamo"));
        if (fechaDevolucionColumn != null) fechaDevolucionColumn.setText(bundle.getString("columna.fechaDevolucion"));
    }
    /**
     * Carga el historial de préstamos con los filtros aplicados.
     *
     * @param dniAlumno    DNI del alumno (puede ser nulo para no filtrar por DNI).
     * @param codigoLibro  Código del libro (puede ser nulo para no filtrar por código).
     * @param fechaInicio  Fecha de inicio del rango de búsqueda (puede ser nula).
     * @param fechaFin     Fecha de fin del rango de búsqueda (puede ser nula).
     */
    private void cargarHistorico(String dniAlumno, Integer codigoLibro, LocalDate fechaInicio, LocalDate fechaFin) {
        try {
            List<HistoricoPrestamo> historico = historicoDAO.obtenerHistorico(dniAlumno, codigoLibro, fechaInicio, fechaFin);
            historicoTable.getItems().setAll(historico);
        } catch (SQLException e) {
            logger.error("Error al cargar el historial de préstamos:", e);
        }
    }
    @FXML
    private void abrirInformeHistorialPrestamos() {
        InformesController.abrirInformeHistorialPrestamos();
    }

    /**
     * Aplica los filtros especificados por el usuario y carga los datos del historial.
     */
    @FXML
    private void filtrarHistorico() {
        // Obtener valores de los filtros
        String dniAlumno = dniAlumnoField.getText().trim().isEmpty() ? null : dniAlumnoField.getText().trim();
        Integer codigoLibro = null;

        if (!codigoLibroField.getText().trim().isEmpty()) {
            try {
                codigoLibro = Integer.parseInt(codigoLibroField.getText().trim());
            } catch (NumberFormatException e) {
                logger.error("El código del libro debe ser un número válido.", e);
                mostrarError("El código del libro debe ser un número válido.");
                return;
            }
        }

        LocalDate fechaInicio = fechaInicioPicker.getValue();
        LocalDate fechaFin = fechaFinPicker.getValue();

        // Cargar datos con los filtros aplicados
        cargarHistorico(dniAlumno, codigoLibro, fechaInicio, fechaFin);
    }

    /**
     * Muestra un mensaje de error en una ventana emergente.
     *
     * @param mensaje Mensaje de error a mostrar.
     */
    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Entrada no válida");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Navega de vuelta al menú principal cargando el archivo FXML de inicio.
     *
     * @param actionEvent Evento de acción que desencadena la navegación.
     */
    @FXML
    public void irAmenu(ActionEvent actionEvent) {
        logger.info("🔄 Volviendo al menú principal...");
        Main.cargarVista("/org/intissar/examenintissar2dein/view/inicio.fxml");
    }
}
