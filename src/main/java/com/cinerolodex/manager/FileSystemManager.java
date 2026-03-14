package com.cinerolodex.manager;
import com.cinerolodex.contract.IFileSystemManager;
import com.cinerolodex.model.RawElement;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileSystemManager implements IFileSystemManager {
    private static FileSystemManager instance;


    private FileSystemManager() {
    }

    public static synchronized FileSystemManager getInstance() {
        if (instance == null) {
            instance = new FileSystemManager();
        }
        return instance;
    }

    /*
     * Estrae i dati grezzi da un singolo file.
     * Utilizzato quando l'utente seleziona un file specifico dalla UI.
     */
    @Override
    public RawElement getRawData(Path path) {
        String fileName = path.getFileName().toString();
        String rawTitle = fileName.contains(".")
                        ? fileName.substring(0, fileName.lastIndexOf('.'))
                        : fileName;

        // Creazione del record
        return new RawElement(rawTitle, path);
    }
}
