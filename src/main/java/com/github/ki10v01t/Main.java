package com.github.ki10v01t;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Stream;

import com.github.ki10v01t.service.LocaleManager;

/**
 * JavaFX App
 */
public class Main extends Application {
    private static Scene scene;
    public static final String osType = Stream.of(System.getProperty("os.name").split(" ")).findFirst().get();

    @Override
    public void start(@SuppressWarnings("exports") Stage stage) throws IOException {

        scene = new Scene(loadFXML("main_form"), 532, 540);
        stage.setTitle("The Sims 4 Simple Mods Manager");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    

    private static void setLocale() {
        String lang = System.getProperty("user.language");
        Locale locale = Locale.getDefault();
        System.out.println("lang = " + lang + "\nlocale = " + locale.getLanguage());
        switch (locale.getLanguage()) {
            case "ru" -> LocaleManager.createInstance(ResourceBundle.getBundle("com.github.ki10v01t.locales.locale_ru", locale));
            default -> LocaleManager.createInstance(ResourceBundle.getBundle("com.github.ki10v01t.locales.locale_en", locale)); 
        }
        //res = ResourceBundle.getBundle("locale", Locale.getDefault());
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        setLocale();
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource(fxml + ".fxml"), LocaleManager.getInstance().getResourceBundle());
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }

}