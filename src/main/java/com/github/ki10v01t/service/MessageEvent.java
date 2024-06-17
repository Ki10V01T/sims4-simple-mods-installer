package com.github.ki10v01t.service;

import javafx.event.Event;
import javafx.event.EventTarget;
import javafx.event.EventType;
import javafx.scene.input.InputEvent;

public class MessageEvent extends Event{
    public static final EventType<MessageEvent> ADD =
            new EventType<MessageEvent>("MOUSE");

    public MessageEvent(Object source, EventTarget target, EventType<? extends Event> eventType) {
        super(source, target, eventType);
        //TODO Auto-generated constructor stub
    }

    

}
