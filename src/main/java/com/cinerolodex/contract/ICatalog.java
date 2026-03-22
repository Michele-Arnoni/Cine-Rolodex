package com.cinerolodex.contract;
import java.nio.file.Path;
import java.util.List;

public interface ICatalog {
    public List<IFilm> showCollection();
    public void addEntry(Path path);    //aggiunge un film al catalogo dato il path del file
    public void removeEntry(IFilm film); //rimuove un film dal catalogo dato l'oggetto Film
    public void updateEntry(IFilm oldFilm, IFilm updatedFilmm); //aggiorna le informazioni di un film esistente nel catalogo dato l'oggetto Film con le nuove informazioni
}
