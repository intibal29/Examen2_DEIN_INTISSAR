module org.intissar.examenintissar2dein {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.intissar.examenintissar2dein to javafx.fxml;
    exports org.intissar.examenintissar2dein;
}