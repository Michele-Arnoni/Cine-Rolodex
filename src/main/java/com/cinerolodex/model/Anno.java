package com.cinerolodex.model;

public class Anno {
    private String nome;

    public Anno(int valore) {
        this.nome = String.valueOf(valore);
    }

    public Anno(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }
}
