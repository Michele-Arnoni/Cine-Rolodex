package com.cinerolodex.model;
import java.nio.file.Path;

import com.cinerolodex.contract.IFilm;

public class Film implements IFilm {
    private int id;
    private String titolo;
    private Path path;
    private Rating rating;
    private StatoVisione stato;
    private Regista regista;
    private Genere genere;
    private Anno anno;

    public Film(int id, String titolo, Path path, Rating rating, StatoVisione stato, Regista regista, Genere genere, Anno anno) {
        this.id = id;
        this.titolo = titolo;
        this.path = path;
        this.rating = rating;
        this.stato = stato;
        this.regista = regista;
        this.genere = genere;
        this.anno = anno;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitolo() {
        return titolo;
    }
    
    @Override
    public Path getPath() {
        return path;
    }
    
    @Override
    public Rating getRating() {
        return rating;
    }

    @Override
    public StatoVisione getStato() {
        return stato;
    }
    
    @Override
    public Regista getRegista() {
        return regista;
    }
    
    @Override
    public Genere getGenere() {
        return genere;
    }
    
    @Override
    public Anno getAnno() {
        return anno;
    }
}
