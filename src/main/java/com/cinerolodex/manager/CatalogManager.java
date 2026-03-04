package com.cinerolodex.manager;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.nio.file.Path;
import javafx.collections.FXCollections;
import javafx.scene.chart.PieChart.Data;

import com.cinerolodex.contract.IFileSystemManager;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.contract.IFilmFactory;
import com.cinerolodex.model.RawElement;
import com.cinerolodex.contract.IPersistence;
import com.cinerolodex.manager.DatabaseManager;
import com.cinerolodex.manager.FileSystemManager;
import com.cinerolodex.model.factory.FilmFactory;


public class CatalogManager implements com.cinerolodex.contract.ICatalog {
    private static CatalogManager instance = null;
    private final List<IFilm> movies; // ObservableList dei film presenti nel catalogo, utilizzata per aggiornare la UI in tempo reale
    private final IPersistence dataManager; // Riferimento al DataManager per le richieste al DATABASE
    private final IFileSystemManager fileSystemManager; // Riferimento al FileSystemManager per le richieste al FILE SYSTEM
    private final IFilmFactory filmFactory; // Riferimento alla FilmFactory per la creazione degli oggetti Film a partire dai dati grezzi
    

    private CatalogManager() {
        // Inizializzazione della ObservableList
        this.movies = FXCollections.observableArrayList();
        
        // Ottenimento dell'istanza del DatabaseManager (Singleton)
        this.dataManager = DatabaseManager.getInstance();

        // Ottenimento dell'istanza del FileSystemManager (Singleton)
        this.fileSystemManager = FileSystemManager.getInstance();

        // Ottenimento dell'istanza della FilmFactory (Singleton)
        this.filmFactory = FilmFactory.getInstance();

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
    public void addEntry(Path path) {
        // Controllo preventivo: il film è già in lista?
        // Uso dello stream per verificare se esiste già un film con lo stesso percorso file
        boolean alreadyExists = movies.stream()
                .anyMatch(f -> f.getPath().equals(path));

        if (alreadyExists) {
            System.out.println("Avviso: Il film situato in " + path + " è già presente nel catalogo.");
            return;
        }

        try {
            // Estrazione dati grezzi dal File System tramite il FileSystemManager
            RawElement raw = fileSystemManager.getRawData(path);

            // Creazione dell'oggetto di dominio tramite la Factory
            // Qui il titolo viene pulito e normalizzato, e vengono estratti gli altri metadati (anno, regista, ecc.) se disponibili
            IFilm newFilm = filmFactory.createFromRaw(raw);

            // Persistenza: salvataggio nel database SQLite
            // Se il salvataggio fallisce (ritorna false o solleva un'eccezione), il film non viene aggiunto alla lista e quindi non appare nella UI
            if (dataManager.save(newFilm)) {
                
                // Aggiornamento UI: aggiunta alla ObservableList
                // Grazie a JavaFX, il film apparirà istantaneamente nella UI senza bisogno di notifiche esplicite
                movies.add(newFilm);
                
                System.out.println("Successo: '" + newFilm.getTitolo() + "' aggiunto correttamente.");
            } else {
                System.err.println("Errore: Impossibile salvare il film nel database.");
            }

        } catch (Exception e) {
            // Gestione di eventuali errori imprevisti (es. file illeggibile o corrotto)
            System.err.println("Errore critico durante l'aggiunta del file: " + e.getMessage());
        }
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
}
