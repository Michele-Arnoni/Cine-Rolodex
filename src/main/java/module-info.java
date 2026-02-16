module com.cinerolodex {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.cinerolodex to javafx.fxml;
    exports com.cinerolodex;
}
