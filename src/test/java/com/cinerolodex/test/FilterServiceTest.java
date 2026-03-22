package com.cinerolodex.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.service.FilterService;
import com.cinerolodex.model.Anno;
import com.cinerolodex.model.Film;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.Rating;
import com.cinerolodex.model.StatoVisione;
import com.cinerolodex.manager.CatalogManager;
import java.nio.file.Path;

class FilterServiceTest {
    private FilterService filterService;
    private List<IFilm> testList;

    @BeforeEach
    void setUp() {
        filterService = FilterService.getInstance();
        CatalogManager catalog = CatalogManager.getInstance();
        
        // Puliamo il catalogo da eventuali film caricati dal database reale
        catalog.clearForTesting();

        // Creiamo i film di test con tutti i parametri richiesti dal tuo costruttore
        IFilm f1 = new Film(
            1, "Inception", java.nio.file.Path.of("test1.mp4"),
            Rating.MI_PIACE, StatoVisione.VISTO,
            new Regista("Christopher Nolan"), new Genere("Azione"), new Anno(2010)
        );

        IFilm f2 = new Film(
            2, "Matrix", java.nio.file.Path.of("test2.mp4"),
            Rating.MI_PIACE, StatoVisione.VISTO,
            new Regista("Wachowski"), new Genere("Azione"), new Anno(1999)
        );

        // Aggiungiamo i film al catalogo tramite il nuovo metodo
        catalog.addFilmForTesting(f1);
        catalog.addFilmForTesting(f2);
    }

    @Test
    void testFilterByGenre() {
        // Cerchiamo "Azione": ora ci aspettiamo esattamente 2 film
        List<IFilm> result = filterService.filter(null, "Azione", null, null, null, null); 
        assertEquals(2, result.size(), "Dovrebbe trovare 2 film di genere Azione");
    }
}