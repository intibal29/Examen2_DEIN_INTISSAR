package org.intissar.examenintissar2dein.dao;

import org.intissar.examenintissar2dein.model.Prestamo;
import org.intissar.examenintissar2dein.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 * Clase DAO (Data Access Object) para la gestión de préstamos en una base de datos.
 * Proporciona métodos para realizar operaciones CRUD y manejar el estado de los libros.
 * Se conecta a la base de datos a través de la clase {@link DBConnection}.
 *
 * @author intissar
 * @version 1.0
 */
public class PrestamoDAO {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(PrestamoDAO.class.getName());
    /**
     * Constructor que inicializa la conexión a la base de datos.
     *
     * @throws SQLException si no se puede establecer la conexión.
     */
    public PrestamoDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }
    /**
     * Obtiene una lista de todos los préstamos registrados en la base de datos.
     *
     * @return Lista de objetos {@link Prestamo}.
     */
    // Obtener todos los préstamos
    public List<Prestamo> obtenerPrestamos() {
        List<Prestamo> prestamos = new ArrayList<>();
        String query = "SELECT * FROM librosexamen2.Prestamo";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestamos.add(new Prestamo(
                        rs.getInt("id_prestamo"),
                        rs.getString("dni_alumno"),
                        rs.getInt("codigo_libro"),
                        rs.getDate("fecha_prestamo").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los préstamos", e);
        }
        return prestamos;
    }
    /**
     * Registra un nuevo préstamo en la base de datos.
     *
     * @param prestamo Objeto {@link Prestamo} que contiene la información del préstamo.
     * @throws SQLException si el libro ya está prestado o si ocurre un error al registrar el préstamo.
     */
    public void registrarPrestamo(Prestamo prestamo) throws SQLException {
        if (esLibroPrestado(prestamo.getCodigoLibro())) {
            throw new SQLException("El libro ya está prestado y no se puede registrar.");
        }

        String query = "INSERT INTO librosexamen2.Prestamo (dni_estudiante, isbn, fecha_prestamo) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, prestamo.getDniAlumno());
            stmt.setInt(2, prestamo.getCodigoLibro());
            stmt.setDate(3, Date.valueOf(prestamo.getFechaPrestamo()));
            stmt.executeUpdate();

            // Actualizar el estado del libro
            actualizarEstadoLibro(prestamo.getCodigoLibro(), "Prestado");
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al registrar el préstamo", e);
            throw new SQLException("Error al registrar el préstamo", e);
        }
    }
    /**
     * Elimina un préstamo por su ID.
     *
     * @param idPrestamo ID del préstamo a eliminar.
     * @return true si el préstamo fue eliminado, false en caso contrario.
     * @throws SQLException si ocurre un error al eliminar el préstamo.
     */
    public boolean eliminarPrestamo(int idPrestamo) throws SQLException {
        String query = "DELETE FROM librosexamen2.Prestamo WHERE id_prestamo = ?";
        try (Connection conn = DBConnection.getConnection(); // ✅ Nueva conexión
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idPrestamo);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                logger.info("✅ Préstamo eliminado con éxito. ID: " + idPrestamo);
            } else {
                logger.info("⚠ No se eliminó ningún préstamo. Verifica si el ID existe.");
            }
            return rowsAffected > 0;

        } catch (SQLException e) {
            logger.info("❌ Error al eliminar el préstamo con ID: " + e);
            throw e;
        }
    }



    /**
     * Obtiene una lista de préstamos activos en la base de datos.
     *
     * @return Lista de objetos {@link Prestamo} correspondientes a préstamos activos.
     */
    // Obtener todos los préstamos activos
    public List<Prestamo> obtenerPrestamosActivos() {
        List<Prestamo> prestamosActivos = new ArrayList<>();
        String query = "SELECT * FROM librosexamen2.Prestamo WHERE fecha_prestamo IS NOT NULL";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestamosActivos.add(new Prestamo(
                        rs.getInt("id_prestamo"),
                        rs.getString("dni_alumno"),
                        rs.getInt("codigo_libro"),
                        rs.getDate("fecha_prestamo").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener los préstamos activos", e);
        }
        return prestamosActivos;
    }
    /**
     * Marca un préstamo como devuelto, moviéndolo al historial de préstamos.
     *
     * @param idPrestamo     ID del préstamo a marcar como devuelto.
     * @param fechaDevolucion Fecha de devolución del libro.
     * @throws SQLException si ocurre un error durante el proceso.
     */
    // Marcar préstamo como devuelto y moverlo al histórico
    public void marcarComoDevuelto(int idPrestamo, Date fechaDevolucion) throws SQLException {
        String querySeleccion = "SELECT * FROM librosexamen2.Prestamo WHERE id_prestamo = ?";
        String queryInsertHistorico = "INSERT INTO librosexamen2.Historico_prestamo (dni_estudiante, isbn, fecha_devolucion) VALUES (?, ?, ?)";
        String queryEliminar = "DELETE FROM librosexamen2.Prestamo WHERE id_prestamo = ?";
        String queryActualizarLibro = "UPDATE librosexamen2.Libro SET estado = 'Disponible' WHERE isbn = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmtSelect = conn.prepareStatement(querySeleccion);
             PreparedStatement stmtInsertHistorico = conn.prepareStatement(queryInsertHistorico);
             PreparedStatement stmtEliminar = conn.prepareStatement(queryEliminar);
             PreparedStatement stmtActualizarLibro = conn.prepareStatement(queryActualizarLibro)) {

            stmtSelect.setInt(1, idPrestamo);
            ResultSet rs = stmtSelect.executeQuery();

            if (rs.next()) {
                stmtInsertHistorico.setString(1, rs.getString("dni_alumno"));
                stmtInsertHistorico.setInt(2, rs.getInt("codigo_libro"));
                stmtInsertHistorico.setDate(3, rs.getDate("fecha_prestamo"));
                stmtInsertHistorico.setDate(4, new Date(fechaDevolucion.getTime()));
                stmtInsertHistorico.executeUpdate();

                stmtEliminar.setInt(1, idPrestamo);
                stmtEliminar.executeUpdate();

                stmtActualizarLibro.setInt(1, rs.getInt("codigo_libro"));
                stmtActualizarLibro.executeUpdate();
            }
        } catch (SQLException e)  {
            throw new SQLException("Error al marcar como devuelto el préstamo con ID: " + idPrestamo, e);
        }
    }

    /**
     * Actualiza el estado de un libro a "Prestado" en la base de datos.
     *
     * @param codigoLibro El código del libro que se marcará como prestado.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    private void actualizarEstadoLibroPrestado(int codigoLibro) throws SQLException {
        String query = "UPDATE librosexamen2.Libro SET estado = 'Prestado' WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codigoLibro);
            stmt.executeUpdate();
        }
    }

    /**
     * Actualiza el estado de un libro a "Disponible" en la base de datos.
     *
     * @param codigoLibro El código del libro que se marcará como disponible.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    private void actualizarEstadoLibroDisponible(int codigoLibro) throws SQLException {
        String query = "UPDATE librosexamen2.Libro SET estado = 'Disponible' WHERE isbn = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, codigoLibro);
            stmt.executeUpdate();
        }
    }

    /**
     * Obtiene el historial de préstamos desde la base de datos.
     *
     * @return Una lista de objetos {@link Prestamo} representando el historial de préstamos.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public List<Prestamo> obtenerPrestamosHistorico() throws SQLException {
        List<Prestamo> prestamosHistoricos = new ArrayList<>();
        String query = "SELECT * FROM librosexamen2.Historico_prestamo";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                prestamosHistoricos.add(new Prestamo(
                        rs.getInt("id_prestamo"),
                        rs.getString("dni_alumno"),
                        rs.getInt("codigo_libro"),
                        rs.getDate("fecha_prestamo").toLocalDate(),
                        rs.getDate("fecha_devolucion") != null ? rs.getDate("fecha_devolucion").toLocalDate() : null
                ));
            }
        }
        return prestamosHistoricos;
    }

    /**
     * Verifica si un libro está prestado.
     *
     * @param codigoLibro Código del libro a verificar.
     * @return true si el libro está prestado, false en caso contrario.
     */
    // Verificar si un libro está prestado
    public boolean esLibroPrestado(int codigoLibro) {
        String query = "SELECT COUNT(*) FROM librosexamen2.Prestamo WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, codigoLibro);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar si el libro está prestado", e);
        }
        return false;
    }
    /**
     * Actualiza el estado de un libro en la base de datos.
     *
     * @param codigoLibro El código del libro que se desea actualizar.
     * @param estado El nuevo estado que se asignará al libro.
     * @return {@code true} si la actualización se realizó con éxito, {@code false} en caso contrario.
     * @throws SQLException Si ocurre un error al ejecutar la consulta SQL.
     */
    public boolean actualizarEstadoLibro(int codigoLibro, String estado) throws SQLException {
        String sql = "UPDATE librosexamen2.Libro SET estado = ? WHERE isbn = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, codigoLibro);
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        }
    }

    /**
     * Verifica si un préstamo está activo, es decir, si tiene una fecha de préstamo registrada.
     *
     * @param idPrestamo El identificador del préstamo que se desea verificar.
     * @return {@code true} si el préstamo está activo, {@code false} en caso contrario.
     */
    public boolean esPrestamoActivo(int idPrestamo) {
        String query = "SELECT COUNT(*) FROM librosexamen2.Libro WHERE id_prestamo = ? AND fecha_prestamo IS NOT NULL";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, idPrestamo);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al verificar si el préstamo está activo con ID: " + idPrestamo, e);
        }
        return false;
    }
}
