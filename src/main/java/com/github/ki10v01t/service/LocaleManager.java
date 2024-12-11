package com.github.ki10v01t.service;

import java.util.ResourceBundle;

import com.github.ki10v01t.Main;

public class LocaleManager {
    private static final LocaleManager instance =  new LocaleManager();
    private static ResourceBundle resourceBundle;
    
    private LocaleManager() {}

    public static String getString(String propertyName) {
        if(resourceBundle == null) {
            throw new NullPointerException("LocaleManager - ResourceBundle is null");
        }
        return resourceBundle.getString(propertyName);
    }

    public void setResourceBundle(ResourceBundle res) {
        resourceBundle = res;
    }

    public static LocaleManager getInstance() {
        return instance;
    }

}
