package com.cinerolodex;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;

import java.io.File;
import java.util.List;

import com.cinerolodex.contract.IFilm;
import com.cinerolodex.contract.ICatalog;
import com.cinerolodex.manager.CatalogManager;
import com.cinerolodex.contract.IFilterEngine;
import com.cinerolodex.service.FilterService;
import com.cinerolodex.manager.MediaPlayerManager;
import com.cinerolodex.model.Anno;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Rating;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.StatoVisione;
import com.cinerolodex.model.factory.FilmFactory;

public class MainController {
    @FXML private TableView<IFilm> movieTable;
    @FXML private TableColumn<IFilm, String> titleColumn;
    @FXML private TableColumn<IFilm, Regista> directorColumn;
    @FXML private TableColumn<IFilm, Genere> genreColumn;
    @FXML private TableColumn<IFilm, Anno> yearColumn;
    @FXML private TableColumn<IFilm, Rating> ratingColumn;

    @FXML private TextField searchField;

    @FXML private ComboBox<String> genreFilter;    // Da ComboBox<Genere> a ComboBox<String>
    @FXML private ComboBox<String> directorFilter; // Da ComboBox<Regista> a ComboBox<String>
    @FXML private ComboBox<String> yearFilter;     // Da ComboBox<Anno> a ComboBox<String>
    @FXML private ComboBox<String> statusFilter;   // Da ComboBox<StatoVisione> a ComboBox<String>
    @FXML private ComboBox<String> ratingFilter;   // Da ComboBox<Rating> a ComboBox<String>

    private final ICatalog catalog = CatalogManager.getInstance();
    private final IFilterEngine filterEngine = FilterService.getInstance();

    @FXML
    public void initialize() {


        // --- CONFIGURAZIONE COLONNE ---
        setupTableColumns();

        // --- POPOLAMENTO INIZIALE DATABASE ---
        refreshTable();
        populateFilterMenus();

        // --- LOGICA DI RICERCA E FILTRO UNIFICATA ---
        /*
         * IMPLEMENTAZIONE DEL DESIGN PATTERN OBSERVER:
         * Ogni volta che il testo cambia o una combo viene selezionata, scatta il metodo applyFilters()
         */
        searchField.textProperty().addListener((obs, oldV, newV) -> applyFilters());
        genreFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        directorFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        yearFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        statusFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
        ratingFilter.valueProperty().addListener((obs, oldV, newV) -> applyFilters());
    }

    /**
     * Raccoglie tutti i criteri dalla UI e chiama l'unico metodo filter del Service
     */
    private void applyFilters() {
        String titleText = searchField.getText();
        
        // Se è selezionato "Tutti", passiamo null come parametro al servizio di filtraggio
        String g = getFilterValue(genreFilter);
        String d = getFilterValue(directorFilter);
        String a = getFilterValue(yearFilter);
        String s = getFilterValue(statusFilter);
        String r = getFilterValue(ratingFilter);

        // Chiamata al metodo filter
        List<IFilm> filtered = filterEngine.filter(titleText, g, a, d, s, r);  //Uso del layer di servizio per il filtraggio
        movieTable.setItems(FXCollections.observableArrayList(filtered));
    }

    // Metodo helper per gestire la voce "Tutti" nei menu a tendina
    private String getFilterValue(ComboBox<String> combo) {
        String val = combo.getValue();
        return (val == null || val.equals("Tutti")) ? null : val;
    }

    /**
     * Riempie le ComboBox con i valori UNICI presenti nel catalogo
     */
    private void populateFilterMenus() {
        List<IFilm> all = catalog.showCollection();
        
        // Genere
        genreFilter.getItems().setAll("Tutti");
        genreFilter.getItems().addAll(all.stream().map(f -> f.getGenere().getNome()).distinct().sorted().toList());
        genreFilter.setValue("Tutti"); // Imposta il default

        // Regista
        directorFilter.getItems().setAll("Tutti");
        directorFilter.getItems().addAll(all.stream().map(f -> f.getRegista().getNome()).distinct().sorted().toList());
        directorFilter.setValue("Tutti");

        // Anno
        yearFilter.getItems().setAll("Tutti");
        yearFilter.getItems().addAll(all.stream().map(f -> String.valueOf(f.getAnno().getValore())).distinct().sorted().toList());
        yearFilter.setValue("Tutti");

        // Stato e Rating (usando i nomi degli Enum)
        statusFilter.getItems().setAll("Tutti");
        for (StatoVisione s : StatoVisione.values()) statusFilter.getItems().add(s.name());
        statusFilter.setValue("Tutti");

        ratingFilter.getItems().setAll("Tutti");
        for (Rating r : Rating.values()) ratingFilter.getItems().add(r.name());
        ratingFilter.setValue("Tutti");
    }

