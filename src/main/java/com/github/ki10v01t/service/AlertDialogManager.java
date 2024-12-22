package com.github.ki10v01t.service;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class AlertDialogManager {
    private AlertType alertType;
    private String headerText;
    private String bodyText;
    private Double width;

    private AlertDialogManager(AlertDialogManagerBuilder alertDialogManagerBuilder) {
        this.alertType = alertDialogManagerBuilder.alertType;
        this.headerText = alertDialogManagerBuilder.headerText;
        this.bodyText = alertDialogManagerBuilder.bodyText;
        this.width = alertDialogManagerBuilder.width;
    }

    public static class AlertDialogManagerBuilder {
        private AlertType alertType;
        private String headerText;
        private String bodyText;
        private Double width;

        public AlertDialogManagerBuilder(AlertType alertType) {
            this.alertType = alertType;
        }

        public AlertDialogManagerBuilder setHeaderText(String headerText) {
            this.headerText = headerText;
            return this;
        }

        public AlertDialogManagerBuilder setBodyText(String bodyText) {
            this.bodyText = bodyText;
            return this;
        }

        public AlertDialogManagerBuilder setWidth(Double width) {
            this.width = width;
            return this;
        }

        public AlertDialogManager build() {
            return new AlertDialogManager(this);
        }
    }

    public void throwAlertDialog() {
        Alert alertDialog = new Alert(alertType);
    
        switch (alertType) {
            case INFORMATION:
                alertDialog.setTitle(LocaleManager.getInstance().getResourceBundle().getString("alert.title.information"));
                break;
            case WARNING:
                alertDialog.setTitle(LocaleManager.getInstance().getResourceBundle().getString("alert.title.warning"));
                break;
            default:
                alertDialog.setTitle(LocaleManager.getInstance().getResourceBundle().getString("alert.title.error"));
                break;
            }
        
        if(width != null) {
            alertDialog.setWidth(width);
        }
        if(headerText != null) {
            alertDialog.setHeaderText(headerText);
        } else {
            alertDialog.setHeaderText("Default text");
        }
        if(bodyText != null) {
            alertDialog.setContentText(bodyText);
        } else {
            alertDialog.setContentText("Default text");
        }

        alertDialog.showAndWait();
    }
}
