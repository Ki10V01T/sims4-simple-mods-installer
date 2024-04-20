package com.github.ki10v01t.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogMessageManager {
    private SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:S");
    private String date;
    
    private String messageBorderTemplate;

    private String copyMessageTemplate = ("Copy file from: %s \n To: %s \n");
    private String fileAlreadyExistsTemplate = ("File : %s \n is already exists. Skip \n");
    
    public LogMessageManager(Integer columnCount) {
        this.messageBorderTemplate = borderCalc(columnCount);
    }

    private String borderCalc(Integer columnCount) {
        return new String(Stream.of(columnCount).map(x -> {
            String result = "";
            for(int i = 0; i < x; i++) {
                result += "="; 
            }
            result += "\n";
            return result;
        }).collect(Collectors.joining()));
    }

    private String collectMessage (String inputTemplate, String inputString1, String inputString2) {
        date = formater.format(new Date());
        return messageBorderTemplate + date + " | " + String.format(inputTemplate, inputString1, inputString2);
    }

    public String createCopyMessage (String inputString1, String inputString2) {
        return collectMessage(copyMessageTemplate, inputString1, inputString2);
    }

    public String createFileAlreadyExistsMessage (String inputString) {
        return collectMessage(fileAlreadyExistsTemplate, inputString, "");
    }

}
