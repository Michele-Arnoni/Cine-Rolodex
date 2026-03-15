package com.cinerolodex.contract;
import java.util.List;

import com.cinerolodex.model.Anno;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.Rating;
import com.cinerolodex.model.StatoVisione;

public interface IFilterEngine {
    // Metodo di filtro intrecciato con la ricerca
    public List<IFilm> filter(String titolo, String genere, String anno, String regista, String stato, String rating);
}
