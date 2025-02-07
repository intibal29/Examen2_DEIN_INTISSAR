package org.intissar.examenintissar2dein.model;

/**
 * Clase Ayuda
 */
public class Help {
    private String text;
    private String html;
    private boolean local = true;

    /**
     * Constructor con parámetros de la ayuda
     *
     * @param text texto de la ayuda
     * @param html de la ayuda
     * @param local html local o no
     */
    public Help(String text, String html, boolean local) {
        this.text = text;
        this.html = html;
        this.local = local;
    }

    /**
     * Constructor con parámetros de la ayuda
     *
     * @param text texto de la ayuda
     * @param html de la ayuda
     */
    public Help(String text, String html) {
        this.text = text;
        this.html = html;
    }

    /**
     * Constructor vacío de la ayuda
     */
    public Help() {}

    /**
     * ToString de la ayuda
     *
     * @return título a mostrar en la vista árbol
     */
    @Override
    public String toString() {
        return text;
    }

    /**
     * Getter para el text
     *
     * @return text
     */
    public String getText() {
        return text;
    }

    /**
     * Setter para el text
     *
     * @param text nuevo valor de text
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Getter para el html
     *
     * @return html
     */
    public String getHtml() {
        return html;
    }

    /**
     * Setter para el html
     *
     * @param html nuevo valor de html
     */
    public void setHtml(String html) {
        this.html = html;
    }

    /**
     * Getter para el local
     *
     * @return local
     */
    public boolean isLocal() {
        return local;
    }

    /**
     * Setter para el local
     *
     * @param local nuevo valor de local
     */
    public void setLocal(boolean local) {
        this.local = local;
    }
}

