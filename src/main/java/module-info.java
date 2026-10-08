module com.example.proyectocrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.proyectocrud to javafx.fxml;
    exports com.example.proyectocrud;
}