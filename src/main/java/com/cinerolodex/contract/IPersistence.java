package com.cinerolodex.contract;

import java.util.List;

public interface IPersistence {
    public void save(IFilm f);
    public void delete(IFilm f);
    public void update(IFilm f);
    public List<IFilm> loadAll();
}
