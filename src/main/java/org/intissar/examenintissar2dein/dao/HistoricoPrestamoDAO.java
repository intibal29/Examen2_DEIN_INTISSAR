package org.intissar.examenintissar2dein.dao;

import org.intissar.examenintissar2dein.model.HistoricoPrestamo;
import org.intissar.examenintissar2dein.util.DBConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase DAO (Data Access Object) para acceder a la tabla `Historico_prestamo` en la base de datos.
 * Proporciona métodos para consultar el historial de préstamos filtrando por diversos criterios.
 *
 * <p>Esta clase utiliza conexiones gestionadas a través de {@link DBConnection}.</p>
 *
 * <strong>Recomendación:</strong> Se recomienda manejar transacciones y cerrar las conexiones adecuadamente
 * para evitar problemas de rendimiento.
 *
 * @author Intissar
 * @version 1.0
 */
public class HistoricoPrestamoDAO {
    private static final Logger logger = Logger.getLogger(HistoricoPrestamoDAO.class.getName());

    /**
     * Consulta el historial de préstamos basado en filtros opcionales.
     *
     * <p>Se pueden filtrar los resultados por:</p>
     * - DNI del alumno.
     * - Código del libro.
     * - Rango de fechas (fecha de préstamo o devolución).
     *
     * @param dniAlumno   DNI del alumno (puede ser null para no filtrar).
     * @param codigoLibro Código del libro (puede ser null para no filtrar).
     * @param fechaInicio Fecha inicial del rango de préstamo (puede ser null).
     * @param fechaFin    Fecha final del rango de devolución (puede ser null).
     * @return Una lista de objetos {@link HistoricoPrestamo} que cumplen con los filtros.
     * @throws SQLException Si ocurre algún error durante la consulta a la base de datos.
     */
    public List<HistoricoPrestamo> obtenerHistorico(String dniAlumno, Integer codigoLibro, LocalDate fechaInicio, LocalDate fechaFin) throws SQLException {
        List<HistoricoPrestamo> historico = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM librosexamen2.Historial_prestamo WHERE 1=1");

        // Construcción dinámica del query según los parámetros proporcionados
        if (dniAlumno != null) query.append(" AND dni_estudiante = ?");
        if (codigoLibro != null) query.append(" AND isbn = ?");
        if (fechaInicio != null) query.append(" AND prestamo_online >= ?");
        if (fechaFin != null) query.append(" AND fecha_prestamo <= ?");

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query.toString())) {

            // Asignación de parámetros a la consulta preparada
            int index = 1;
            if (dniAlumno != null) stmt.setString(index++, dniAlumno);
            if (codigoLibro != null) stmt.setInt(index++, codigoLibro);
            if (fechaInicio != null) stmt.setDate(index++, Date.valueOf(fechaInicio));
            if (fechaFin != null) stmt.setDate(index++, Date.valueOf(fechaFin));

            // Ejecución de la consulta
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                historico.add(new HistoricoPrestamo(
                        rs.getInt("id_prestamo"),
                        rs.getString("dni_estudiante"),
                        rs.getInt("isbn"),
                        rs.getBoolean("prestamo_online"),
                        rs.getDate("fecha_prestamo").toLocalDate()
                ));
            }
        } catch (SQLException e) {
            // Registro del error y propagación de la excepción
            logger.log(Level.SEVERE, "Error al obtener histórico de préstamos", e);
            throw e;
        }
        return historico;
    }
}
