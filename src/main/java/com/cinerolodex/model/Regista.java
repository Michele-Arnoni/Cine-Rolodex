package com.cinerolodex.model;

public class Regista {
    private String nome;

    public Regista(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    @Override
    public String toString() {
        return nome;
    }
}
