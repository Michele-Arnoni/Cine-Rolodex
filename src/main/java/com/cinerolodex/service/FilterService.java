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
    private final CatalogManager catalog = CatalogManager.getInstance(); //riferimento al CatalogManager per accedere alla collezione di film
    
    private FilterService() {
        // Costruttore privato per garantire il Singleton
    }

    public static synchronized FilterService getInstance() {
        if (instance == null) {
            instance = new FilterService();
        }
        return instance;
    }
    
    // Implementazione del metodo di filtraggio, che utilizza lo stream API di Java per applicare i filtri in modo efficiente e leggibile.
    @Override
    public List<IFilm> filter(String titolo, String genere, String anno, String regista, String stato, String rating) {
        return catalog.showCollection().stream()
            .filter(f -> titolo == null || titolo.isBlank() || f.getTitolo().toLowerCase().contains(titolo.toLowerCase()))
            .filter(f -> genere == null || f.getGenere().getNome().equals(genere))
            .filter(f -> regista == null || f.getRegista().getNome().equals(regista))
            .filter(f -> anno == null || String.valueOf(f.getAnno().getValore()).equals(anno))
            .filter(f -> stato == null || f.getStato().name().equals(stato))
            .filter(f -> rating == null || f.getRating().name().equals(rating))
            .toList();
    }
}
