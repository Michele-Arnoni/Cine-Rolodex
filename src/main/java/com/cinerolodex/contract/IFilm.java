package com.cinerolodex.contract;

import java.nio.file.Path;

import com.cinerolodex.model.Rating;
import com.cinerolodex.model.StatoVisione;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Anno;

public interface IFilm {
    int getId();
    String getTitolo();
    Path getPath();
    Rating getRating();
    StatoVisione getStato();
    Regista getRegista();
    Genere getGenere();
    Anno getAnno();
}
