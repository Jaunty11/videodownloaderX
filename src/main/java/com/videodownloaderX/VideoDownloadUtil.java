package com.videodownloaderX;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@ApplicationScoped
public class VideoDownloadUtil {

    @ConfigProperty(name = "videodownloader.download.output-dir", defaultValue = "${user.home}/Downloads/VideoDownloader")
    String outputDir;

    public void download(String url) {
        try {
            System.out.println("📥 Starting download: " + url);

            String[] command = {
                    "yt-dlp",
                    "--format", "best",
                    "--output", outputDir + "/%(title)s.%(ext)s",
                    "--quiet",
                    url
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("  " + line);
                }
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("✅ Download complete: " + url);
            } else {
                System.err.println("❌ Download failed: " + url + " (exit code: " + exitCode + ")");
            }

        } catch (Exception e) {
            System.err.println("❌ Error downloading " + url + ": " + e.getMessage());
        }
    }
}
