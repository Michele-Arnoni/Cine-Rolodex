package com.cinerolodex.contract;
import java.util.List;

public interface IFilterEngine {
    // Metodo di filtro intrecciato con la ricerca
    public List<IFilm> filter(String titolo, String genere, String anno, String regista, String stato, String rating);
}
