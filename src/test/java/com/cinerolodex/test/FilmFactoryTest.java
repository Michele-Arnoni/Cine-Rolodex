package com.cinerolodex.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.nio.file.Path;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.model.RawElement;
import com.cinerolodex.model.factory.FilmFactory;


public class FilmFactoryTest {

    /**
     * Test per verificare la creazione di un film a partire da un path
     * @see FilmFactory#createFromRaw(RawElement)
    */
    @Test
    public void testCreateFromRawPath() {
        Path path = Path.of("Inception_2010_Bluray.mp4"); //Path di esempio
        IFilm film = FilmFactory.getInstance().createFromRaw(new RawElement("Inception_2010_Bluray.mp4", path)); // Creazione del film a partire dal path

        assertEquals("Inception 2010 Bluray", film.getTitolo()); // Verifica che il titolo sia stato estratto correttamente dal pathe
    }
}