package com.example.ejerencriptacion;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;

import java.net.URL;
import java.util.ResourceBundle;

public class EncriptacionController implements Initializable {

    @FXML
    private Button botonDesencriptar;

    @FXML
    private Button botonEncriptar;

    @FXML
    private ComboBox<String> comboBox;

    @FXML
    private RadioButton radioBuBcrypt;

    @FXML
    private RadioButton radioBuCriptoSime;



    @FXML
    void pulsarDesencriptar(ActionEvent event) {

    }

    @FXML
    void pulsarEncritar(ActionEvent event) {

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        comboBox.getItems().addAll("AES/ECB","AES/CBC","DES","3DES");
    }
}
