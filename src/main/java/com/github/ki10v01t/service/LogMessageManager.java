package com.github.ki10v01t.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class LogMessageManager {
    //GUI text area    
    private ListView<String> logBox;
    private Integer columnCount;

    private ObservableList<String> messageList;

    //date and time
    private static SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:S");
    
    //templates
    private static String messageBorderTemplate;
    private final static String copyMessageTemplate = ("Copy file from: %s \n To: %s \n");
    private final static String fileAlreadyExistsTemplate = ("File : %s \n is already exists. Skip \n");


    public LogMessageManager(ListView<String> logBox) {
        this.logBox = logBox;
        this.columnCount = 10;

        this.messageList = FXCollections.observableArrayList();
        this.logBox.setItems(messageList);
        
        messageBorderTemplate = borderCalc();
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

    public synchronized void addMessage(String message) {
        messageList.add(message);
    }
}
