package com.github.ki10v01t.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.ki10v01t.MainFormController;

public class LogMessage {    
    private static Integer columnCount = MainFormController.columnCount;

    private String date;
    private SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:S");
    
    private String messageBorderTemplate;
    private String copyMessageTemplate = ("Copy file from: %s \n To: %s \n");
    private String fileAlreadyExistsTemplate = ("File : %s \n is already exists. Skip \n");

    private String sourceFileName, dstFileName;
    private String message;

    public LogMessage(String sourceFileName) {
        this.sourceFileName = sourceFileName;

        createAlreadyExistsMessage();
    }

    public LogMessage(String sourceFileName, String dstFileName) {
        this.sourceFileName = sourceFileName;
        this.dstFileName = dstFileName; 

        createCopyMessage();
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

    private void createAlreadyExistsMessage() {
        date = formater.format(new Date());
        message = messageBorderTemplate + date + " | " + String.format(fileAlreadyExistsTemplate, sourceFileName);
    }

    private void createCopyMessage() {
        date = formater.format(new Date());
        message = messageBorderTemplate + date + " | " + String.format(copyMessageTemplate, sourceFileName, dstFileName);        
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }
}
