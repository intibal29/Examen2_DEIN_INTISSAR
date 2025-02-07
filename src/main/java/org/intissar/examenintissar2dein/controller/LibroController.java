package org.intissar.examenintissar2dein.controller;

import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import org.intissar.examenintissar2dein.Main;
import org.intissar.examenintissar2dein.dao.LibroDAO;
import org.intissar.examenintissar2dein.model.Libro;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Controlador para gestionar la vista de libros en la aplicación.
 * Permite realizar operaciones como agregar, modificar, eliminar y filtrar libros,
 * así como interactuar con la base de datos y generar informes.
 */
public class LibroController {
    /**
     * Logger para registrar eventos y errores en la clase.
     */
    private static final Logger logger = LoggerFactory.getLogger(LibroController.class);

    @FXML
    private TextField tituloField, autorField, editorialField;
    @FXML
    private ComboBox<String> estadoComboBox, idiomaComboBox,filtroEstadoComboBox;
    @FXML
    private TableView<Libro> librosTable;
    @FXML
    private TableColumn<Libro, Integer> codigoColumn;
    @FXML
    private TableColumn<Libro, String> tituloColumn, autorColumn, estadoColumn;

    // Etiquetas y botones
    @FXML private Label tituloLabel;
    @FXML private Button agregarLibroButton, modificarLibroButton, eliminarLibro,
            verBajaButton, reactivarLibroButton, helpButton, informeButton, MenuButton;

    private Locale locale = new Locale("es"); // Idioma predeterminado (Español)
        private ResourceBundle bundle;

        private ObservableList<Libro> librosList;
        private final LibroDAO libroDAO;
    /**
     * Constructor de la clase `LibroController`.
     * @throws SQLException Si ocurre un error de conexión con la base de datos.
     */
        public LibroController() throws SQLException {
            libroDAO = new LibroDAO();
        }
    /**
     * Inicializa la vista configurando elementos UI y cargando la lista de libros.
     */
    @FXML
    public void initialize() {
        logger.info("Inicializando la vista de libros...");

        // Configurar ComboBox de idioma
        bundle = Main.getBundle();

        idiomaComboBox.getItems().addAll("Español", "English");
        idiomaComboBox.setValue(Main.getLocale().getLanguage().equals("en") ? "English" : "Español");
        idiomaComboBox.setOnAction(event -> cambiarIdioma());

        // Cargar textos desde el archivo de idiomas
        actualizarTextos();

        // Configurar ComboBox con los estados disponibles
        if (estadoComboBox != null) {
            estadoComboBox.setItems(FXCollections.observableArrayList(
                    bundle.getString("estado.nuevo"),
                    bundle.getString("estado.usadoNuevo"),
                    bundle.getString("estado.usadoSeminuevo"),
                    bundle.getString("estado.usadoEstropeado"),
                    bundle.getString("estado.restaurado")
            ));
        }

        // Configurar columnas de la tabla
        codigoColumn.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        tituloColumn.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        autorColumn.setCellValueFactory(new PropertyValueFactory<>("autor"));
        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("estado"));

