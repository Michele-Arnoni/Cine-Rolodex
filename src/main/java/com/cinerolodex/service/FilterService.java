package com.cinerolodex.service;
import java.util.List;
import java.util.stream.Collectors;

import com.cinerolodex.contract.IFilterEngine;
import com.cinerolodex.contract.IFilm;
import com.cinerolodex.model.Anno;
import com.cinerolodex.model.Genere;
import com.cinerolodex.model.Regista;
import com.cinerolodex.model.Rating;
import com.cinerolodex.model.StatoVisione;
import com.cinerolodex.manager.CatalogManager;

public class FilterService implements IFilterEngine {
    private static FilterService instance;
    
    private FilterService() {
        // Costruttore privato per garantire il Singleton
    }

    public static synchronized FilterService getInstance() {
        if (instance == null) {
            instance = new FilterService();
        }
        return instance;
    }

    @Override
    public List<IFilm> search(String titoloFilm) {
        List<IFilm> allMovies = CatalogManager.getInstance().showCollection(); //Recupero della lista completa dei film dal CatalogManager
        
        if (titoloFilm == null || titoloFilm.isEmpty()) {
            return allMovies;
        }

        // Filtro per sottostringa (case-insensitive)
        return allMovies.stream()
                .filter(f -> f.getTitolo().toLowerCase().contains(titoloFilm.toLowerCase()))
                .collect(Collectors.toList());
    }


    @Override
    public List<IFilm> filter(Genere genere, Anno anno, Regista regista, StatoVisione statoVisione, Rating rating) {
        List<IFilm> allMovies = CatalogManager.getInstance().showCollection(); //Recupero della lista completa dei film dal CatalogManager

        return allMovies.stream()
            .filter(f -> (genere == null || f.getGenere().equals(genere)))
            .filter(f -> (regista == null || f.getRegista().equals(regista)))
            .filter(f -> (anno == null || f.getAnno().equals(anno)))
            .filter(f -> (statoVisione == null || f.getStato().equals(statoVisione))) 
            .filter(f -> (rating == null || f.getRating().equals(rating)))
            .collect(Collectors.toList());
    }
}
