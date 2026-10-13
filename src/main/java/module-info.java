
module com.example.proyectocrud {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.example.proyectocrud to javafx.fxml;
    opens com.example.proyectocrud.controller to javafx.fxml;
    opens com.example.proyectocrud.model to javafx.base;

    exports com.example.proyectocrud;
}
