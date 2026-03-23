module com.cinerolodex {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    opens com.cinerolodex to javafx.fxml;
    exports com.cinerolodex;

    opens com.cinerolodex.model to javafx.base, javafx.fxml;
    
    //opens com.cinerolodex.test to org.junit.platform.commons;
}
