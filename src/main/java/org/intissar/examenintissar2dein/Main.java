package org.intissar.examenintissar2dein;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.*;

/**
 * Clase principal de la aplicación JavaFX que gestiona la navegación entre vistas y el manejo de idiomas.
 * Proporciona métodos para cargar vistas y cambiar dinámicamente el idioma de la interfaz.
 *
 * <p>El idioma predeterminado es español, pero puede cambiarse a inglés mediante la interfaz gráfica.</p>
 *
 * <strong>Nota:</strong> Las vistas FXML y los archivos de recursos deben estar ubicados en los directorios
 * especificados para que se carguen correctamente.
 *
 * @author Intissar
 * @version 1.0
 */
public class Main extends Application {

    private static final Logger logger = Logger.getLogger(Main.class.getName()); // Logger
    private static Stage primaryStage;
    private static Locale locale = new Locale("es"); // Idioma predeterminado (Español)
    private static ResourceBundle bundle;

    static {
        try {
            LogManager.getLogManager().reset();
            FileHandler fileHandler = new FileHandler("app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
            logger.setLevel(Level.ALL);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ResourceBundle getBundle() {
        return bundle;
    }

    public static Locale getLocale() {
        return locale;
    }

    /**
     * Metodo principal de JavaFX que se ejecuta al iniciar la aplicación.
     * Establece el idioma inicial y carga la vista principal.
     *
     * @param stage la ventana principal de la aplicación.
     */
    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        logger.info("Iniciando aplicación...");
        cambiarIdioma(locale, true); // Aquí sí recargamos la vista al iniciar la aplicación
        cargarVista("/org/intissar/examenintissar2dein/view/inicio.fxml");
    }


    /**
     * Cambia el idioma de la aplicación y recarga la vista principal.
     *
     * @param nuevaLocale Objeto Locale que define el nuevo idioma.
     */
    public static void cambiarIdioma(Locale nuevaLocale, boolean recargar) {
        locale = nuevaLocale;
        bundle = ResourceBundle.getBundle("org.intissar.examenintissar2dein.languages.lang", locale);
        logger.info("🌍 Idioma cambiado a: " + nuevaLocale.getLanguage());

        // Solo recargar la vista si es necesario
        if (recargar) {
            recargarVista();
        } else {
            actualizarTextos(); // Solo actualizar textos sin recargar
        }
    }



    /**
     * Carga una vista FXML específica en la ventana principal.
     *
     * @param rutaFXML Ruta relativa del archivo FXML.
     */
    public static void cargarVista(String rutaFXML) {
        try {
            logger.info("Cargando vista: " + rutaFXML);
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(rutaFXML), bundle);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(
                    Main.class.getResource("/org/intissar/examenintissar2dein/styles/styles.css")).toExternalForm());

            primaryStage.setTitle(bundle.getString("titulo"));
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();

            // Llamar a actualizarTextos() después de mostrar la escena
            actualizarTextos();

        } catch (Exception e) {
            logger.severe("Error al cargar la vista: " + rutaFXML);
            e.printStackTrace();
        }
    }


    /**
     * Recarga la vista principal para aplicar cambios en el idioma.
     */
    private static void recargarVista() {
        cargarVista("/org/intissar/examenintissar2dein/view/inicio.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista de alumnos.
     *
     * @param event Evento de acción de JavaFX.
     */
    @FXML
    public void handleAlumno(ActionEvent event) {
        logger.info("Navegando a la vista de alumnos.");
        cargarVista("/org/intissar/examenintissar2dein/view/alumno_view.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista del histórico.
     *
     * @param event Evento de acción de JavaFX.
     */
    @FXML
    public void handleHistorico(ActionEvent event) {
        logger.info("Navegando a la vista del histórico.");
        cargarVista("/org/intissar/examenintissar2dein/view/historico_view.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista de libros.
     *
     * @param event Evento de acción de JavaFX.
     */
    @FXML
    public void handleLibro(ActionEvent event) {
        logger.info("Navegando a la vista de libros.");
        cargarVista("/org/intissar/examenintissar2dein/view/libro_view.fxml");
    }

    /**
     * Maneja la acción para mostrar la vista de préstamos.
     *
     * @param event Evento de acción de JavaFX.
     */
    @FXML
    public void handlePrestamo(ActionEvent event) {
        logger.info("Navegando a la vista de préstamos.");
        cargarVista("/org/intissar/examenintissar2dein/view/prestamo_view.fxml");
    }

    @FXML
    private ComboBox<String> idiomaComboBox;

    /**
     * Metodo de inicialización para configurar el ComboBox de selección de idiomas.
     */
    @FXML
    public void initialize() {
        idiomaComboBox.getItems().addAll("Español", "English");
        idiomaComboBox.setValue(locale.getLanguage().equals("en") ? "English" : "Español");

        // Manejar el cambio de idioma sin redirigir la vista
        idiomaComboBox.setOnAction(event -> cambiarIdiomaSeleccionado());
    }


    /**
     * Cambia el idioma según la selección del usuario en el ComboBox.
     */
    private void cambiarIdiomaSeleccionado() {
        String seleccion = idiomaComboBox.getValue();
        Locale nuevoIdioma = seleccion.equals("English") ? new Locale("en") : new Locale("es");
        logger.info("🌍 Usuario cambió el idioma a: " + nuevoIdioma.getLanguage());

        // Cambiar el idioma global en Main sin recargar la vista
        cambiarIdioma(nuevoIdioma, false);

        // Actualizar los textos en la UI sin recargar toda la escena
        actualizarTextos();
    }


    public static void actualizarTextos() {
        if (primaryStage != null && primaryStage.getScene() != null) {
            ResourceBundle bundle = getBundle();
            Scene scene = primaryStage.getScene();

            // Actualizar el título de la ventana
            primaryStage.setTitle(bundle.getString("titulo"));

            // Buscar y actualizar botones según los fx:id definidos en el FXML
            Node alumnoNode = scene.lookup("#btnAlumno");
            Node historicoNode = scene.lookup("#btnHistorico");
            Node libroNode = scene.lookup("#btnLibro");
            Node prestamoNode = scene.lookup("#btnPrestamo");

            if (alumnoNode instanceof Button alumnoButton) {
                alumnoButton.setText(bundle.getString("boton.alumno"));
            } else {
                logger.warning("⚠ No se encontró el botón de Alumno en la escena.");
            }

            if (historicoNode instanceof Button historicoButton) {
                historicoButton.setText(bundle.getString("boton.historico"));
            } else {
                logger.warning("⚠ No se encontró el botón de Histórico en la escena.");
            }

            if (libroNode instanceof Button libroButton) {
                libroButton.setText(bundle.getString("boton.libro"));
            } else {
                logger.warning("⚠ No se encontró el botón de Libro en la escena.");
            }

            if (prestamoNode instanceof Button prestamoButton) {
                prestamoButton.setText(bundle.getString("boton.prestamo"));
            } else {
                logger.warning("⚠ No se encontró el botón de Préstamo en la escena.");
            }

            logger.info("Textos de la UI actualizados con el idioma: {}");
        } else {
            logger.info("❌ Error: No se puede actualizar la UI porque la escena es null.");
        }
    }

    /**
     * metodo principal de la aplicación.
     *
     * @param args Argumentos de línea de comandos.
     */
    public static void main(String[] args) {
        logger.info("Iniciando la aplicación desde el método main.");
        launch(args);
    }
}
