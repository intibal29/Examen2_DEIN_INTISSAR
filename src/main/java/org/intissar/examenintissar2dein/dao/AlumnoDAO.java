package org.intissar.examenintissar2dein.dao;

import org.intissar.examenintissar2dein.model.Alumno;
import org.intissar.examenintissar2dein.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Clase DAO (Data Access Object) para acceder y manipular la tabla `Alumno` en la base de datos.
 * Esta clase proporciona métodos para realizar operaciones CRUD (Create, Read, Update, Delete) sobre los alumnos.
 *
 * <p>Se gestiona la conexión a la base de datos a través de {@link DBConnection}.</p>
 *
 * <strong>Recomendación:</strong> Mantener buenas prácticas para cerrar las conexiones y manejar errores.
 *
 * @author Intissar
 * @version 1.0
 */
public class AlumnoDAO {
    private final Connection connection;
    private static final Logger logger = Logger.getLogger(AlumnoDAO.class.getName());

    /**
     * Constructor de la clase que inicializa la conexión a la base de datos.
     *
     * @throws SQLException Si ocurre un error al obtener la conexión.
     */
    public AlumnoDAO() throws SQLException {
        connection = DBConnection.getConnection();
    }

    /**
     * Obtiene una lista de todos los alumnos registrados en la base de datos.
     *
     * @return Una lista de objetos {@link Alumno}.
     */
    public List<Alumno> obtenerAlumnos() {
        List<Alumno> alumnos = new ArrayList<>();
        String query = "SELECT * FROM librosexamen2.Estudiante";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                alumnos.add(new Alumno(
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido1"),
                        rs.getString("apellido2")
                ));
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al obtener alumnos", e);
        }
        return alumnos;
    }

    /**
     * Busca un alumno por su DNI en la base de datos.
     *
     * @param dni El DNI del alumno a buscar.
     * @return Un {@link Optional} que contiene el alumno si se encuentra, o vacío si no existe.
     */
    public Optional<Alumno> buscarAlumnoPorDni(String dni) {
        String query = "SELECT * FROM librosexamen2.Estudiantee WHERE dni_estudisnte = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, dni);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Alumno(
                            rs.getString("dni"),
                            rs.getString("nombre"),
                            rs.getString("apellido1"),
                            rs.getString("apellido2")
                    ));
                }
            }
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al buscar alumno con DNI: " + dni, e);
        }
        return Optional.empty();
    }

    /**
     * Registra un nuevo alumno en la base de datos.
     *
     * @param nuevoAlumno El objeto {@link Alumno} que contiene los datos del nuevo alumno.
     * @throws SQLException Si ocurre un error durante la inserción.
     */
    public void registrarAlumno(Alumno nuevoAlumno) throws SQLException {
        String sql = "INSERT INTO librosexamen2.Estudiante (dni_estudiante, nombre, primerApellido, segundApellido) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, nuevoAlumno.getDni());
            statement.setString(2, nuevoAlumno.getNombre());
            statement.setString(3, nuevoAlumno.getApellido1());
            statement.setString(4, nuevoAlumno.getApellido2());
            statement.executeUpdate();
        }
    }

    /**
     * Modifica los datos de un alumno existente en la base de datos.
     *
     * @param seleccionado El objeto {@link Alumno} que contiene los nuevos datos del alumno.
     * @throws SQLException Si ocurre un error durante la actualización.
     */
    public void modificarAlumno(Alumno seleccionado) throws SQLException {
        String sql = "UPDATE librosexamen2.Estudiante SET nombre = ?, primerApellido = ?, segundApellido = ? WHERE dni_estudiante = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, seleccionado.getNombre());
            statement.setString(2, seleccionado.getApellido1());
            statement.setString(3, seleccionado.getApellido2());
            statement.setString(4, seleccionado.getDni());
            statement.executeUpdate();
        }
    }

    /**
     * Elimina un alumno de la base de datos basado en su DNI.
     *
     * @param dni El DNI del alumno a eliminar.
     * @return true si el alumno fue eliminado exitosamente; false en caso contrario.
     */
    public boolean eliminarAlumno(String dni) {
        String query = "DELETE FROM librosexamen2.Estudiante WHERE dni_estudiante = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, dni);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error al eliminar alumno con DNI: " + dni, e);
            return false;
        }
    }
}
