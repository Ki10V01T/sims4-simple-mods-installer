package com.github.ki10v01t.service;

import java.io.IOException;
import java.io.InputStream;
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

import com.github.ki10v01t.Main;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert.AlertType;

public class FileTransferManager extends Task<Boolean> {
    private Logger log = LogManager.getLogger("FileTransferManager");
    private Path selectedSourceDir, selectedDstDir;
    private String copyTo;

    private LogMessageManager lmm;

    private CopyMode mode;
    private Boolean isDefaultPath;

    private String slashStyle = "";

    private ArrayList<String> modsFileExtentions = new ArrayList<>(Arrays.asList(".package", ".ts4script"));
    private ArrayList<String> trayFileExtentions = new ArrayList<>(Arrays.asList(".rmi", ".sgi", ".blueprint", ".bpi", ".hhi", ".householdbinary", ".room", ".trayitem"));
    private ArrayList<Path> modsFilesList = new ArrayList<>();
    private ArrayList<Path> trayFilesList = new ArrayList<>();

    public FileTransferManager(CopyMode copyMode, Path selectedSourceDir, Path selectedDstDir, String copyTo, Boolean defaultPathFlag, LogMessageManager lmm) {
        this.lmm = lmm;
        this.isDefaultPath = defaultPathFlag;
        this.mode = copyMode;
        this.selectedSourceDir = selectedSourceDir;
        this.selectedDstDir = selectedDstDir;
        this.copyTo = copyTo;

        if (Main.osType.equals("Windows")) {
            slashStyle = "\\";
        } else {
            slashStyle = "/";
        }
    }
    
    public void setCopyMode(CopyMode cm) {
        this.mode = cm;
    }

    public void setIsDefaultPath(Boolean flag) {
        this.isDefaultPath = flag;
    }

    public void copyModsAndTray() throws IOException, InterruptedException{
        String osHome;
        String targetFolderName;
        ArrayList<Path> pathList;
        ArrayList<String> extentionsList;

        if((mode == null) || (isDefaultPath == null)) {
            throw new IOException();
        }

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
            
            if (mode == CopyMode.MODS) {
                date = formater.format(new Date());
            } else {
                date = "";
            } 

            if (Main.osType.equals("Windows")) {
                Process p =  Runtime.getRuntime().exec("reg query \"HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders\" /v personal");
                p.waitFor();

                InputStream in = p.getInputStream();
                byte[] b = new byte[in.available()];
                in.read(b);
                in.close();

                osHome = new String(b);
                osHome = osHome.split("\\s\\s+")[4];
                //osHome = System.getenv("USERPROFILE");


                selectedDstDir = Paths.get(osHome + "\\Electronic Arts\\The Sims 4\\" + targetFolderName + "\\" + date);

                AlertDialogManager.throwAlertDialog("Check your WINDOWS path", 
                                "Your system type is Windows. Default copy path sets to " + selectedDstDir.toString(), AlertType.INFORMATION);
            } else {
                String wineprefix = System.getenv("WINEPREFIX");

                if (wineprefix == null) {
                    osHome = System.getenv("PWD");
                    selectedDstDir = Paths.get(osHome + "/" + targetFolderName + "/" + date);

                    AlertDialogManager.throwAlertDialog("Environment variable WINEPREFIX is not defined", 
                                "Default WINEPREFIX is not finded. In this case, all files will be saved in:  " + selectedDstDir.toString() , AlertType.WARNING);
                } else {
                    selectedDstDir = Paths.get(wineprefix + "/users/" + System.getProperty("user.name") + "/Documents/Electronic Arts/The Sims 4/" + 
                    targetFolderName + "/" + date);
                    
                    AlertDialogManager.throwAlertDialog("Check your WINEPREFIX path", 
                                "Default copy path sets to founded default WINEPREFIX " + selectedDstDir.toString(), AlertType.INFORMATION);
                }
            }
        } else if (mode == CopyMode.TRAY) {
            Path currentDir = selectedDstDir.toAbsolutePath();
            
            
            if (currentDir == null) {
                //TODO:
                //currentDir = Paths.get(copyToBox.getText());
                currentDir = Paths.get(copyTo);
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

            AlertDialogManager.throwAlertDialog("Changing path to Trayitems", "Current path for Trayitems will be a " + selectedDstDir.toString(), AlertType.INFORMATION);
        }

        Files.createDirectories(selectedDstDir);
        copyFileToDestination(pathList, selectedDstDir);
    }

    private void copyFileToDestination(ArrayList<Path> inputFilesList, Path destinationFolder) {        
        inputFilesList.stream().filter(f -> f.toFile().exists()).forEach(file -> {
            Path destinationFile = Paths.get(destinationFolder.toString() + slashStyle + file.getFileName().toString());
            try (OutputStream fos = Files.newOutputStream(destinationFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
                Files.copy(file,fos);
                Thread.sleep(1000);
                lmm.sendMessage(lmm.createCopyMessage(file.toString(), destinationFile.toString()));
                //updateMessage(lmm.createCopyMessage(file.toString(), destinationFile.toString()));
            } catch (FileAlreadyExistsException faee) {
                log.info(faee.getMessage(), faee);
                lmm.sendMessage(lmm.createAlreadyExistsMessage(file.toString()));
                //updateMessage(lmm.createAlreadyExistsMessage(file.toString()));
                //lmm.writeMessage(new LogMessage(file.toString()));
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            } catch (InterruptedException ie) {

            }
        });
    }

    // public ChangeListener<String> fileListener() {
    //     return new
    // }

    // @Override
    // public void run() {
    //     
    // }

    @Override
    public Boolean call(){
        try {
            copyModsAndTray();
            return true;
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
            return false;
        } catch (InterruptedException ie) {
            log.error(ie.getMessage(), ie);
            return false;
        }
    }
}
