package com.cinerolodex.manager;

import com.cinerolodex.contract.IMediaPlayerManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.awt.Desktop;

public class MediaPlayerManager  implements IMediaPlayerManager {
    private static MediaPlayerManager mediaPlayerManager;

    private MediaPlayerManager() {
    }

    public static synchronized MediaPlayerManager getInstance() {
        if (mediaPlayerManager == null) {
            mediaPlayerManager = new MediaPlayerManager();
        }
        return mediaPlayerManager;
    }

    @Override
    public void play(Path filmPath) {
        if (filmPath == null) {
            System.err.println("Errore: Il percorso del file è nullo.");
            return;
        }

        File videoFile = filmPath.toFile();

        // Verifica che il file esista effettivamente sul disco prima di aprirlo
        if (!videoFile.exists()) {
            System.err.println("Errore: Il file video non è stato trovato in: " + filmPath);
            return;
        }

        try {
            // Utilizzo del Desktop del sistema per aprire il file con l'app predefinita
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(videoFile);
                System.out.println("Riproduzione avviata per: " + videoFile.getName());
            } else {
                System.err.println("Errore: Apertura file non supportata su questo sistema.");
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'avvio del player: " + e.getMessage());
        }
    }
    
}
