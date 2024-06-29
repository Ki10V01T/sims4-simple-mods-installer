package com.github.ki10v01t.service;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

public class LogMessageManager {
    //GUI text area    
    private ListView<String> logBox;

    private ObservableList<String> messageList;

    public LogMessageManager(ListView<String> logBox, ObservableList<String> messageList) {
        this.logBox = logBox;

        //this.messageList = FXCollections.observableArrayList();
        this.messageList = messageList;
        this.logBox.setItems(messageList);
    }

    
    public synchronized void sendMessage(LogMessage message) throws IllegalStateException {
        if(Thread.currentThread().getId() == 1) {
            messageList.add(message.getMessage());
        } else {
            Platform.runLater(() -> messageList.add(message.getMessage()));
        }
        //logBox.refresh();
    }
}