    // Metodo helper per ricaricare la tabella dopo modifiche al catalogo (aggiunta/rimozione/aggiornamento)
    private void refreshTable() {
        movieTable.setItems((ObservableList<IFilm>) catalog.showCollection());
    }

    // Metodo per resettare tutti i filtri e mostrare l'intera collezione
    @FXML
    private void handleResetFilters() {
        searchField.clear();
        genreFilter.setValue(null);
        directorFilter.setValue(null);
        yearFilter.setValue(null);
        statusFilter.setValue(null);
        ratingFilter.setValue(null);
        
        refreshTable();
        System.out.println("Filtri resettati.");
    }

    // Metodo per configurare le colonne della tabella UI e abilitare l'editing in-place
    private void setupTableColumns() {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("titolo"));
        directorColumn.setCellValueFactory(new PropertyValueFactory<>("regista"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genere"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("anno"));
        ratingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));

        // Editing Titolo
        titleColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        titleColumn.setOnEditCommit(e -> {
            catalog.updateEntry(e.getRowValue(),
            FilmFactory.getInstance().createWithNewTitle(e.getRowValue(), e.getNewValue()));
            populateFilterMenus();
        });

        // Editing Regista
        directorColumn.setCellFactory(TextFieldTableCell.<IFilm, Regista>forTableColumn(new StringConverter<Regista>() {
            @Override public String toString(Regista r) { return (r != null) ? r.getNome() : ""; }
            @Override public Regista fromString(String s) { return new Regista(s); }
        }));
        directorColumn.setOnEditCommit(e -> {
            catalog.updateEntry(e.getRowValue(),
                FilmFactory.getInstance().createWithNewRegista(e.getRowValue(), e.getNewValue().getNome()));
            populateFilterMenus();
        });

        // Editing Genere
        genreColumn.setCellFactory(TextFieldTableCell.<IFilm, Genere>forTableColumn(new StringConverter<Genere>() {
            @Override public String toString(Genere g) { return (g != null) ? g.getNome() : ""; }
            @Override public Genere fromString(String s) { return new Genere(s); }
        }));
        genreColumn.setOnEditCommit(e -> {
            catalog.updateEntry(e.getRowValue(),
                FilmFactory.getInstance().createWithNewGenere(e.getRowValue(), e.getNewValue().getNome()));
            populateFilterMenus();
        });

        // Editing Anno
        yearColumn.setCellFactory(TextFieldTableCell.<IFilm, Anno>forTableColumn(new StringConverter<Anno>() {
            @Override public String toString(Anno a) { return (a != null) ? String.valueOf(a.getValore()) : ""; }
            @Override public Anno fromString(String s) { try { return new Anno(Integer.parseInt(s)); } catch (Exception ex) { return new Anno(0); } }
        }));
        yearColumn.setOnEditCommit(e -> {
            catalog.updateEntry(e.getRowValue(),
                FilmFactory.getInstance().createWithNewYear(e.getRowValue(), e.getNewValue().getValore()));
            populateFilterMenus();
        });

        // Editing Rating
        ratingColumn.setCellFactory(ComboBoxTableCell.forTableColumn(Rating.values()));
        ratingColumn.setOnEditCommit(e -> {
            catalog.updateEntry(e.getRowValue(),
                FilmFactory.getInstance().createWithNewRating(e.getRowValue(), e.getNewValue()));
            populateFilterMenus();
        });
    }

    /**
     * Metodo per gestire l'aggiunta di un nuovo film tramite FileChooser, delegando al CatalogManager e aggiornando i menu a tendina
     * @see ./docs/SequenceDiagrams.md#aggiunta-nuovo-film
     * */
    @FXML
    private void handleAddMovie() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Video Files", "*.mp4", "*.mkv", "*.avi"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            catalog.addEntry(selectedFile.toPath()); //delega al CatalogManager la creazione dell'istanza di IFilm e l'aggiunta al catalogo
            populateFilterMenus(); // Aggiorniamo i menu se viene aggiunto un nuovo film con un nuovo genere/regista/anno
        }
    }

    /**
     * Metodo per gestire la riproduzione del film selezionato, delegando al MediaPlayerManager
     * */
    @FXML
    private void handlePlay() {
        IFilm selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            MediaPlayerManager.getInstance().play(selected.getPath());
        }
    }

    /**
     * Metodo per gestire la cancellazione del film selezionato dal catalogo
     * @see ./docs/SequenceDiagrams.md#rimozione-film
     * */
    @FXML
    private void handleDelete() {
        IFilm selected = movieTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            catalog.removeEntry(selected);
            populateFilterMenus();
        }
    }
}