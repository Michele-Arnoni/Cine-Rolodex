module com.cinerolodex {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.cinerolodex to javafx.fxml;
    exports com.cinerolodex;
}
