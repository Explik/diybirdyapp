package com.explik.diybirdyapp.service.imports;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ImportSupportUnitTest {
    @Test
    void givenPayloadWithMissingRequiredField_whenRequireString_thenThrows() {
        var payload = Map.<String, Object>of("other", "value");

        var exception = assertThrows(IllegalArgumentException.class,
                () -> ImportSupport.requireString(payload, "required"));

        assertEquals("Missing required payload field: required", exception.getMessage());
    }

    @Test
    void givenPayloadWithIntegerString_whenReadInteger_thenParses() {
        var payload = Map.<String, Object>of("value", "42");

        var value = ImportSupport.readInteger(payload, "value");

        assertEquals(42, value);
    }

    @Test
    void givenPayloadWithInvalidInteger_whenReadInteger_thenThrows() {
        var payload = Map.<String, Object>of("value", "not-a-number");

        var exception = assertThrows(IllegalArgumentException.class,
                () -> ImportSupport.readInteger(payload, "value"));

        assertEquals("Expected integer payload field: value", exception.getMessage());
    }

    @Test
    void givenNullPayload_whenReadString_thenReturnsNull() {
        assertNull(ImportSupport.readString(null, "value"));
    }

    @Test
    void givenPathWithBackslashesAndLeadingSlash_whenNormalizeFileRef_thenNormalizes() {
        var normalized = ImportSupport.normalizeFileRef("/media\\audio\\file.mp3");

        assertEquals("media/audio/file.mp3", normalized);
    }

    @Test
    void givenPathWithTraversal_whenNormalizeFileRef_thenThrows() {
        var exception = assertThrows(IllegalArgumentException.class,
                () -> ImportSupport.normalizeFileRef("../secret/file"));

        assertEquals("Invalid fileRef path: ../secret/file", exception.getMessage());
    }

    @Test
    void givenFileRef_whenExtractFileName_thenReturnsLeafName() {
        assertEquals("file.mp3", ImportSupport.extractFileName("media/path/file.mp3"));
        assertEquals("file.mp3", ImportSupport.extractFileName("file.mp3"));
    }

    @Test
    void givenFileRef_whenToChunkSafeName_thenReplacesSlashes() {
        assertEquals("media_sub_file.mp3", ImportSupport.toChunkSafeName("media/sub/file.mp3"));
    }

    @Test
    void givenBytes_whenSha256WithPrefix_thenReturnsExpectedHash() {
        var hash = ImportSupport.sha256WithPrefix("abc".getBytes());

        assertEquals("sha256:ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", hash);
    }

    @Test
    void givenString_whenMd5Hex_thenReturnsExpectedHash() {
        var hash = ImportSupport.md5Hex("abc");

        assertEquals("900150983cd24fb0d6963f7d28e17f72", hash);
    }
}
