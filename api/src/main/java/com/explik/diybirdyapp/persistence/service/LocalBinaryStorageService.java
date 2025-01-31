package com.explik.diybirdyapp.persistence.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LocalBinaryStorageService implements BinaryStorageService {
    private final Path storagePath;

    public LocalBinaryStorageService(@Value("${binary-file-storage-path}") String storagePath) {
        this.storagePath = Paths.get(storagePath);

        try {
            Files.createDirectories(this.storagePath);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to create storage directory", e);
        }
    }

    @Override
    public byte[] get(String key) {
        try {
            Path filePath = storagePath.resolve(key);
            return Files.readAllBytes(filePath);
        }
        catch (IOException e) {
            return null;
        }
    }

    @Override
    public void set(String key, byte[] value) {
        try {
            Path filePath = storagePath.resolve(key);
            Files.write(filePath, value);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to write file", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            Path filePath = storagePath.resolve(key);
            Files.delete(filePath);
        }
        catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }

}
