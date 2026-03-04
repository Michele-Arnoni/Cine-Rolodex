package com.cinerolodex.contract;
import java.util.List;

import com.cinerolodex.model.Anno;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.Rating;
import com.cinerolodex.model.StatoVisione;

public interface IFilterEngine {
    public List<IFilm> search(String titoloFilm);
    public List<IFilm> filter(Genere genere, Anno anno, Regista regista, StatoVisione statoVisione, Rating rating);
}
