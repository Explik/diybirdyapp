package com.explik.diybirdyapp.service.imports;

import org.springframework.util.DigestUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public final class ImportSupport {
    private ImportSupport() {
    }

    public static String requireString(Map<String, Object> payload, String key) {
        var value = readString(payload, key);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Missing required payload field: " + key);
        }

        return value;
    }

    public static String readString(Map<String, Object> payload, String key) {
        if (payload == null) {
            return null;
        }

        var value = payload.get(key);
        if (value == null) {
            return null;
        }

        return String.valueOf(value);
    }

    public static Integer readInteger(Map<String, Object> payload, String key) {
        if (payload == null) {
            return null;
        }

        var value = payload.get(key);
        if (value == null) {
            return null;
        }

        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Expected integer payload field: " + key);
        }
    }

    public static String normalizeFileRef(String fileRef) {
        if (fileRef == null) {
            return null;
        }

        var normalized = fileRef.replace('\\', '/');
        while (normalized.startsWith("/")) {
            normalized = normalized.substring(1);
        }

        if (normalized.contains("..")) {
            throw new IllegalArgumentException("Invalid fileRef path: " + fileRef);
        }

        return normalized;
    }

    public static String extractFileName(String fileRef) {
        var normalized = normalizeFileRef(fileRef);
        var slashIndex = normalized.lastIndexOf('/');
        return slashIndex >= 0 ? normalized.substring(slashIndex + 1) : normalized;
    }

    public static String toChunkSafeName(String fileRef) {
        return normalizeFileRef(fileRef).replace('/', '_');
    }

    public static String sha256WithPrefix(byte[] bytes) {
        try {
            var digest = MessageDigest.getInstance("SHA-256");
            var hash = digest.digest(bytes);
            var hex = new StringBuilder(hash.length * 2);
            for (byte value : hash) {
                hex.append(String.format("%02x", value));
            }

            return "sha256:" + hex;
        } catch (NoSuchAlgorithmException ex) {
            throw new IllegalStateException("SHA-256 algorithm not available", ex);
        }
    }

    public static String md5Hex(String value) {
        return DigestUtils.md5DigestAsHex(value.getBytes(StandardCharsets.UTF_8));
    }
}
