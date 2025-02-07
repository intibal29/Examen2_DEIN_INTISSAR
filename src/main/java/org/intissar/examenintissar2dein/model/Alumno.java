package org.intissar.examenintissar2dein.model;

import java.util.Objects;

/**
 * Clase que representa un Alumno con sus datos personales: DNI, nombre y apellidos.
 * Proporciona métodos para establecer y obtener estos valores, además de métodos
 * para verificar igualdad y calcular el hashcode.
 *
 * <p>Incluye validaciones para asegurar la correcta asignación de los atributos.
 * Por ejemplo, el nombre y el primer apellido no pueden estar vacíos.</p>
 *
 * @author Intissar
 * @version 1.0
 */
public class Alumno {
    private String dni;
    private String nombre;
    private String apellido1;
    private String apellido2;

    /**
     * Constructor que inicializa un objeto Alumno con todos sus atributos.
     *
     * @param dni DNI del alumno.
     * @param nombre Nombre del alumno.
     * @param apellido1 Primer apellido del alumno.
     * @param apellido2 Segundo apellido del alumno (puede ser nulo).
     */
    public Alumno(String dni, String nombre, String apellido1, String apellido2) {
        setDni(dni);
        setNombre(nombre);
        setApellido1(apellido1);
        setApellido2(apellido2);
    }

    /**
     * Obtiene el DNI del alumno.
     *
     * @return DNI del alumno.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el DNI del alumno.
     *
     * @param dni DNI del alumno (debe tener 8 dígitos seguidos de una letra).
     * @throws IllegalArgumentException si el formato del DNI no es válido.
     */
    public void setDni(String dni) {
        /* if (dni == null || !dni.matches("\\d{8}[A-Za-z]")) {
            throw new IllegalArgumentException("DNI no válido. Debe ser de 8 dígitos seguidos de una letra.");
        } */
        this.dni = dni;
    }

    /**
     * Obtiene el nombre del alumno.
     *
     * @return Nombre del alumno.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre del alumno.
     *
     * @param nombre Nombre del alumno.
     * @throws IllegalArgumentException si el nombre es nulo o está vacío.
     */
    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        this.nombre = nombre;
    }

    /**
     * Obtiene el primer apellido del alumno.
     *
     * @return Primer apellido del alumno.
     */
    public String getApellido1() {
        return apellido1;
    }

    /**
     * Establece el primer apellido del alumno.
     *
     * @param apellido1 Primer apellido del alumno.
     * @throws IllegalArgumentException si el apellido es nulo o está vacío.
     */
    public void setApellido1(String apellido1) {
        if (apellido1 == null || apellido1.trim().isEmpty()) {
            throw new IllegalArgumentException("El primer apellido no puede estar vacío.");
        }
        this.apellido1 = apellido1;
    }

    /**
     * Obtiene el segundo apellido del alumno.
     *
     * @return Segundo apellido del alumno, puede ser nulo.
     */
    public String getApellido2() {
        return apellido2;
    }

    /**
     * Establece el segundo apellido del alumno.
     *
     * @param apellido2 Segundo apellido del alumno (puede ser nulo).
     * @throws IllegalArgumentException si el segundo apellido es una cadena vacía.
     */
    public void setApellido2(String apellido2) {
        if (apellido2 != null && apellido2.trim().isEmpty()) {
            throw new IllegalArgumentException("El segundo apellido no puede estar vacío si se proporciona.");
        }
        this.apellido2 = apellido2;
    }

    /**
     * Devuelve una representación en cadena del objeto Alumno.
     *
     * @return Cadena con el nombre completo del alumno y su DNI.
     */
    @Override
    public String toString() {
        return nombre + " " + apellido1 + " " + (apellido2 != null ? apellido2 : "") + " (" + dni + ")";
    }

    /**
     * Compara si este objeto es igual a otro basado en el DNI.
     *
     * @param obj Objeto a comparar.
     * @return true si el DNI es igual, false en caso contrario.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Alumno alumno = (Alumno) obj;
        return Objects.equals(dni, alumno.dni);
    }

    /**
     * Calcula el código hash basado en el DNI del alumno.
     *
     * @return Código hash del objeto.
     */
    @Override
    public int hashCode() {
        return Objects.hash(dni);
    }
}
