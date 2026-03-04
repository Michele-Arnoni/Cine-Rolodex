package com.cinerolodex.contract;

import com.cinerolodex.model.RawElement;

public interface IFilmFactory {
    IFilm createFromRaw(RawElement raw);
}
