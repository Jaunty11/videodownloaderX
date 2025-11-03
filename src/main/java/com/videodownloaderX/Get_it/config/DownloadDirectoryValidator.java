package com.videodownloaderX.Get_it.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class DownloadDirectoryValidator {

    @Value("${videodownloader.download.output-dir}")
    private String outputDir;

    @PostConstruct
    public void validateDownloadDirectory() {
        try {
            Path downloadPath = Path.of(outputDir);

            // Create directory if it doesn't exist
            if (!Files.exists(downloadPath)) {
                System.out.println("📁 Creating download directory: " + downloadPath);
                Files.createDirectories(downloadPath);
                System.out.println("✅ Directory created successfully");
            } else {
                System.out.println("✅ Download directory exists: " + downloadPath);
            }

            // Validate write permissions
            if (!Files.isWritable(downloadPath)) {
                throw new IOException("Download directory is not writable: " + downloadPath);
            }

            // Test write by creating a temp file
            File testFile = new File(downloadPath.toFile(), ".write-test");
            if (testFile.createNewFile()) {
                testFile.delete();
                System.out.println("✅ Directory is writable");
            }

        } catch (IOException e) {
            System.err.println("❌ Failed to initialize download directory: " + e.getMessage());
            System.err.println("Please check permissions or specify a different directory in application.yml");
            System.exit(1);
        }
    }
}
