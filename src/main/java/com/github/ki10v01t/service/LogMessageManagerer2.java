package com.github.ki10v01t.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class LogMessageManagerer2 {
    //GUI text area    
    private static ListView<String> logBox;
    private static Integer columnCount;

    private static ObservableList<String> messageList;

    //date and time
    private static SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:S");
    
    //templates
    private static String messageBorderTemplate;
    private final static String copyMessageTemplate = ("Copy file from: %s \n To: %s \n");
    private final static String fileAlreadyExistsTemplate = ("File : %s \n is already exists. Skip \n");


    public LogMessageManagerer2(ListView<String> inputLogBox) {
        logBox = inputLogBox;
        columnCount = 10;

        messageList = FXCollections.observableArrayList();
        logBox.setItems(messageList);
        
        messageBorderTemplate = borderCalc();
    }

    private static String borderCalc() {
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

    public static String createAlreadyExistsMessage(String sourceFileName) {
        String date = formater.format(new Date());
        return messageBorderTemplate + date + " | " + String.format(fileAlreadyExistsTemplate, sourceFileName);
    }

    public static String createCopyMessage(String sourceFileName, String dstFileName) {
        String date = formater.format(new Date());
        return messageBorderTemplate + date + " | " + String.format(copyMessageTemplate, sourceFileName, dstFileName);        
    }

    public static synchronized void addMessage(String message) {
        messageList.add(message);
    }
}
