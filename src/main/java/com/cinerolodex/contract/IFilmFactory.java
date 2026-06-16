package com.cinerolodex.contract;

import com.cinerolodex.model.Rating;
import com.cinerolodex.model.RawElement;
import com.cinerolodex.model.StatoVisione;

public interface IFilmFactory {
    public IFilm createFromRaw(RawElement raw);
    public IFilm createWithNewTitle(IFilm original, String nuovoTitolo);
    public IFilm createWithNewRegista(IFilm original, String nuovoRegista);
    public IFilm createWithNewGenere(IFilm original, String nuovoGenere);
    public IFilm createWithNewYear(IFilm original, int nuovoAnno);
    public IFilm createWithNewRating(IFilm original, Rating nuovoRating);
    public IFilm createWithNewStato(IFilm original, StatoVisione nuovoStato);
}
