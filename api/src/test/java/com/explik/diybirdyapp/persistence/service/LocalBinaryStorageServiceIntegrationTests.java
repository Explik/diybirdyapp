package com.explik.diybirdyapp.persistence.service;

import com.explik.diybirdyapp.service.storageService.LocalBinaryStorageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class LocalBinaryStorageServiceIntegrationTests {
    @Autowired
    LocalBinaryStorageService storageService;

    @Test
    public void givenNonExistentFile_whenGet_returnNull() {
        // Check that the file does not exist
        var fileContents = storageService.get("non-existent-file.txt");
        assertNull(fileContents);
    }

    @Test
    public void givenNewlyCreatedFile_whenGet_returnFileContents() {
        // Create a new file
        var fileName = "new-file.txt";
        var fileContents = "Hello, this is a test.";
        storageService.set(fileName, fileContents.getBytes());

        // Check that the file exists and has the correct contents
        var retrievedContents = storageService.get(fileName);
        assertArrayEquals(fileContents.getBytes(), retrievedContents);
    }

    @Test
    public void givenNewlyDeletedFile_whenGet_returnNull() {
        // Create a new file
        var fileName = "new-file.txt";
        var fileContents = "Hello, this is a test.";
        storageService.set(fileName, fileContents.getBytes());

        // Delete the file
        storageService.delete(fileName);

        // Check that the file does not exist
        var retrievedContents = storageService.get(fileName);
        assertNull(retrievedContents);
    }
}
