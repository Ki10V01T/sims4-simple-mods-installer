package com.github.ki10v01t.service;


import com.github.ki10v01t.Main;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertDialogManager {

    public static void throwAlertDialog(Double width, String headerText, String bodyText, AlertType type) {
        Alert alertDialog = new Alert(type);
    
        switch (type) {
            case INFORMATION:
                alertDialog.setTitle(Main.res.getString("alert.title.information"));
                break;
            case WARNING:
                alertDialog.setTitle(Main.res.getString("alert.title.warning"));
                break;
            default:
                alertDialog.setTitle(Main.res.getString("alert.title.error"));
                break;
            }
            
        alertDialog.setWidth(width);
        alertDialog.setHeaderText(headerText);
        alertDialog.setContentText(bodyText);

        alertDialog.showAndWait();
    }

    public static void throwAlertDialog(String headerText, String bodyText, AlertType type) {
        Alert alertDialog = new Alert(type);
    
        switch (type) {
            case INFORMATION:
                alertDialog.setTitle(Main.res.getString("alert.title.information"));
                break;
            case WARNING:
                alertDialog.setTitle(Main.res.getString("alert.title.warning"));
                break;
            default:
                alertDialog.setTitle(Main.res.getString("alert.title.error"));
                break;
            }
        
        alertDialog.setHeaderText(headerText);
        alertDialog.setContentText(bodyText);

        alertDialog.showAndWait();
    }
}
