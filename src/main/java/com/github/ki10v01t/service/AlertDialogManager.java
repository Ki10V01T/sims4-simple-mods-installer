package com.github.ki10v01t.service;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertDialogManager {

    public static void throwAlertDialog(String headerText, String bodyText, AlertType type) {
        Alert alertDialog = new Alert(type);
    
        switch (type) {
            case INFORMATION:
                alertDialog.setTitle("Information");;
                break;
            case WARNING:
                alertDialog.setTitle("Warning");
                break;
            default:
                alertDialog.setTitle("Error");
                break;
            }
        
        alertDialog.setHeaderText(headerText);
        alertDialog.setContentText(bodyText);

        alertDialog.showAndWait();
    }
}
