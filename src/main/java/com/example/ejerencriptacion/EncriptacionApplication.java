package com.example.ejerencriptacion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class EncriptacionApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Application.setUserAgentStylesheet(String.valueOf(EncriptacionApplication.class.getResource("dracula.css")));
        FXMLLoader fxmlLoader = new FXMLLoader(EncriptacionApplication.class.getResource("vistaEncriptacion.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 700);
        stage.setTitle("Practica de Criptografia");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}