module org.intissar.examenintissar2dein {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires javafx.web;
    requires java.desktop;
    requires itextpdf;
    requires org.slf4j;
    requires jasperreports;
    requires java.logging;
    requires kotlin.stdlib; // 👈 Agrega esta línea



    opens org.intissar.examenintissar2dein.controller to javafx.fxml;


    opens org.intissar.examenintissar2dein to javafx.fxml;
    exports org.intissar.examenintissar2dein;
}