        librosList = FXCollections.observableArrayList();
        librosTable.setItems(librosList);
        cargarLibros();
    }

    /**
     * Cambia el idioma de la interfaz basado en la selección del ComboBox.
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
    /**
     * Actualiza los textos de la interfaz según el idioma seleccionado.
     */
    private void actualizarTextos() {
        bundle = Main.getBundle();

        // Actualizar título
        if (tituloLabel != null) tituloLabel.setText(bundle.getString("titulo.libros"));

        // Actualizar botones
        if (agregarLibroButton != null) agregarLibroButton.setText(bundle.getString("boton.agregarLibro"));
        if (modificarLibroButton != null) modificarLibroButton.setText(bundle.getString("boton.modificarLibro"));
        if (eliminarLibro != null) eliminarLibro.setText(bundle.getString("boton.eliminarLibro"));
        if (verBajaButton != null) verBajaButton.setText(bundle.getString("boton.verLibrosBaja"));
        if (reactivarLibroButton != null) reactivarLibroButton.setText(bundle.getString("boton.reactivarLibro"));
        if (helpButton != null) helpButton.setText(bundle.getString("boton.ayuda"));
        if (informeButton != null) informeButton.setText(bundle.getString("boton.generarInforme"));
        //if (MenuButton != null) MenuButton.setText(bundle.getString("boton.menu"));

        // Actualizar ComboBox de estados
        if (estadoComboBox != null) {
            estadoComboBox.setItems(FXCollections.observableArrayList(
                    bundle.getString("estado.nuevo"),
                    bundle.getString("estado.usadoNuevo"),
                    bundle.getString("estado.usadoSeminuevo"),
                    bundle.getString("estado.usadoEstropeado"),
                    bundle.getString("estado.restaurado")
            ));
        }

        // Actualizar placeholders de los campos de entrada
        if (tituloField != null) tituloField.setPromptText(bundle.getString("placeholder.titulo"));
        if (autorField != null) autorField.setPromptText(bundle.getString("placeholder.autor"));
        if (editorialField != null) editorialField.setPromptText(bundle.getString("placeholder.editorial"));

        // Actualizar los nombres de las columnas de la tabla
        if (codigoColumn != null) codigoColumn.setText(bundle.getString("columna.codigo"));
        if (tituloColumn != null) tituloColumn.setText(bundle.getString("columna.titulo"));
        if (autorColumn != null) autorColumn.setText(bundle.getString("columna.autor"));
        if (estadoColumn != null) estadoColumn.setText(bundle.getString("columna.estado"));
    }

    /**
     * Agrega un nuevo libro a la base de datos si los campos son válidos.
     */

    @FXML
    private void agregarLibro() {
        try {
            if (validarCampos()) {
                Libro libro = new Libro();
                libro.setTitulo(tituloField.getText());
                libro.setAutor(autorField.getText());
                libro.setEditorial(editorialField.getText());
                libro.setEstado(estadoComboBox.getValue());

                libroDAO.insertarLibro(libro);
                mostrarAlerta("Éxito", "Libro agregado correctamente.", Alert.AlertType.INFORMATION);
                cargarLibros();
                limpiarCampos();
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo agregar el libro.", Alert.AlertType.ERROR);
        }
    }
    @FXML
    private void filtrarPorEstado() {
        String estadoSeleccionado = filtroEstadoComboBox.getValue();
        if (estadoSeleccionado == null || estadoSeleccionado.equals("Todos")) {
            cargarLibros();
            return;
        }

        try {
            List<Libro> librosFiltrados = libroDAO.obtenerLibrosPorEstado(estadoSeleccionado);
            librosList.clear();
            librosList.addAll(librosFiltrados);
            librosTable.refresh();
        } catch (SQLException e) {

            mostrarAlerta("Error", "No se pudieron cargar los libros filtrados.", Alert.AlertType.ERROR);
        }
    }
    /**
     * Elimina un libro seleccionado de la base de datos.
     */
    @FXML
    private void eliminarLibro() {
        Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
        if (libroSeleccionado == null) {
            mostrarAlerta("Error", "Seleccione un libro para eliminar.", Alert.AlertType.WARNING);
            return;
        }
        try {
            libroDAO.eliminarLibro(libroSeleccionado.getCodigo());
            cargarLibros();
            mostrarAlerta("Éxito", "Libro eliminado correctamente.", Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            logger.error("Error al eliminar el libro", e);
            mostrarAlerta("Error", "No se pudo eliminar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void reactivarLibro() {
        try {
            Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null) {
                libroDAO.reactivarLibro(libroSeleccionado.getCodigo());
                mostrarAlerta("Éxito", "Libro reactivado correctamente.", Alert.AlertType.INFORMATION);
                cargarLibros();
            } else {
                mostrarAlerta("Error", "Seleccione un libro para reactivarlo.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo reactivar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void mostrarLibrosDadosDeBaja() {
        try {
            List<Libro> librosBaja = libroDAO.obtenerLibros(true);
            librosList.clear();
            librosList.addAll(librosBaja);
            librosTable.refresh();
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudieron cargar los libros dados de baja.", Alert.AlertType.ERROR);
        }
    }
    /**
     * Modifica los datos de un libro seleccionado.
     */
    @FXML
    private void modificarLibro() {
        try {
            Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null && validarCampos()) {
                libroSeleccionado.setTitulo(tituloField.getText());
                libroSeleccionado.setAutor(autorField.getText());
                libroSeleccionado.setEditorial(editorialField.getText());
                libroSeleccionado.setEstado(estadoComboBox.getValue());

                libroDAO.modificarLibro(libroSeleccionado);
                mostrarAlerta("Éxito", "Libro modificado correctamente.", Alert.AlertType.INFORMATION);
                cargarLibros();
                limpiarCampos();
            } else {
                mostrarAlerta("Error", "Seleccione un libro para modificar.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo modificar el libro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void marcarBajaLibro() {
        try {
            Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
            if (libroSeleccionado != null) {
                libroDAO.marcarBaja(libroSeleccionado.getCodigo());
                mostrarAlerta("Éxito", "Libro marcado como baja.", Alert.AlertType.INFORMATION);
                cargarLibros();
            } else {
                mostrarAlerta("Error", "Seleccione un libro para marcarlo como baja.", Alert.AlertType.WARNING);
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo marcar el libro como baja.", Alert.AlertType.ERROR);
        }
    }
    /**
     * Carga la lista de libros desde la base de datos.
     */
    @FXML
    private void cargarLibros() {
        try {
            logger.info("Cargando libros desde la base de datos...");
            List<Libro> libros = libroDAO.obtenerLibros(false);

            if (libros == null || libros.isEmpty()) {
                System.err.println("⚠ No se encontraron libros en la base de datos.");
                return;
            }

            // Asegurar que librosList está inicializado antes de manipularlo
            if (librosList == null) {
                librosList = FXCollections.observableArrayList();
                librosTable.setItems(librosList);
            }

            librosList.setAll(libros); //  Mejor que clear() + addAll()
            librosTable.refresh();

            logger.info(" Libros cargados correctamente.");
            System.out.println(" Libros obtenidos: " + libros.size());
            for (Libro libro : libros) {
                System.out.println(" Título: " + libro.getTitulo() + " | Autor: " + libro.getAutor());
            }

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(" Error al cargar los libros", e);
            mostrarAlerta("Error", "No se pudieron cargar los libros.", Alert.AlertType.ERROR);
        }
    }


    private void limpiarCampos() {
        tituloField.clear();
        autorField.clear();
        editorialField.clear();
        estadoComboBox.getSelectionModel().clearSelection();
    }
    @FXML
    /**
     * Abre la ventana de ayuda sobre libros.
     */
    private void abrirAyuda() {
        try {
            logger.info("Cargando ayuda ");
            // Establecer el archivo de ayuda que se quiere abrir
            AyudaHTMLController.setArchivoAyuda("libro.html");

            // Cargar la ventana de ayuda
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/intissar/proyecto2/view/AyudaHTML.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ayuda - Libros");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            logger.info("error");

            mostrarAlerta("Error", "No se pudo abrir la ayuda", Alert.AlertType.ERROR);
        }
    }
    /**
     * Genera un informe de libros en la base de datos.
     */
    @FXML
    private void generarInforme() {
        InformesController.generarReporteLibros();
    }
    /**
     * Valida que los campos de entrada no estén vacíos.
     */
    private boolean validarCampos() {
        if (tituloField.getText().isEmpty() || autorField.getText().isEmpty() ||
                editorialField.getText().isEmpty() || estadoComboBox.getValue() == null) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }
    /**
     * Muestra una alerta en pantalla.
     * @param titulo Título de la alerta.
     * @param mensaje Mensaje a mostrar en la alerta.
     * @param tipo Tipo de alerta (Información, Advertencia o Error).
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void seleccionarLibro(MouseEvent event) {
        Libro libroSeleccionado = librosTable.getSelectionModel().getSelectedItem();
        if (libroSeleccionado != null) {
            tituloField.setText(libroSeleccionado.getTitulo());
            autorField.setText(libroSeleccionado.getAutor());
            editorialField.setText(libroSeleccionado.getEditorial());
            estadoComboBox.setValue(libroSeleccionado.getEstado());
        }
    }

    /**
     * Metodo para volver al menú principal cargando el archivo FXML de inicio.
     *
     * @param actionEvent Evento de acción que desencadena la navegación.
     */
    @FXML
    public void irAmenu(ActionEvent actionEvent) {
        logger.info("🔄 Volviendo al menú principal...");
        Main.cargarVista("/org/intissar/examenintissar2dein/view/inicio.fxml");
    }
}
