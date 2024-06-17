package com.github.ki10v01t.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableArray;
import javafx.event.EventHandler;
import javafx.scene.control.TextArea;
import javafx.scene.input.MouseEvent;

public class LogMessageManager {
    //GUI text area    
    private TextArea logBox;
    private Integer columnCount;

    //date and time
    private static SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:S");
    
    //templates
    private static String messageBorderTemplate;
    private final static String copyMessageTemplate = ("Copy file from: %s \n To: %s \n");
    private final static String fileAlreadyExistsTemplate = ("File : %s \n is already exists. Skip \n");


    public LogMessageManager(TextArea logBox) {
        this.logBox = logBox; 
        this.columnCount = logBox.getPrefColumnCount();

        messageBorderTemplate = borderCalc();
        queue = new ConcurrentLinkedQueue<>();
    }

    private String borderCalc() {
        if (columnCount == null) {throw new NullPointerException();}

        return new String(Stream.of(columnCount).map(x -> {
            String result = "";
            for(int i = 0; i < x; i++) {
                result += "="; 
            }
            result += "\n";
            return result;
        }).collect(Collectors.joining()));
    }

    public String createAlreadyExistsMessage(String sourceFileName) {
        String date = formater.format(new Date());
        return messageBorderTemplate + date + " | " + String.format(fileAlreadyExistsTemplate, sourceFileName);
    }

    public String createCopyMessage(String sourceFileName, String dstFileName) {
        String date = formater.format(new Date());
        return messageBorderTemplate + date + " | " + String.format(copyMessageTemplate, sourceFileName, dstFileName);        
    }

    public synchronized void sendMessage(String message) {
        Platform.runLater(() -> logBox.appendText(message));
    }

    public synchronized void refreshLog() {
        logBox.sceneProperty().get().
    }

    public synchronized void addMessage() {
    }

    public void bindLog(ReadOnlyProperty<String> str) {
        logBox.textProperty().bindBidirectional(str);
}
