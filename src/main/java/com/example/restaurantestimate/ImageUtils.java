package com.example.restaurantestimate;

import com.azure.identity.DefaultAzureCredentialBuilder;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityNotFoundException;
import java.io.*;
import java.net.URL;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@Slf4j
public class ImageUtils {
    private final String imageDirectory;

    public ImageUtils(@Value("${image-directory}") String imageDirectory) {
        this.imageDirectory = imageDirectory;
    }

    public File getImage(String imageName) {
        List<File> files = new ArrayList<>();
        try (Stream<Path> entries = Files.walk(Path.of(imageDirectory))) {
            files = entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        Optional<File> file = files.stream()
                .filter(e -> e.getName().equals(imageName))
                .findFirst();

        return file.orElseThrow(() -> new EntityNotFoundException("Аватар не найден."));
    }
    public void deletePreviousUserImage(String prefix) throws IOException {
        List<File> files = new ArrayList<>();
        try (Stream<Path> entries = Files.walk(Path.of(imageDirectory))) {
            files = entries
                    .filter(Files::isRegularFile)
                    .map(Path::toFile)
                    .toList();
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        Optional<File> file = files.stream()
                .filter(e -> {
                    String filename = e.getName().substring(0, e.getName().lastIndexOf("."));
                    String avatarPrefix = prefix + "_avatar";
                    return filename.equals(avatarPrefix);
                })
                .findFirst();

        file.ifPresent(File::delete);
    }

    public String buildFile(String contentType, String username) {
        String type = contentType.substring(6);
        return imageDirectory
                + "/"
                + username
                + "_avatar"
                + "."
                + type;
    }

    public static boolean isSupportedContentType(String contentType) {
        return contentType.equals("image/png")
                || contentType.equals("image/jpg")
                || contentType.equals("image/gif")
                || contentType.equals("image/jpeg");
    }
}