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

import javafx.concurrent.Task;

public class FileTransferManager extends Task<Void> {
    private Logger log = LogManager.getLogger("FileTransferManager");
    private Path selectedSourceDir, selectedDstDir;

    private LogMessageManager lmm;

    private Boolean isDefaultPath;

    private String slashStyle = "";

    private ArrayList<String> modsFileExtentions = new ArrayList<>(Arrays.asList(".package", ".ts4script"));
    private ArrayList<String> trayFileExtentions = new ArrayList<>(Arrays.asList(".rmi", ".sgi", ".blueprint", ".bpi", ".hhi", ".householdbinary", ".room", ".trayitem"));

    public FileTransferManager(Path selectedSourceDir, Path selectedDstDir, Boolean defaultPathFlag, LogMessageManager lmm) {
        this.lmm = lmm;
        this.isDefaultPath = defaultPathFlag;
        this.selectedSourceDir = selectedSourceDir;
        this.selectedDstDir = selectedDstDir;



        if (Main.osType.equals("Windows")) {
            slashStyle = "\\";
        } else {
            slashStyle = "/";
        }
    }

    public void copyModsAndTray() throws IllegalStateException, InterruptedException, IOException {
        String osHome;
        String targetFolderName;

        final ArrayList<Path> pathList =  new ArrayList<>();
        ArrayList<String> extentionsList;
        

        if(isDefaultPath == null) {
            throw new IllegalStateException();
        }

        for(CopyMode mode : CopyMode.values()) {
            switch (mode) {
                case MODS:
                    extentionsList = modsFileExtentions;
                    targetFolderName = "Mods";
                    break;
                case TRAY:
                    extentionsList = trayFileExtentions;
                    targetFolderName = "Tray";
                    break;
                default:
                    throw new IOException();
            }

            pathList.clear();
            //pathList.trimToSize();

            for(String el : extentionsList) {
                try (Stream<Path> walkStream = Files.walk(Paths.get(selectedSourceDir.toUri()))) {
                //try (Stream<Path> walkStream = Files.walk(selectedSourceDir.toAbsolutePath())) {
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

                    lmm.sendMessage(LogMessage.createInfoMessage("Your system type is Windows. Default copy path sets to " + selectedDstDir.toString()));
                } else {
                    String wineprefix = System.getenv("WINEPREFIX");

                    if (wineprefix == null) {
                        osHome = System.getenv("PWD");
                        selectedDstDir = Paths.get(osHome + "/" + targetFolderName + "/" + date);

                        lmm.sendMessage(LogMessage.createInfoMessage("Default WINEPREFIX is not finded. In this case, all files will be saved in:  " + selectedDstDir.toString()));
                    } else {
                        selectedDstDir = Paths.get(wineprefix + "/users/" + System.getProperty("user.name") + "/Documents/Electronic Arts/The Sims 4/" + 
                        targetFolderName + "/" + date);
                        
                        lmm.sendMessage(LogMessage.createInfoMessage("Default copy path sets to founded default WINEPREFIX " + selectedDstDir.toString()));
                    }
                }
            } else if (mode == CopyMode.TRAY) {
                Path currentDir = selectedDstDir.toAbsolutePath();

                if (selectedDstDir == null) {
                    throw new IllegalStateException();
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

                lmm.sendMessage(LogMessage.createInfoMessage("Current path for Trayitems will be a " + selectedDstDir.toString()));
            }

            Files.createDirectories(selectedDstDir);
            
            copyFileToDestination(pathList, selectedDstDir);
    }
    }

    private void copyFileToDestination(ArrayList<Path> inputFilesList, Path destinationFolder) {        
        inputFilesList.stream().filter(f -> f.toFile().exists()).forEach(file -> {
            Path destinationFile = Paths.get(destinationFolder.toString() + slashStyle + file.getFileName().toString());
            try (OutputStream fos = Files.newOutputStream(destinationFile, StandardOpenOption.WRITE, StandardOpenOption.CREATE_NEW)) {
                Files.copy(file,fos);
                lmm.sendMessage(LogMessage.createCopyMessage(file.toString(), destinationFile.toString()));
                Thread.sleep(1000);
                //updateMessage(lmm.createCopyMessage(file.toString(), destinationFile.toString()));
            } catch (FileAlreadyExistsException faee) {
                log.info(faee.getMessage(), faee);
                lmm.sendMessage(LogMessage.createAlreadyExistsMessage(file.toString()));
                //updateMessage(lmm.createAlreadyExistsMessage(file.toString()));
                //lmm.writeMessage(new LogMessage(file.toString()));
            } catch (SecurityException se) {
                log.error(se.getMessage(), se);
            } catch (InterruptedException ie) {
                log.error(ie.getMessage(), ie);
            } 
            catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        });
    }

    @Override
    public Void call(){
        try {
            copyModsAndTray();
        } catch (IOException ioe) {
            log.error(ioe.getMessage(), ioe);
        } catch (InterruptedException ie) {
            log.error(ie.getMessage(), ie);
        }
        return null;
    }
}
