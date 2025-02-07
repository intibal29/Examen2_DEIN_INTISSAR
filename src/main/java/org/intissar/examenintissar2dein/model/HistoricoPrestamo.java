package org.intissar.examenintissar2dein.model;

import javafx.beans.property.*;

import java.time.LocalDate;

/**
 * Modelo que representa el historial de préstamos en la biblioteca.
 */
public class HistoricoPrestamo {
    private final IntegerProperty idPrestamo;
    private final StringProperty dniEstudiante;
    private final IntegerProperty isbn;
    private final BooleanProperty prestamoOnline;
    private final ObjectProperty<LocalDate> fechaPrestamo;

    /**
     * Constructor para inicializar un historial de préstamo.
     *
     * @param idPrestamo ID del préstamo.
     * @param dniEstudiante DNI del estudiante.
     * @param isbn ISBN del libro prestado.
     * @param prestamoOnline Indica si el préstamo fue online.
     * @param fechaPrestamo Fecha del préstamo.
     */
    public HistoricoPrestamo(int idPrestamo, String dniEstudiante, int isbn, boolean prestamoOnline, LocalDate fechaPrestamo ) {
        this.idPrestamo = new SimpleIntegerProperty(idPrestamo);
        this.dniEstudiante = new SimpleStringProperty(dniEstudiante);
        this.isbn = new SimpleIntegerProperty(isbn);
        this.prestamoOnline = new SimpleBooleanProperty(prestamoOnline);
        this.fechaPrestamo = new SimpleObjectProperty<>(fechaPrestamo);
    }

    // --- GETTERS Y SETTERS ---

    public int getIdPrestamo() {
        return idPrestamo.get();
    }

    public void setIdPrestamo(int idPrestamo) {
        this.idPrestamo.set(idPrestamo);
    }

    public String getDniEstudiante() {
        return dniEstudiante.get();
    }

    public void setDniEstudiante(String dniEstudiante) {
        this.dniEstudiante.set(dniEstudiante);
    }

    public int getIsbn() {
        return isbn.get();
    }

    public void setIsbn(int isbn) {
        this.isbn.set(isbn);
    }

    public boolean isPrestamoOnline() {
        return prestamoOnline.get();
    }

    public void setPrestamoOnline(boolean prestamoOnline) {
        this.prestamoOnline.set(prestamoOnline);
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo.get();
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo.set(fechaPrestamo);
    }



    @Override
    public String toString() {
        return String.format("HistorialPrestamo[ID=%d, DNI=%s, ISBN=%d, Online=%s, Fecha Préstamo=%s, Fecha Devolución=%s]",
                getIdPrestamo(), getDniEstudiante(), getIsbn(), isPrestamoOnline() ? "Sí" : "No",
                getFechaPrestamo());
    }

    // --- PROPIEDADES PARA JAVA FX ---
    public IntegerProperty idPrestamoProperty() { return idPrestamo; }
    public StringProperty dniAlumnoProperty() { return dniEstudiante; }
    public IntegerProperty codigoLibroProperty() { return isbn; }
    public BooleanProperty prestamoOnlineProperty() { return prestamoOnline; }
    public ObjectProperty<LocalDate> fechaPrestamoProperty() { return fechaPrestamo; }

    // Getters de JavaFX para la TableView



}
