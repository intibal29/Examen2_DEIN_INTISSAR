package org.intissar.examenintissar2dein.model;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase que representa un préstamo de un libro a un alumno. Contiene información
 * sobre el ID del préstamo, el DNI del alumno, el código del libro, las fechas
 * de préstamo y devolución.
 *
 * <p>Proporciona validaciones para asegurar la correcta asignación de valores, así como
 * métodos auxiliares para el manejo de fechas y propiedades compatibles con JavaFX.</p>
 *
 * @author Intissar
 * @version 1.0
 */
public class Prestamo {
    private final IntegerProperty idPrestamo;
    private final StringProperty dniAlumno;
    private final IntegerProperty codigoLibro;
    private final ObjectProperty<LocalDate> fechaPrestamo;
    private final ObjectProperty<LocalDate> fechaDevolucion;

    /**
     * Constructor por defecto que inicializa las propiedades del préstamo.
     */
    public Prestamo() {
        this.idPrestamo = new SimpleIntegerProperty();
        this.dniAlumno = new SimpleStringProperty();
        this.codigoLibro = new SimpleIntegerProperty();
        this.fechaPrestamo = new SimpleObjectProperty<>();
        this.fechaDevolucion = new SimpleObjectProperty<>();
    }

    /**
     * Constructor para crear un préstamo sin fecha de devolución.
     *
     * @param idPrestamo ID del préstamo (debe ser mayor a 0).
     * @param dniAlumno DNI del alumno (no puede ser nulo).
     * @param codigoLibro Código del libro prestado (debe ser mayor a 0).
     * @param fechaPrestamo Fecha del préstamo (no puede ser nula).
     * @throws IllegalArgumentException si alguno de los parámetros obligatorios es inválido.
     */
    public Prestamo(int idPrestamo, String dniAlumno, int codigoLibro, LocalDate fechaPrestamo) {
        if (dniAlumno == null || fechaPrestamo == null) {
            throw new IllegalArgumentException("DNI y Fecha de préstamo no pueden ser nulos.");
        }
        this.idPrestamo = new SimpleIntegerProperty(idPrestamo);
        this.dniAlumno = new SimpleStringProperty(dniAlumno);
        this.codigoLibro = new SimpleIntegerProperty(codigoLibro);
        this.fechaPrestamo = new SimpleObjectProperty<>(fechaPrestamo);
        this.fechaDevolucion = new SimpleObjectProperty<>();
    }

    /**
     * Constructor para crear un préstamo con fecha de devolución.
     *
     * @param idPrestamo ID del préstamo (debe ser mayor a 0).
     * @param dniAlumno DNI del alumno (no puede ser nulo).
     * @param codigoLibro Código del libro prestado (debe ser mayor a 0).
     * @param fechaPrestamo Fecha del préstamo (no puede ser nula).
     * @param fechaDevolucion Fecha de devolución (puede ser nula).
     */
    public Prestamo(int idPrestamo, String dniAlumno, int codigoLibro, LocalDate fechaPrestamo, LocalDate fechaDevolucion) {
        if (dniAlumno == null || fechaPrestamo == null) {
            throw new IllegalArgumentException("DNI y Fecha de préstamo no pueden ser nulos.");
        }
        this.idPrestamo = new SimpleIntegerProperty(idPrestamo);
        this.dniAlumno = new SimpleStringProperty(dniAlumno);
        this.codigoLibro = new SimpleIntegerProperty(codigoLibro);
        this.fechaPrestamo = new SimpleObjectProperty<>(fechaPrestamo);
        this.fechaDevolucion = new SimpleObjectProperty<>(fechaDevolucion);
    }

    /**
     * Obtiene el ID del préstamo.
     *
     * @return ID del préstamo.
     */
    public int getIdPrestamo() {
        return idPrestamo.get();
    }

    /**
     * Establece el ID del préstamo.
     *
     * @param idPrestamo ID del préstamo (debe ser mayor a 0).
     * @throws IllegalArgumentException si el ID es menor o igual a 0.
     */
    public void setIdPrestamo(int idPrestamo) {
        if (idPrestamo <= 0) {
            throw new IllegalArgumentException("El ID del préstamo debe ser mayor a 0.");
        }
        this.idPrestamo.set(idPrestamo);
    }

    /**
     * Obtiene el DNI del alumno.
     *
     * @return DNI del alumno.
     */
    public String getDniAlumno() {
        return dniAlumno.get();
    }

