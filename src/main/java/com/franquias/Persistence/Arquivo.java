package com.franquias.Persistence;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Arquivo {

    public static final String DATA_DIRECTORY = "data";

    public static String le(String filePath) {
        Path path = Path.of(filePath);
        if (!Files.exists(path)) {
            return "";
        }

        try {
            return Files.readString(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler o arquivo: " + filePath, e);
        }
    }

    public static void salva(String filePath, String content) {
        Path path = Path.of(filePath);
        try {
            Path parent = path.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            Files.writeString(path, content, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao salvar o arquivo: " + filePath, e);
        }
    }
}
