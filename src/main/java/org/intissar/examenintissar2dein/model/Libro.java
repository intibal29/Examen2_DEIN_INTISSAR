package org.intissar.examenintissar2dein.model;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

/**
 * Clase que representa un libro con atributos como código, título, autor,
 * editorial, estado y una propiedad para indicar si está dado de baja.
 *
 * <p>Utiliza propiedades de JavaFX para facilitar la vinculación en interfaces
 * gráficas. Incluye validaciones para la correcta asignación de valores en los
 * atributos.</p>
 *
 * @author Intissar
 * @version 1.0
 */
public class Libro {
    private final SimpleIntegerProperty codigo;
    private final SimpleStringProperty titulo;
    private final SimpleStringProperty autor;
    private final SimpleStringProperty editorial;
    private SimpleStringProperty estado;
    private final SimpleBooleanProperty baja;

    /**
     * Constructor por defecto que inicializa las propiedades del libro.
     * El estado por defecto es "Nuevo" y la baja es false.
     */
    public Libro() {
        this.codigo = new SimpleIntegerProperty();
        this.titulo = new SimpleStringProperty();
        this.autor = new SimpleStringProperty();
        this.editorial = new SimpleStringProperty();
        this.estado = new SimpleStringProperty("Nuevo"); // Estado por defecto
        this.baja = new SimpleBooleanProperty(false);
    }

    /**
     * Constructor que permite inicializar todas las propiedades del libro.
     *
     * @param codigo Código del libro (debe ser mayor que 0).
     * @param titulo Título del libro.
     * @param autor Autor del libro.
     * @param editorial Editorial del libro.
     * @param estado Estado del libro ("Nuevo", "Usado nuevo", "Usado seminuevo", "Usado estropeado", "Restaurado").
     * @param baja Indica si el libro está dado de baja.
     */
    public Libro(int codigo, String titulo, String autor, String editorial, String estado, boolean baja) {
        this.codigo = new SimpleIntegerProperty(codigo);
        this.titulo = new SimpleStringProperty(titulo);
        this.autor = new SimpleStringProperty(autor);
        this.editorial = new SimpleStringProperty(editorial);
        this.estado = new SimpleStringProperty(estado);
        this.baja = new SimpleBooleanProperty(baja);
    }

    /**
     * Obtiene el código del libro.
     *
     * @return Código del libro.
     */
    public int getCodigo() {
        return codigo.get();
    }

    /**
     * Establece el código del libro.
     *
     * @param codigo Código del libro (debe ser mayor que 0).
     * @throws IllegalArgumentException si el código es menor o igual a 0.
     */
    public void setCodigo(int codigo) {
        if (codigo <= 0) throw new IllegalArgumentException("El código debe ser mayor que 0.");
        this.codigo.set(codigo);
    }

    /**
     * Obtiene el título del libro.
     *
     * @return Título del libro.
     */
    public String getTitulo() {
        return titulo.get();
    }

    /**
     * Establece el título del libro.
     *
     * @param titulo Título del libro (no puede ser nulo o vacío).
     * @throws IllegalArgumentException si el título es nulo o vacío.
     */
    public void setTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty())
            throw new IllegalArgumentException("El título no puede estar vacío.");
        this.titulo.set(titulo);
    }

    /**
     * Obtiene el autor del libro.
     *
     * @return Autor del libro.
     */
    public String getAutor() {
        return autor.get();
    }

    /**
     * Establece el autor del libro.
     *
     * @param autor Autor del libro (no puede ser nulo o vacío).
     * @throws IllegalArgumentException si el autor es nulo o vacío.
     */
    public void setAutor(String autor) {
        if (autor == null || autor.trim().isEmpty())
            throw new IllegalArgumentException("El autor no puede estar vacío.");
        this.autor.set(autor);
    }

    /**
     * Obtiene la editorial del libro.
     *
     * @return Editorial del libro.
     */
    public String getEditorial() {
        return editorial.get();
    }

    /**
     * Establece la editorial del libro.
     *
     * @param editorial Editorial del libro (no puede ser nula o vacía).
     * @throws IllegalArgumentException si la editorial es nula o vacía.
     */
    public void setEditorial(String editorial) {
        if (editorial == null || editorial.trim().isEmpty())
            throw new IllegalArgumentException("La editorial no puede estar vacía.");
        this.editorial.set(editorial);
    }

    /**
     * Obtiene el estado del libro.
     *
     * @return Estado del libro.
     */
    public String getEstado() {
        return estado.get();
    }

    /**
     * Establece el estado del libro. Si el estado proporcionado no es válido,
     * se asigna el valor por defecto "Nuevo".
     *
     * @param estado Estado del libro ("Nuevo", "Usado nuevo", "Usado seminuevo", "Usado estropeado", "Restaurado").
     */
    public void setEstado(String estado) {
        if (!estado.matches("Nuevo|Usado nuevo|Usado seminuevo|Usado estropeado|Restaurado")) {
            System.err.println("⚠ Estado inválido recibido: " + estado + ". Se asignará 'Nuevo'.");
            this.estado.set("Nuevo"); // Valor por defecto
        } else {
            this.estado.set(estado);
        }
    }

    /**
     * Indica si el libro está dado de baja.
     *
     * @return true si el libro está dado de baja, false en caso contrario.
     */
    public boolean isBaja() {
        return baja.get();
    }

    /**
     * Establece si el libro está dado de baja.
     *
     * @param baja true si el libro debe darse de baja, false en caso contrario.
     */
    public void setBaja(boolean baja) {
        this.baja.set(baja);
    }

    /**
     * Devuelve una representación en cadena del objeto Libro.
     *
     * @return Cadena con la información del libro.
     */
    @Override
    public String toString() {
        return String.format("Libro{codigo=%d, titulo='%s', autor='%s', editorial='%s', estado='%s', baja=%b}",
                getCodigo(), getTitulo(), getAutor(), getEditorial(), getEstado(), isBaja());
    }

    // Métodos para integrarse con JavaFX
    public SimpleIntegerProperty codigoProperty() { return codigo; }
    public SimpleStringProperty tituloProperty() { return titulo; }
    public SimpleStringProperty autorProperty() { return autor; }
    public SimpleStringProperty editorialProperty() { return editorial; }
    public SimpleStringProperty estadoProperty() { return estado; }
    public SimpleBooleanProperty bajaProperty() { return baja; }
}
