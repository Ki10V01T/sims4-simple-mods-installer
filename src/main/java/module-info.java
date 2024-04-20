module com.github.ki10v01t {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.apache.logging.log4j.core;
    requires javafx.graphics;

    opens com.github.ki10v01t to javafx.fxml;
    exports com.github.ki10v01t;
}
