package org.intissar.examenintissar2dein.dao;

import org.intissar.examenintissar2dein.model.Libro;
import org.intissar.examenintissar2dein.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Clase DAO (Data Access Object) para gestionar las operaciones sobre la tabla `Libro` en la base de datos.
 * Proporciona métodos para operaciones CRUD, búsquedas avanzadas y gestión del estado de libros.
 *
 * <p>Se gestiona la conexión a la base de datos a través de {@link DBConnection}.</p>
 *
 * <strong>Recomendación:</strong> Mantener buenas prácticas para cerrar conexiones y manejar excepciones.
 *
 * @version 1.0
 */
public class LibroDAO {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(LibroDAO.class.getName());

    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException Si ocurre un error al obtener la conexión.
     */
    public LibroDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }
    /**
     * Obtiene una lista de todos los libros registrados, con opción de incluir los dados de baja.
     *
     * @param incluirBaja true si se desean incluir libros dados de baja; false en caso contrario.
     * @return Lista de libros obtenidos de la base de datos.
     */
    public List<Libro> obtenerLibros(boolean incluirBaja) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String query = incluirBaja ? "SELECT * FROM librosexamen2.Libro" : "SELECT * FROM librosexamen2.Libro WHERE Baja = 0";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                libros.add(mapearLibro(rs));
            }
        }
        return libros;
    }


    /**
     * Inserta un nuevo libro en la base de datos.
     *
     * @param libro Objeto `Libro` a insertar.
     */
    public void insertarLibro(Libro libro) throws SQLException {
        String query = "INSERT INTO librosexamen2.Libro (Titulo, Autor, Editorial, Estado, Baja) VALUES (?, ?, ?, ?, 0)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, libro.getTitulo());
            stmt.setString(2, libro.getAutor());
            stmt.setString(3, libro.getEditorial());
            stmt.setString(4, libro.getEstado());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("✅ Libro agregado correctamente.");
            } else {
                logger.warning("⚠ No se pudo insertar el libro.");
            }
        }
    }
    /**
     * Reactiva un libro dado de baja.
     *
     * @param codigo Código del libro a reactivar.
     */
    public void reactivarLibro(int codigo) throws SQLException {
        String query = "UPDATE librosexamen2.Libro SET Baja = 0 WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene libros por estado.
     *
     * @param estado Estado del libro.
     * @return Lista de libros filtrados por estado.
     */
    public List<Libro> obtenerLibrosPorEstado(String estado) throws SQLException {
        List<Libro> libros = new ArrayList<>();
        String query = "SELECT * FROM librosexamen2.Libro WHERE Estado = ? AND Baja = 0";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, estado);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    libros.add(mapearLibro(rs));
                }
            }
        }
        return libros;
    }
    /**
     * Elimina un libro de la base de datos.
     *
     * @param codigo Código del libro a eliminar.
     * @return true si la eliminación fue exitosa, false si no.
     */
    public boolean eliminarLibro(int codigo) throws SQLException {
        String query = "UPDATE librosexamen2.Libro SET Baja = 1 WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            return stmt.executeUpdate() > 0;
        }
    }

    /**
     * Modifica un libro en la base de datos, permitiendo actualizar solo los campos que cambian.
     *
     * @param libro Objeto `Libro` con los cambios.
     */
    public void modificarLibro(Libro libro) throws SQLException {
        String query = "UPDATE librosexamen2.Libro SET Titulo = COALESCE(?, Titulo), "
                + "Autor = COALESCE(?, Autor), Editorial = COALESCE(?, Editorial), "
                + "Estado = COALESCE(?, Estado) WHERE isbn = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, libro.getTitulo().isEmpty() ? null : libro.getTitulo());
            stmt.setString(2, libro.getAutor().isEmpty() ? null : libro.getAutor());
            stmt.setString(3, libro.getEditorial().isEmpty() ? null : libro.getEditorial());
            stmt.setString(4, libro.getEstado().isEmpty() ? null : libro.getEstado());
            stmt.setInt(5, libro.getCodigo());

            int filasAfectadas = stmt.executeUpdate();
            if (filasAfectadas > 0) {
                logger.info("✅ Libro modificado correctamente.");
            } else {
                logger.warning("⚠ No se pudo modificar el libro.");
            }
        }
    }
    /**
     * Verifica si un libro está disponible para préstamo.
     *
     * @param codigo El código del libro.
     * @return true si el libro está disponible; false en caso contrario.
     */
    // Verificar si un libro está disponible para préstamo
    public boolean estaDisponible(int codigo) {
        String query = "SELECT Baja FROM librosexamen2.Libro WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return !rs.getBoolean("Baja");
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar disponibilidad del libro con código: " + codigo, e);
        }
        return false;
    }
    /**
     * Actualiza el estado de un libro en la base de datos.
     *
     * @param codigoLibro  El código del libro a actualizar.
     * @param nuevoEstado  El nuevo estado que se asignará al libro.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public void actualizarEstadoLibro(int codigoLibro, String nuevoEstado) throws SQLException {
        String query = "UPDATE librosexamen2.Libro SET Estado = ? WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, codigoLibro);
            stmt.executeUpdate();
        }
    }

    /**
     * Marca un libro como dado de baja en el sistema.
     * Antes de darlo de baja, verifica si existe un préstamo activo para el libro
     * y lo registra en el historial de préstamos. Luego elimina el préstamo y marca el libro como dado de baja.
     *
     * @param codigo El código del libro a dar de baja.
     * @throws SQLException Si el libro no existe, si no tiene préstamo activo,
     *                      o si ocurre un error al realizar las operaciones en la base de datos.
     */
    public void marcarBaja(int codigo) throws SQLException {
        if (!existeLibro(codigo)) {
            throw new SQLException("El libro con código " + codigo + " no existe.");
        }

        // Obtener el DNI del alumno asociado al préstamo antes de hacer la baja
        String queryObtenerDni = "SELECT dni_estudisnte FROM librosexamen2.Prestamo WHERE isbn = ?";
        String dniAlumno = null;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtDni = conn.prepareStatement(queryObtenerDni)) {

            stmtDni.setInt(1, codigo);
            try (ResultSet rs = stmtDni.executeQuery()) {
                if (rs.next()) {
                    dniAlumno = rs.getString("dni_alumno");
                } else {
                    throw new SQLException("No se encontró un préstamo activo para este libro.");
                }
            }
        }

        // Insertar en el historial de préstamos
        String queryHistorico = "INSERT INTO librosexamen2.Historico_prestamo (isbn, fecha_prestamo) " +
                "SELECT isbn FROM librosexamen2.Libro WHERE isbn = ?";

        // Eliminar el préstamo activo
        String queryEliminarPrestamo = "DELETE FROM librosexamen2.Prestamo WHERE isbn = ?";

        // Marcar el libro como dado de baja
        String queryBaja = "UPDATE librosexamen2.Libro SET Baja = 1 WHERE isbn = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtHist = conn.prepareStatement(queryHistorico);
             PreparedStatement stmtEliminarPrestamo = conn.prepareStatement(queryEliminarPrestamo);
             PreparedStatement stmtBaja = conn.prepareStatement(queryBaja)) {

            // Insertar en el historial
            stmtHist.setString(1, dniAlumno);
            stmtHist.setInt(2, codigo);
            int filasHistorial = stmtHist.executeUpdate();

            if (filasHistorial == 0) {
                throw new SQLException("No se pudo mover el préstamo al historial.");
            }

            // Eliminar de la tabla de préstamos activos
            stmtEliminarPrestamo.setInt(1, codigo);
            stmtEliminarPrestamo.executeUpdate();

            // Marcar el libro como dado de baja
            stmtBaja.setInt(1, codigo);
            stmtBaja.executeUpdate();

        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al dar de baja el libro con código: " + codigo, e);
            throw e;
        }
    }

    /**
     * Verifica si un libro existe en la base de datos.
     *
     * @param codigo Código del libro.
     * @return true si el libro existe, false en caso contrario.
     */
    public boolean existeLibro(int codigo) {
        String query = "SELECT COUNT(*) FROM librosexamen2.Libro WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codigo);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar existencia del libro con código: " + codigo, e);
        }
        return false;
    }

    /**
     * Mapear un {@link ResultSet} a un objeto {@link Libro}.
     *
     * @param rs El objeto ResultSet con los datos del libro.
     * @return Un objeto Libro.
     * @throws SQLException Si ocurre un error al acceder a los datos.
     */
    // Mapear ResultSet a objeto Libro
    private Libro mapearLibro(ResultSet rs) throws SQLException {
        return new Libro(
                rs.getInt("isbn"),
                rs.getString("Titulo"),
                rs.getString("Autor"),
                rs.getString("Editorial"),
                rs.getString("Estado"),
                rs.getBoolean("Baja")
        );

    }
}
