package com.github.ki10v01t;

import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ki10v01t.service.CopyMode;
import com.github.ki10v01t.service.FileTransferManager;

import javafx.concurrent.Task;
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
    
    public static Integer columnCount = 0;
    
    // @FXML
    // private void switchToSecondary() throws IOException {
        
    //     App.setRoot("secondary");
    // }

    @FXML
    private void openSourceFolder() throws IOException {
        selectedSourceDir = dc.showDialog(Stage.getWindows().get(0)).toPath();

        if (selectedSourceDir != null) {
            copyFromBox.setText(selectedSourceDir.toString());
        }
    }

    @FXML
    private void openDestinationFolder() throws IOException {
        selectedDstDir = dc.showDialog(Stage.getWindows().get(0)).toPath();
        if (selectedDstDir != null) {
            copyToBox.setText(selectedDstDir.toString());
        }
    }

    @FXML
    private void downloadAll() {
        // if(lmm == null) {
        //     lmm = new LogMessageManager(logBox.getPrefColumnCount());
        // }

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

            columnCount = logBox.getPrefColumnCount();
            
            ftm = new FileTransferManager(selectedSourceDir, selectedDstDir, copyToBox.getText());
            Task<Boolean> fileThreadMods = ftm;
            Task<Boolean> fileThreadTray = ftm;
            
            logBox.textProperty().bind(ftm.messageProperty().concat(logBox.getText()));

            for(CopyMode el : CopyMode.values()) {
                ftm.setCopyMode(el);
                ftm.setIsDefaultPath(byDefaultProp.isSelected());

                switch (el) {
                    case TRAY:
                        fileThreadTray.run();
                        break;
                    case MODS:
                        //fileThreadMods.run();
                        break;
                    default:
                        break;
                }
                
                //ftm.copyModsAndTray();
                
            }
        // } catch (IOException ioe) {
        //     log.error(ioe.getMessage(), ioe);
        } catch (IllegalThreadStateException itse) {
            log.error(itse.getMessage(), itse);
        } finally {
            downloadButton.setDisable(false);
        }
    }
}
