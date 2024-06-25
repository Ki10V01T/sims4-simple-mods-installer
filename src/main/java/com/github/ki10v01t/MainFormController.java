package com.github.ki10v01t;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ki10v01t.service.AlertDialogManager;
import com.github.ki10v01t.service.FileTransferManager;
import com.github.ki10v01t.service.LogMessageManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
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
    private ListView<String> logBox;
    @FXML
    private Button downloadButton;  

    private Logger log = LogManager.getLogger("MainFormController");

    private Path selectedSourceDir, selectedDstDir;
    private DirectoryChooser dc =  new DirectoryChooser();
    private FileTransferManager ftm;
    private LogMessageManager lmm;

    private ObservableList<String> messageList = FXCollections.observableArrayList();;
    final ExecutorService threadPool = Executors.newFixedThreadPool(1);
    
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
            downloadButton.setDisable(true);
            
            ftm = new FileTransferManager(selectedSourceDir, selectedDstDir, byDefaultProp.isSelected(), lmm);
            //Task<Boolean> ftm = new FileTransferManager(selectedSourceDir, selectedDstDir, byDefaultProp.isSelected(), lmm);

            //CompletableFuture<Void> fileThread = CompletableFuture.runAsync(() -> ftm.call(), Main.threadPool);
            //threadPool.submit(ftm, resultMods);


            //ftm.call();
            Thread fileThread = new Thread(ftm, "File copy thread");
            fileThread.setDaemon(true);
            fileThread.start();
            fileThread.start();
            // while(fileThread.isAlive()) {
            //     continue;
            // }
        // }
        } catch (IllegalThreadStateException itse) {
            log.error(itse.getMessage(), itse);
        } 
        
        // catch (InterruptedException ie) {
        //     log.error(ie.getMessage(), ie);
        // } catch (ExecutionException ee) {
        //     log.error(ee.getMessage(), ee);
        // }
        
        finally {
            downloadButton.setDisable(false);
        }
    }
}
