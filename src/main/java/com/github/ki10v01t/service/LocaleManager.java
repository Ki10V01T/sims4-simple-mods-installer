package com.github.ki10v01t.service;

import java.util.ResourceBundle;

public class LocaleManager {
    private static volatile LocaleManager instance;
    private static volatile ResourceBundle resourceBundle;
    
    private LocaleManager() {}

    public static void createInstance(ResourceBundle inputBundle) {
        if (resourceBundle == null) {
            synchronized (LocaleManager.class) {
                if (instance == null) {
                    instance = new LocaleManager();
                    resourceBundle = inputBundle;
                }
            }
        }
    }

    public static LocaleManager getInstance() throws NullPointerException {
        if (instance == null) {
            throw new NullPointerException("First, you need to create an instance");    
        }
        return instance;
    }

    public ResourceBundle getResourceBundle() throws NullPointerException {
        if (instance == null) {
            throw new NullPointerException("First, you need to create an instance");    
        }
        return resourceBundle;
    }
}
