package com.cinerolodex.model;

public class Anno {
    private int valore;

    public Anno(int valore) {
        this.valore = valore;
    }

    public int getValore() {
        return valore;
    }

    @Override
    public String toString() {
        return String.valueOf(valore);
    }
}
