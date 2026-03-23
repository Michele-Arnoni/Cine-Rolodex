package com.cinerolodex.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.model.RawElement;
import com.cinerolodex.model.factory.FilmFactory;

class FilmFactoryTest {

    /** 
     * Test per verificare la creazione di un film a partire da un path 
     * @see FilmFactory#createFromRaw(RawElement)
    */
    @Test
    void testCreateFromRawPath() {
        Path path = Path.of("Inception_2010_Bluray.mp4");
        IFilm film = FilmFactory.getInstance().createFromRaw(new RawElement("Inception_2010_Bluray.mp4", path));

        assertEquals("Inception 2010 Bluray", film.getTitolo());
    }
}