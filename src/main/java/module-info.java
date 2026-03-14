module com.cinerolodex {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.cinerolodex to javafx.fxml;
    exports com.cinerolodex;
    requires java.desktop;

    opens com.cinerolodex.model to javafx.base, javafx.fxml;
}
