package com.github.ki10v01t;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ki10v01t.service.AlertDialogManager;
import com.github.ki10v01t.service.ExitMode;
import com.github.ki10v01t.service.FileTransferManager;
import com.github.ki10v01t.service.LogMessage;
import com.github.ki10v01t.service.LogMessageManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

public class MainFormController {

    @FXML
    private CheckBox byDefaultProp;
    @FXML
    private CheckBox toSameFolderProp; 
    @FXML
    private TextField copyFromBox;
    @FXML
    private TextField copyToBox;
    @FXML
    private ListView<String> logBox;
    @FXML
    private Button downloadButton;
    @FXML
    private Button cancelButton;
    @FXML
    private Button destButton;
    @FXML
    private MenuItem instructionsMenuItem;
    @FXML
    private MenuItem aboutMenuItem;
    @FXML
    private MenuItem supportMenuItem;

    private Logger log;

    private Path selectedSourceDir, selectedDstDir;
    private DirectoryChooser dc;
    private FileTransferManager ftm;
    private LogMessageManager lmm;

    private Thread fileThread;
    private ObservableList<String> messageList;
    
    // @FXML
    // private void switchToSecondary() throws IOException {
        
    //     App.setRoot("secondary");
    // }

    @FXML
    public void initialize() {
        log = LogManager.getLogger("MainFormController");
        dc = new DirectoryChooser();
        messageList = FXCollections.observableArrayList();
    }
        

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
    private void openInstructionsWindow() {
        AlertDialogManager.throwAlertDialog(700.0, Main.res.getString("menubar.instructions"), Main.res.getString("message.menubar.instructions"), AlertType.INFORMATION);
    }

    @FXML
    private void openAboutWindow() {

    }

    @FXML
    private void openSupportWindow() {

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

    private void switchBtwDownloadAndCancelButtons() {
        Boolean resultDownloadButtonVisibility = downloadButton.isVisible() == true ? false : true;
        Boolean resultCancelButtonVisibility = cancelButton.isVisible() == true ? false : true;

        downloadButton.setVisible(resultDownloadButtonVisibility);
        cancelButton.setVisible(resultCancelButtonVisibility);
    }

    @FXML
    private void cancelDownoad() {
        try {
            finishingOfCopyProcess(ExitMode.CANCELLED);
        } catch (SecurityException se) {
            log.error(se.getMessage(), se);
        }
    }

    @FXML
    private void byDefaultPropCheck() {
        if(byDefaultProp.isSelected() == true) {
            copyToBox.setDisable(true);
            toSameFolderProp.setDisable(true);
            destButton.setDisable(true);
        } else {
            copyToBox.setDisable(false);
            toSameFolderProp.setDisable(false);
            destButton.setDisable(false);
        }
    }

    @FXML
    private void toSameFolderPropCheck() {
        if(toSameFolderProp.isSelected() == true) {
            byDefaultProp.setDisable(true);
        } else {
            byDefaultProp.setDisable(false);
        }
    }

    private void finishingOfCopyProcess(ExitMode cm) {
        fileThread.interrupt();

        Boolean copyResult;
        LogMessage msg;
        
        switch (cm) {
            case CANCELLED -> {
                copyResult = true;
                msg = LogMessage.createInfoMessage("File operations has been interrupted");
            }
            case FAILED -> {
                copyResult = false;
                msg = LogMessage.createErrorMessage("Error, when interrupting file operations attempt. Try again.");
            }
            case SUCCEEDED -> {
                copyResult = true;
                msg = LogMessage.createInfoMessage("Copy has been succeeded");
            }
            default -> {
                copyResult = false;
                msg = LogMessage.createInfoMessage("FATAL ERROR when finishing process. Try again.");
            }
        }

        if(copyResult) {
            switchBtwDownloadAndCancelButtons();
        } else {
            AlertDialogManager.throwAlertDialog("Error", msg.getMessage(), AlertType.ERROR);
        }

        lmm.sendMessage(msg);    
    }

    // @FXML
    // private void downloadAll() {
    //     logBox.fireEvent(new CopyCompleteEvent(MainFormController.COPY_COMPLETE));
    // }

    @FXML
    private void downloadAll() {
        if(lmm == null) {
            lmm = new LogMessageManager(logBox, messageList);
        }

        if (selectedSourceDir == null) {
            AlertDialogManager.throwAlertDialog("Source folder han not been selected", "Please, select a source folder", AlertType.WARNING);
            return;
        }
        
        //System.out.println(Main.osType);
        if ((byDefaultProp.isSelected() == false) && (copyToBox.getText() == "")) {
            AlertDialogManager.throwAlertDialog("Destination forlder has not been selected", "Please, select a destination folder or choose a 'Copy by default' property", AlertType.WARNING);
            return;
        }

        try {
            switchBtwDownloadAndCancelButtons();

            Boolean byDefaultStatus = toSameFolderProp.isSelected() ? false : byDefaultProp.isSelected();
            Boolean samePathStatus = byDefaultProp.isSelected() ? false : toSameFolderProp.isSelected();
            
            ftm = new FileTransferManager(selectedSourceDir, selectedDstDir, byDefaultStatus, samePathStatus, lmm);

            ftm.setOnSucceeded(e -> finishingOfCopyProcess(ExitMode.SUCCEEDED));
            ftm.setOnCancelled(e -> finishingOfCopyProcess(ExitMode.CANCELLED));
            ftm.setOnFailed(e -> finishingOfCopyProcess(ExitMode.FAILED));

            fileThread = new Thread(ftm, "File copy thread");
            fileThread.setDaemon(true);
            fileThread.start();

        } catch (IllegalThreadStateException itse) {
            log.error(itse.getMessage(), itse);
        }
    }
}
