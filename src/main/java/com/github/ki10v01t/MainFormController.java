package com.github.ki10v01t;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainFormController {

    private File selectedSourceDir, selectedDstDir;
    private DirectoryChooser dc =  new DirectoryChooser();
    
    @FXML
    private CheckBox byDefaultProp; 
    @FXML
    private TextField copyFromBox;
    @FXML
    private TextField copyToBox;
    
    // @FXML
    // private void switchToSecondary() throws IOException {
        
    //     App.setRoot("secondary");
    // }

    @FXML
    private void openSourceFolder() throws IOException {
        selectedSourceDir = dc.showDialog(Stage.getWindows().get(0));
        copyFromBox.setText(selectedSourceDir.getPath());
    }

    @FXML
    private void openDestinationFolder() throws IOException {
        selectedDstDir = dc.showDialog(Stage.getWindows().get(0));
        copyToBox.setText(selectedDstDir.getPath());
    }

    private void throwAlertDialog(String headerMsg, String msg, AlertType type) {
        Alert alert = new Alert(type);

        switch (type) {
            case INFORMATION:
                alert.setTitle("Information");;
                break;
            case WARNING:
                alert.setTitle("Warning");
                break;
            default:
                alert.setTitle("Error");
                break;

            }
        
        alert.setHeaderText(headerMsg);
        alert.setContentText(msg);

        alert.showAndWait();
    }

    private void copyMods() {
        ArrayList<File> sourceFiles = new ArrayList<File>(Arrays.asList(selectedSourceDir.listFiles(null))); //selectedSourceDir.listFiles(null)
    }

    @FXML
    private void downloadMods() throws IOException {
        if (selectedSourceDir == null) {
            throwAlertDialog("Source folder han not been selected", "Please, select a source folder", AlertType.WARNING);
            return;
        }
        if ((byDefaultProp.isSelected() == false) && (selectedDstDir == null)) {
            throwAlertDialog("Destination forlder has not been selected", "Please, select a destination folder or choose a 'Copy by default' property", AlertType.WARNING);
        }
    }


}