    /**
     * Establece el DNI del alumno.
     *
     * @param dniAlumno DNI del alumno (no puede ser nulo o vacío).
     * @throws IllegalArgumentException si el DNI es nulo o vacío.
     */
    public void setDniAlumno(String dniAlumno) {
        if (dniAlumno == null || dniAlumno.trim().isEmpty()) {
            throw new IllegalArgumentException("El DNI no puede estar vacío.");
        }
        this.dniAlumno.set(dniAlumno);
    }

    /**
     * Obtiene el código del libro.
     *
     * @return Código del libro.
     */
    public int getCodigoLibro() {
        return codigoLibro.get();
    }

    /**
     * Establece el código del libro.
     *
     * @param codigoLibro Código del libro (debe ser mayor a 0).
     * @throws IllegalArgumentException si el código es menor o igual a 0.
     */
    public void setCodigoLibro(int codigoLibro) {
        if (codigoLibro <= 0) {
            throw new IllegalArgumentException("El código del libro debe ser mayor a 0.");
        }
        this.codigoLibro.set(codigoLibro);
    }

    /**
     * Obtiene la fecha de préstamo.
     *
     * @return Fecha de préstamo.
     */
    public LocalDate getFechaPrestamo() {
        return fechaPrestamo.get();
    }

    /**
     * Establece la fecha de préstamo.
     *
     * @param fechaPrestamo Fecha del préstamo (no puede ser nula).
     * @throws IllegalArgumentException si la fecha es nula.
     */
    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        if (fechaPrestamo == null) {
            throw new IllegalArgumentException("La fecha de préstamo no puede ser nula.");
        }
        this.fechaPrestamo.set(fechaPrestamo);
    }

    /**
     * Obtiene la fecha de devolución.
     *
     * @return Fecha de devolución o null si el préstamo no ha sido devuelto.
     */
    public LocalDate getFechaDevolucion() {
        return fechaDevolucion.get();
    }

    /**
     * Establece la fecha de devolución.
     *
     * @param fechaDevolucion Fecha de devolución (puede ser nula).
     */
    public void setFechaDevolucion(LocalDate fechaDevolucion) {
        this.fechaDevolucion.set(fechaDevolucion);
    }

    /**
     * Calcula la fecha límite para la devolución del libro.
     *
     * @return Fecha límite de devolución (15 días después del préstamo).
     * @throws IllegalStateException si la fecha de préstamo no está asignada.
     */
    public LocalDate getFechaLimiteDevolucion() {
        if (fechaPrestamo.get() == null) {
            throw new IllegalStateException("La fecha de préstamo debe estar asignada.");
        }
        return fechaPrestamo.get().plusDays(15);
    }

    /**
     * Verifica si el préstamo ha sido devuelto.
     *
     * @return true si el préstamo ha sido devuelto, false en caso contrario.
     */
    public boolean esPrestamoDevuelto() {
        return fechaDevolucion.get() != null;
    }

    /**
     * Obtiene la fecha de préstamo formateada en el formato "dd/MM/yyyy".
     *
     * @return Fecha de préstamo formateada o cadena vacía si no está asignada.
     */
    public String getFechaPrestamoFormateada() {
        return fechaPrestamo.get() != null ? fechaPrestamo.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "";
    }

    /**
     * Obtiene la fecha de devolución formateada en el formato "dd/MM/yyyy".
     *
     * @return Fecha de devolución formateada o "No devuelto" si no está asignada.
     */
    public String getFechaDevolucionFormateada() {
        return fechaDevolucion.get() != null ? fechaDevolucion.get().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "No devuelto";
    }

    /**
     * Devuelve una representación en cadena del objeto Prestamo.
     *
     * @return Cadena con la información del préstamo.
     */
    @Override
    public String toString() {
        return String.format("Préstamo[ID=%d, DNI=%s, Código Libro=%d, Fecha Préstamo=%s, Fecha Devolución=%s]",
                getIdPrestamo(), getDniAlumno(), getCodigoLibro(), getFechaPrestamoFormateada(), getFechaDevolucionFormateada());
    }

    /** Getter para la propiedad del DNI */
    public ObservableValue<String> dniAlumnoProperty() {
        return dniAlumno;
    }

    /** Getter para la propiedad del Código del libro */
    public IntegerProperty codigoLibroProperty() {
        return codigoLibro;
    }
    
    /** Getter para la propiedad de la Fecha de Préstamo */
    public ObservableValue<LocalDate> fechaPrestamoProperty() {
        return fechaPrestamo;
    }

    public IntegerProperty idPrestamoProperty() {
        return idPrestamo;
    }

}
