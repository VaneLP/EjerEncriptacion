module com.example.ejerencriptacion {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.ejerencriptacion to javafx.fxml;
    exports com.example.ejerencriptacion;
}