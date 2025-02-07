package org.intissar.examenintissar2dein.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase utilitaria para gestionar la conexión a una base de datos mediante el patrón Singleton.
 * Carga la configuración desde un archivo `configuration.properties`.
 *
 * <p>Ofrece métodos para obtener y cerrar la conexión de manera segura.</p>
 *
 * <strong>Nota:</strong> Asegúrate de tener configurado el archivo `configuration.properties`
 * con las propiedades `db.url`, `db.user`, y `db.password` en el directorio raíz del proyecto.
 *
 * @author Intissar
 * @version 1.0
 */
public class DBConnection {
    private static String URL;
    private static String USER;
    private static String PASSWORD;

    private static volatile Connection connection;

    // Bloque estático para cargar las propiedades de configuración
    static {
        try (FileInputStream input = new FileInputStream("configuration.properties")) {
            Properties properties = new Properties();
            properties.load(input);
            URL = properties.getProperty("db.url");
            USER = properties.getProperty("db.user");
            PASSWORD = properties.getProperty("db.password");
        } catch (IOException e) {
            throw new RuntimeException("Error al cargar configuración de la base de datos", e);
        }
    }

    // Constructor privado para evitar instancias
    private DBConnection() {
    }

    /**
     * Obtiene una conexión a la base de datos.
     *
     * @return Objeto Connection activo.
     * @throws SQLException si ocurre algún error al establecer la conexión.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            synchronized (DBConnection.class) {
                if (connection == null || connection.isClosed()) {
                    connection = DriverManager.getConnection(URL, USER, PASSWORD);
                    System.out.println("Conexión exitosa a la base de datos.");
                }
            }
        }
        return connection;
    }

    /**
     * Cierra la conexión activa con la base de datos si existe.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Conexión cerrada correctamente.");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
                throw new RuntimeException("Error cerrando la conexión a la base de datos", e);
            }
        }
    }
}
