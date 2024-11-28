module com.sergio.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires java.desktop;
    requires org.json;
    requires java.logging;


    opens com.sergio.demo to javafx.fxml;
    exports com.sergio.demo;
}