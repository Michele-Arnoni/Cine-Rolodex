package com.cinerolodex.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.service.FilterService;
import com.cinerolodex.model.Film;
import com.cinerolodex.model.factory.FilmFactory;
import com.cinerolodex.model.RawElement;

class FilterServiceTest {
    private FilterService filterService;
    private List<IFilm> testList;

    @BeforeEach
    void setUp() {
        filterService = FilterService.getInstance();
        
        // Creazione di una lista fittizia di test
        testList = List.of(
            FilmFactory.getInstance().createFromRaw(new RawElement("Inception_2010_Bluray.mp4", null)),
            FilmFactory.getInstance().createFromRaw(new RawElement("The_Dark_Knight_2008_BluRay.mp4", null)),
            FilmFactory.getInstance().createFromRaw(new RawElement("Interstellar_2014_BluRay.mp4", null))
        );
    }

    @Test
    void testFilterByGenre() {
        // Rimuovi 'testList' dagli argomenti
        List<IFilm> result = filterService.filter(null, "Azione", null, null, null, null); 
        assertEquals(2, result.size());
    }
}