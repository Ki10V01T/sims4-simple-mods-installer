module com.github.ki10v01t {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.github.ki10v01t to javafx.fxml;
    exports com.github.ki10v01t;
}
