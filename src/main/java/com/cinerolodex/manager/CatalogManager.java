package com.cinerolodex.manager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.file.Path;

import com.cinerolodex.contract.IFileSystemManager;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.model.RawElement;
import com.cinerolodex.contract.IPersistence;

import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart.Data;

public class CatalogManager implements com.cinerolodex.contract.ICatalog {
    private static CatalogManager instance = null;
    private final List<IFilm> movies; // ObservableList dei film presenti nel catalogo, utilizzata per aggiornare la UI in tempo reale
    private final IPersistence dataManager; // Riferimento al DataManager per le richieste al DATABASE
    private final IFileSystemManager fileSystemManager; // Riferimento al FileSystemManager per le richieste al FILE SYSTEM
    

    private CatalogManager() {
        // Inizializzazione della ObservableList
        this.movies = FXCollections.observableArrayList();
        
        // Ottenimento dell'istanza del DatabaseManager (Singleton)
        this.dataManager = DatabaseManager.getInstance();

        // Ottenimento dell'istanza del FileSystemManager (Singleton)
        this.fileSystemManager = FileSystemManager.getInstance();

        // Caricamento dei film dal database all'avvio dell'applicazione
        IPersistence persistence = DatabaseManager.getInstance();
        List<IFilm> loadedFilms = persistence.loadAll();
        movies.addAll(loadedFilms);
    }
    

    public static synchronized CatalogManager getInstance() {
        if (instance == null) {
            instance = new CatalogManager();
        }
        return instance;
    }

    @Override
    public List<IFilm> showCollection() {
        // Ritorna una lista non modificabile, solo questa classe deve avere il controllo sul catalogo.
        return Collections.unmodifiableList(movies);
    }

    @Override
    public void addEntry(Path path) { //aggiunge un film al catalogo dato il path del file.
        
    }

    @Override
    public void removeEntry(IFilm film) { //rimuove un film dal catalogo.
        /*
         * Bisogna gestire un possibile errore che generebbe un disallineamento dei dati tra catalogo e database.
         */
        try {
            dataManager.delete(film); // Se il metodo solleva un'eccezione, il codice si ferma
            movies.remove(film);      // Viene eseguito solo se delete() ha avuto successo
        } catch (Exception e) {
            // Gestione dell'errore (es. mostrare una finestra di dialogo all'utente)
        }
    }

    @Override
    public void updateEntry(IFilm film) { //aggiorna le informazioni di un film esistente nel catalogo dato l'oggetto Film con le nuove informazioni.
        dataManager.update(film);
    }
    
    @Override
    public IFilm createFilm(RawElement raw) { //FACTORY METHOD: crea un oggetto Film a partire da un RawElement, che contiene le informazioni grezze del film.
        return null;
    }
}
