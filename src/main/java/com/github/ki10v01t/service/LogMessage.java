package com.github.ki10v01t.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LogMessage {
    //date and time
    private static SimpleDateFormat formater = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss:S");
    
    //templates
    private static Integer columnCount = 20;
    private static final String messageBorderTemplate = borderCalc();;
    private static final String copyMessageTemplate = LocaleManager.getInstance().getResourceBundle().getString("message.log.template.copymessage");
    private static final String fileAlreadyExistsTemplate = LocaleManager.getInstance().getResourceBundle().getString("message.log.template.filealreadyexists");
    private static final String infoTemplate = LocaleManager.getInstance().getResourceBundle().getString("message.log.template.info");
    private static final String errorTemplate = LocaleManager.getInstance().getResourceBundle().getString("message.log.template.error");

    private String message;

    private LogMessage() {

    }

    private LogMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
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

    public static synchronized LogMessage createErrorMessage(String message) {
        String date = formater.format(new Date());
        return new LogMessage(messageBorderTemplate + date + " | " + String.format(errorTemplate, message));
    }

    public static synchronized LogMessage createInfoMessage(String message) {
        String date = formater.format(new Date());
        return new LogMessage(messageBorderTemplate + date + " | " + String.format(infoTemplate, message));
    }

    public static synchronized LogMessage createAlreadyExistsMessage(String sourceFileName) {
        String date = formater.format(new Date());
        return new LogMessage(messageBorderTemplate + date + " | " + String.format(fileAlreadyExistsTemplate, sourceFileName));
    }

    public static synchronized LogMessage createCopyMessage(String sourceFileName, String dstFileName) {
        String date = formater.format(new Date());
        return new LogMessage(messageBorderTemplate + date + " | " + String.format(copyMessageTemplate, sourceFileName, dstFileName));        
    }
}
