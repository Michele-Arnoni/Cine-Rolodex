package com.cinerolodex.contract;

import java.util.List;

public interface IPersistence {
    public boolean save(IFilm f);
    public boolean delete(IFilm f);
    public boolean update(IFilm f);
    public List<IFilm> loadAll();
}
