package com.cinerolodex.model;
import java.nio.file.Path;

/*
 * RawElement rappresenta i dati grezzi estratti dal file system.
 * Essendo un record, è immutabile e genera automaticamente getter, equals, hashCode e toString.
 */

public record RawElement(String title, Path filePath) {
}
