package com.cinerolodex.test;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.List;
import java.nio.file.Path;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.model.RawElement;
import com.cinerolodex.model.factory.FilmFactory;

class FilmFactoryTest {

    @Test
    void testCreateFromRawPath() {
        Path path = Path.of("C:/Movies/Inception_2010_Bluray.mp4");
        RawElement raw = new RawElement("Inception_2010_Bluray.mp4", path);

        IFilm film = FilmFactory.getInstance().createFromRaw(raw);

        // Verifichiamo che il titolo sia stato pulito correttamente
        assertEquals("Inception", film.getTitolo());
        assertEquals(2010, film.getAnno().getValore());
        assertNotNull(film.getGenere()); // Dovrebbe avere un genere di default
    }
}