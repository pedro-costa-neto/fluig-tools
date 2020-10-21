package br.com.softdigital.fluig.services.exception;

import javafx.scene.control.Alert;

/**
 * Exception Dialog
 *
 * @author Pedro Costa
 *
 * @version 1.0.0
 * @since 1.0.0, 06/06/2020
 */
public class ExceptionDialog extends Exception{
    
    private final String message;
    private final String detail;
    
    public ExceptionDialog(String message, String detail) {
        super(message);
        this.message = message;
        this.detail = detail;
    }
    
    public void showMessage() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Falha");
        alert.setHeaderText(message);
        alert.setContentText(detail);
        alert.showAndWait();
    }
}
