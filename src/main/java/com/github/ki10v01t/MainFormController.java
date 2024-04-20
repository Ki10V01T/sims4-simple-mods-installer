package com.github.ki10v01t;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.github.ki10v01t.service.LogMessageManager;

import javafx.beans.property.IntegerProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

enum COPY_MODE {
    MODS,
    TRAY;
}

public class MainFormController {
    private Logger log = LogManager.getLogger("MainFormController");

    private Path selectedSourceDir, selectedDstDir;
    private DirectoryChooser dc =  new DirectoryChooser();
    
    @FXML
    private CheckBox byDefaultProp; 
    @FXML
    private TextField copyFromBox;
    @FXML
    private TextField copyToBox;
    @FXML
    private TextArea logBox;

    private LogMessageManager lmm;

    
    private ArrayList<String> modsFileExtentions = new ArrayList<>(Arrays.asList(".package", ".ts4script"));
    private ArrayList<String> trayFileExtentions = new ArrayList<>(Arrays.asList(".rmi", ".sgi", ".blueprint", ".bpi", ".hhi", ".householdbinary", ".room", ".trayitem"));
    private ArrayList<Path> modsFilesList = new ArrayList<>();
    private ArrayList<Path> trayFilesList = new ArrayList<>();
    
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

    private void copyModsAndTray(COPY_MODE mode, Boolean isDefaultPath) throws IOException {
        String osHome;
        String targetFolderName;
        ArrayList<Path> pathList;
        ArrayList<String> extentionsList;

        switch (mode) {
            case MODS:
                pathList = modsFilesList;
                extentionsList = modsFileExtentions;
                targetFolderName = "Mods";
                break;
            case TRAY:
                extentionsList = trayFileExtentions;
                pathList = trayFilesList;
                targetFolderName = "Tray";
                break;
            default:
                throw new IOException();
        }

        for(String el : extentionsList) {
            try (Stream<Path> walkStream = Files.walk(Paths.get(selectedSourceDir.toUri()))) {
                walkStream.filter(p -> p.toFile().isFile()).forEach(f -> {
                    if (f.toString().endsWith(el)) {
                        pathList.add(f);
                    }
                });
            } catch (IOException ioe) {
                log.error(ioe.getMessage());
            }
        }

        if (isDefaultPath == true) {
            SimpleDateFormat formater = new SimpleDateFormat("dd_MM_yyyy");
            String date;
            
            if (mode == COPY_MODE.MODS) {
                date = formater.format(new Date());
            } else {
                date = "";
            } 

            if (Main.osType.equals("Windows")) {
                osHome = System.getenv("USERPROFILE");
                selectedDstDir = Paths.get(osHome + "\\Documents\\Electronic Arts\\The Sims 4\\" + targetFolderName + "\\" + date);

                throwAlertDialog("Check your WINDOWS path", 
                                "Your system type is Windows. Default copy path sets to " + selectedDstDir.toString() , AlertType.INFORMATION);
            } else {
                String wineprefix = System.getenv("WINEPREFIX");

                if (wineprefix == null) {
                    osHome = System.getenv("PWD");
                    selectedDstDir = Paths.get(osHome + "/" + targetFolderName + "/" + date);

                    throwAlertDialog("Environment variable WINEPREFIX is not defined", 
                                "Default WINEPREFIX is not finded. In this case, all files will be saved in:  " + selectedDstDir.toString() , AlertType.WARNING);
                } else {
                    selectedDstDir = Paths.get(wineprefix + "/users/" + System.getProperty("user.name") + "/Documents/Electronic Arts/The Sims 4/" + 
                    targetFolderName + "/" + date);
                    
                    throwAlertDialog("Check your WINEPREFIX path", 
                                "Default copy path sets to founded default WINEPREFIX " + selectedDstDir.toString() , AlertType.INFORMATION);
                }
            }
        } else if (mode == COPY_MODE.TRAY) {
            Path currentDir = selectedDstDir.toAbsolutePath();
            String slashStyle = "";
            
            if (currentDir == null) {
                currentDir = Paths.get(copyToBox.getText());
            }

            if (Main.osType.equals("Windows")) {
                slashStyle = "\\";
            } else {
                slashStyle = "/";
            }

            do {
                if (currentDir.endsWith("Mods")) {
                    selectedDstDir = currentDir.getParent();
                    break;
                }
                currentDir = currentDir.getParent();

                if (currentDir == null) {
                    break;
                }
            } while (currentDir.endsWith("Mods") == false);


            selectedDstDir = Paths.get(selectedDstDir.toAbsolutePath().toString() + slashStyle + "Tray");
            throwAlertDialog("Changing path to Trayitems", "Current path for Trayitems will be a " + selectedDstDir.toString(), AlertType.INFORMATION);
        }

        Files.createDirectories(selectedDstDir);
        copyFileToDestination(pathList, selectedDstDir);
    }

    private void copyFileToDestination(ArrayList<Path> inputFilesList, Path destinationFolder) {
        String slashStyle;

        if (Main.osType.equals("Windows")) {
            slashStyle = "\\";
        } else {
            slashStyle = "/";
        }
        
        inputFilesList.stream().filter(f -> f.toFile().exists()).forEach(file -> {
            Path destinationFile = Paths.get(destinationFolder.toString() + slashStyle + file.getFileName().toString());
            try (OutputStream fos = Files.newOutputStream(destinationFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
                Files.copy(file,fos);
                logBox.appendText(lmm.createCopyMessage(file.toString(), destinationFile.toString()));
            } catch (FileAlreadyExistsException faee) {
                log.info(faee.getMessage(), faee);
                logBox.appendText(lmm.createFileAlreadyExistsMessage(file.toString()));
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        });
    }
    

    @FXML
    private void downloadAll() {
        if(lmm == null) {
            lmm = new LogMessageManager(logBox.getPrefColumnCount());
        }

        if (selectedSourceDir == null) {
            throwAlertDialog("Source folder han not been selected", "Please, select a source folder", AlertType.WARNING);
            return;
        }
        
        //System.out.println(Main.osType);
        if ((byDefaultProp.isSelected() == false) && (copyToBox.getText() == "")) {
            throwAlertDialog("Destination forlder has not been selected", "Please, select a destination folder or choose a 'Copy by default' property", AlertType.WARNING);
            return;
        }

        try {
            for(COPY_MODE el : COPY_MODE.values()) {
                copyModsAndTray(el, byDefaultProp.isSelected());
            }
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        }
    }
}
