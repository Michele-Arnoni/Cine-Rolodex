module com.cinerolodex {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires java.desktop;

    // Permette a JavaFX di caricare le View e i Controller
    opens com.cinerolodex to javafx.fxml, org.junit.platform.commons;
    exports com.cinerolodex;

    // Permette alla TableView di JavaFX di leggere i getter del Model (IFilm, ecc.)
    opens com.cinerolodex.model to javafx.base, javafx.fxml, org.junit.platform.commons;

    // Apre i manager e le factory sia a JavaFX che a JUnit (Per poter fare i test)
    opens com.cinerolodex.manager to javafx.fxml, org.junit.platform.commons;
    opens com.cinerolodex.model.factory to javafx.fxml, org.junit.platform.commons;
}