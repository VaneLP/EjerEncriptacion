package com.example.ejerencriptacion;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.mindrot.jbcrypt.BCrypt;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.ResourceBundle;

public class EncriptacionController implements Initializable {

    // Atributos
    @FXML
    private ComboBox<String> comboBox;
    @FXML
    private RadioButton radioBuBcrypt;
    @FXML
    private RadioButton radioBuCriptoSime;
    @FXML
    private TextArea escribirTxt;
    @FXML
    private TextArea textEncriptado;
    @FXML
    private TextField claveEncriptacion;
    @FXML
    private Text text;

    // atributos para almacenar el hash de BCrypt
    private String claveGuardada;
    // genera numeros aleatorios seguros
    private static final SecureRandom secureRandom = new SecureRandom();

    // metodo de inicializacion de la interfaz grafica
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // añadimos al ComboBox las opciones
        comboBox.getItems().addAll("AES/ECB", "AES/CBC", "DES", "3DES");
        comboBox.getSelectionModel().selectFirst();
    }

    // criptografía simetrica
    @FXML
    private void selectCripSi() {
        // Desactivar seleccion de BCrypt
        radioBuBcrypt.setSelected(false);
        // habilitar la edición de la clave
        claveEncriptacion.setEditable(true);
        // y limpiar campos
        claveEncriptacion.clear();
        escribirTxt.clear();

        text.setText("Clave encriptacion  ");

        System.out.println("*-*-* Criptografia Simetrica *-*-* ");
    }

    // BCrypt
    @FXML
    private void selectBCry() {
        // desactivar seleccion de criptografia simetrica
        radioBuCriptoSime.setSelected(false);
        // mostrar mensaje clave no guardada
        mostrarClaveNoGuardada();
        // limpiar campo
        claveEncriptacion.clear();

        System.out.println("*-*-* BCryp *-*-*t");
    }

    // mensaje de clave no guardada
    private void mostrarClaveNoGuardada() {
        text.setText("Clave no guardada  ");
        claveEncriptacion.setEditable(false);
    }

    // mensaje de clave guardada con un fragmento de la clave
    private void mostrarClaveGuardada(String clave) {
        text.setText("Clave guardada  ");
        claveEncriptacion.setText(clave);
    }

    // pulsar encriptar
    @FXML
    private void pulsarEncritar() {
        if (radioBuCriptoSime.isSelected() && !escribirTxt.getText().isEmpty()) {
            // Encriptar utilizando criptografia simetrica
            String textoOriginal = escribirTxt.getText();
            String clave = claveEncriptacion.getText();

            String textoEncriptado = encriptarSimetrico(textoOriginal, clave);
            textEncriptado.setText(textoEncriptado);

            // Guardar la clave
            mostrarClaveGuardada(clave);

        } else if (radioBuBcrypt.isSelected() && !escribirTxt.getText().isEmpty()) {
            // Generar hash BCrypt para el texto
            String textoOriginal = escribirTxt.getText();
            claveGuardada = BCrypt.hashpw(textoOriginal, BCrypt.gensalt());
            textEncriptado.setText(claveGuardada);
            mostrarClaveGuardada(claveGuardada.substring(0, Math.min(claveGuardada.length(), 10)) + "...");
        } else {
            mostrarError("Faltan datos", "Por favor, complete todos los campos antes de encriptar.");
        }

        escribirTxt.clear();
    }

    // pulsar desencriptar
    @FXML
    private void pulsarDesencriptar() {
        if (radioBuCriptoSime.isSelected() && !textEncriptado.getText().isEmpty()) {
            // Desencriptar utilizando criptografia simetrica
            String textoEncriptadoStr = textEncriptado.getText();
            String clave = claveEncriptacion.getText();

            String textoDesencriptado = desencriptarSimetrico(textoEncriptadoStr, clave);
            escribirTxt.setText(textoDesencriptado);

        } else if (radioBuBcrypt.isSelected()) {
            // Comprobar si el texto coincide con el hash BCrypt almacenado
            String textoAComprobar = escribirTxt.getText();

            if (claveGuardada == null || claveGuardada.isEmpty()) {
                mostrarError("Operación no permitida", "No hay una clave guardada para verificar.");
                return;
            }

            boolean match = BCrypt.checkpw(textoAComprobar, claveGuardada);

            if (match) {
                mostrarMensaje("Operación Exitosa", "Clave correcta.");
            } else {
                mostrarMensaje("Operación Fallida", "Clave incorrecta.");
            }
        } else {
            mostrarError("Faltan datos", "Por favor, complete todos los campos antes de desencriptar.");
        }

        textEncriptado.clear();
    }

    // Metodo para encriptar con criptografia simetrica
    private String encriptarSimetrico(String texto, String clave) {
        try {
            // Configuracion de la clave y el algoritmo
            int keySize = 128; // Tamaño de la clave para AES
            String keyAlgorithm = "AES";
            byte[] keyBytes = Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(clave.getBytes(StandardCharsets.UTF_8)), keySize / 8);
            Key secretKey = new SecretKeySpec(keyBytes, keyAlgorithm);

            // Configuración del modo CBC con PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] ivBytes = new byte[16];
            secureRandom.nextBytes(ivBytes); // Generar un IV aleatorio
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

            // Encriptar el texto y combinar IV y datos encriptados
            byte[] encrypted = cipher.doFinal(texto.getBytes(StandardCharsets.UTF_8));
            byte[] encryptedIVAndText = concatenateIVAndEncryptedData(ivBytes, encrypted);

            // Devolver el resultado en Base64
            return Base64.getEncoder().encodeToString(encryptedIVAndText);
        } catch (Exception e) {
            mostrarError("Error de Encriptación", "Ocurrió un error al encriptar el texto.");
            e.printStackTrace();
            return null;
        }
    }

    // Metodo para desencriptar con criptografia simetrica
    private String desencriptarSimetrico(String textoEncriptado, String clave) {
        try {
            // Decodificar el texto encriptado de Base64
            byte[] decodedData = Base64.getDecoder().decode(textoEncriptado);
            // Extraer IV y datos encriptados
            byte[] ivBytes = Arrays.copyOfRange(decodedData, 0, 16);
            byte[] encryptedTextBytes = Arrays.copyOfRange(decodedData, 16, decodedData.length);

            // Configuracion de la clave y el algoritmo para desencriptar
            int keySize = 128; // Tamaño de la clave para AES
            String keyAlgorithm = "AES";
            byte[] keyBytes = Arrays.copyOf(MessageDigest.getInstance("SHA-256").digest(clave.getBytes(StandardCharsets.UTF_8)), keySize / 8);
            SecretKeySpec secretKey = new SecretKeySpec(keyBytes, keyAlgorithm);

            // Configuracion del modo CBC con PKCS5Padding
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

            // Desencriptar y convertir a texto
            byte[] decrypted = cipher.doFinal(encryptedTextBytes);
            return new String(decrypted, StandardCharsets.UTF_8);
        } catch (Exception e) {
            mostrarError("Error de Desencriptación", "Ocurrió un error al desencriptar el texto.");
            e.printStackTrace();
            return null;
        }
    }

    // Utilidad para concatenar IV y datos encriptados
    private byte[] concatenateIVAndEncryptedData(byte[] iv, byte[] encryptedData) {
        byte[] combined = new byte[iv.length + encryptedData.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(encryptedData, 0, combined, iv.length, encryptedData.length);
        return combined;
    }

    // Método para mostrar un mensaje de error
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // Método para mostrar un mensaje informativo
    private void mostrarMensaje(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
