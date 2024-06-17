package com.github.ki10v01t;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ki10v01t.service.CopyMode;
import com.github.ki10v01t.service.FileTransferManager;
import com.github.ki10v01t.service.LogMessageManager;

import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainFormController {
    @FXML
    private CheckBox byDefaultProp; 
    @FXML
    private TextField copyFromBox;
    @FXML
    private TextField copyToBox;
    @FXML
    private TextArea logBox;
    @FXML
    private Button downloadButton;  

    private Logger log = LogManager.getLogger("MainFormController");

    private Path selectedSourceDir, selectedDstDir;
    private DirectoryChooser dc =  new DirectoryChooser();
    private FileTransferManager ftm;
    private LogMessageManager lmm;
    
    // @FXML
    // private void switchToSecondary() throws IOException {
        
    //     App.setRoot("secondary");
    // }

    @FXML
    private void openSourceFolder() throws IOException {
        File file = dc.showDialog(Stage.getWindows().get(0));
        if (file != null) {
            selectedSourceDir = file.toPath();
        }

        if (selectedSourceDir != null) {
            copyFromBox.setText(selectedSourceDir.toString());
        }
    }

    @FXML
    private void openDestinationFolder() throws IOException {
        File file = dc.showDialog(Stage.getWindows().get(0));
        if (file != null) {
            selectedDstDir = file.toPath();
        }

        if (selectedDstDir != null) {
            copyToBox.setText(selectedDstDir.toString());
        }
    }

    @FXML
    private void downloadAll() {
        if(lmm == null) {
            lmm = new LogMessageManager(logBox);
        }

        if (selectedSourceDir == null) {
            //AlertDialogManager.throwAlertDialog("Source folder han not been selected", "Please, select a source folder", AlertType.WARNING);
            return;
        }
        
        //System.out.println(Main.osType);
        if ((byDefaultProp.isSelected() == false) && (copyToBox.getText() == "")) {
            //AlertDialogManager.throwAlertDialog("Destination forlder has not been selected", "Please, select a destination folder or choose a 'Copy by default' property", AlertType.WARNING);
            return;
        }

        try {
            downloadButton.setDisable(true);
            
            //ftm = new FileTransferManager(CopyMode.MODS, selectedSourceDir, selectedDstDir, copyToBox.getText(), byDefaultProp.isSelected());
            
            Task<Boolean> modsThread = new FileTransferManager(CopyMode.MODS, selectedSourceDir, selectedDstDir, copyToBox.getText(), byDefaultProp.isSelected(), lmm);
            Task<Boolean> trayThread = new FileTransferManager(CopyMode.TRAY, selectedSourceDir, selectedDstDir, copyToBox.getText(), byDefaultProp.isSelected(), lmm);
            Boolean resultMods = false, resultTray = false;

            lmm.bindLog(modsThread.messageProperty());
            modsThread.run();
            trayThread.run();

        // } catch (IOException ioe) {
        //     log.error(ioe.getMessage(), ioe);
        } catch (IllegalThreadStateException itse) {
            log.error(itse.getMessage(), itse);
        }

        finally {
            downloadButton.setDisable(false);
        }
    }
}
