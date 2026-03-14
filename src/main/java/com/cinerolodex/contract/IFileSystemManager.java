package com.cinerolodex.contract;

import java.nio.file.Path;

import com.cinerolodex.model.RawElement;

public interface IFileSystemManager {
    public RawElement getRawData(Path path);
}
