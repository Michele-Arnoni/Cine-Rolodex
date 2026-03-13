package com.cinerolodex.model.factory;

import java.nio.file.Path;

import com.cinerolodex.contract.IFilmFactory;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.model.Anno;
import com.cinerolodex.model.Film;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Rating;
import com.cinerolodex.model.RawElement;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.StatoVisione;

public class FilmFactory implements IFilmFactory {
    private static FilmFactory instance;

    private FilmFactory() {
        // Costruttore privato per garantire il Singleton
    }

    public static synchronized FilmFactory getInstance() {
        if (instance == null) {
            instance = new FilmFactory();
        }
        return instance;
    }

    // --- RICETTA 1: Creazione da dati grezzi dal file system ---
    @Override
    public IFilm createFromRaw(RawElement raw) {
        //Pulizia del titolo dal nome del file
        String titoloPulito = cleanTitle(raw.title());
        
        //Recupero del percorso del file
        Path percorso = raw.filePath();
        
        // Impostazione dei valori di default per un NUOVO inserimento
        // Un film appena trovato sul disco non ha ancora un ID (lo darà il DB),
        // né una valutazione o uno stato di visione specifico.
        int idProvvisorio = 0;
        Rating ratingDefault = Rating.NON_VALUTATO;
        StatoVisione statoDefault = StatoVisione.NON_VISTO;
        
        // Creazione dei metadati satellite
        // Questi verranno poi eventualmente modificati dall'utente nella UI
        Regista registaSconosciuto = new Regista("Sconosciuto");
        Genere genereSconosciuto = new Genere("Sconosciuto");
        Anno annoDefault = new Anno(0); // O l'anno corrente, a tua scelta

        // Restituzione dell'istanza concreta di Film (impacchettata come IFilm)
        return new Film(
            idProvvisorio,
            titoloPulito,
            percorso,
            ratingDefault,
            statoDefault,
            registaSconosciuto,
            genereSconosciuto,
            annoDefault
        );
    }

    // --- RICETTA 2: Creazione per aggiornamento (Copia e Modifica) ---
    @Override
    public IFilm createWithNewTitle(IFilm original, String nuovoTitolo) {
        return new Film(
            original.getId(),
            nuovoTitolo,
            original.getPath(),
            original.getRating(),
            original.getStato(),
            original.getRegista(),
            original.getGenere(),
            original.getAnno()
        );
    }

    // --- RICETTA 3: Creazione per aggiornamento del regista (Copia e Modifica) ---
    @Override
    public IFilm createWithNewRegista(IFilm original, String nuovoRegista) {
        return new Film(
            original.getId(), original.getTitolo(), original.getPath(),
            original.getRating(), original.getStato(),
            new Regista(nuovoRegista), // Creiamo il nuovo oggetto Regista
            original.getGenere(), original.getAnno()
        );
    }

    // --- RICETTA 4: Creazione per aggiornamento dell'anno (Copia e Modifica) ---
    @Override
    public IFilm createWithNewYear(IFilm original, int nuovoAnno) {
        return new Film(original.getId(), original.getTitolo(), original.getPath(),
                        original.getRating(), original.getStato(),
                        original.getRegista(), original.getGenere(), new Anno(nuovoAnno));
    }

    // --- RICETTA 5: Creazione per aggiornamento del genere (Copia e Modifica) ---
    @Override
    public IFilm createWithNewGenere(IFilm original, String nuovoGenere) {
        return new Film(
            original.getId(), original.getTitolo(), original.getPath(),
            original.getRating(), original.getStato(),
            original.getRegista(), new Genere(nuovoGenere), original.getAnno()
        );
    }

    // --- RICETTA 6: Creazione per aggiornamento della valutazione (Copia e Modifica) ---
    @Override
    public IFilm createWithNewRating(IFilm original, Rating nuovoRating) {
        return new Film(original.getId(), original.getTitolo(), original.getPath(),
                        nuovoRating, original.getStato(),
                        original.getRegista(), original.getGenere(), original.getAnno());
    }

    /**
     * Metodo helper per trasformare "NomeFile.mp4" in "NomeFile", si pulisce l'estentsione
     */
    private String cleanTitle(String rawTitle) {
        if (rawTitle == null || rawTitle.isEmpty()) return "Titolo Sconosciuto";

        // Rimozione dell'estensione (es. .mp4, .mkv, .avi)
        int lastDotIndex = rawTitle.lastIndexOf('.');
        String nameWithoutExtension = (lastDotIndex == -1) ? rawTitle : rawTitle.substring(0, lastDotIndex);

        // Sostituzione caratteri comuni nei nomi file con spazi
        String clean = nameWithoutExtension.replace('_', ' ').replace('.', ' ');

        // Rimozione eventuali spazi doppi o all'inizio/fine
        return clean.replaceAll("\\s+", " ").trim();
    }
}