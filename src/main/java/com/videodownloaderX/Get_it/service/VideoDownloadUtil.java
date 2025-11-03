package com.videodownloaderX.Get_it.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class VideoDownloadUtil {

    @Value("${videodownloader.download.output-dir}")
    private String outputDir;

    public void download(String url) {
        try {
            System.out.println("📥 Starting download: " + url);

            String[] command = {
                    "yt-dlp",
                    "--format", "best",
                    "--output", outputDir + "/%(title)s.%(ext)s",
                    "--quiet",
                    "--progress",
                    url
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);
            Process process = pb.start();

            // Read yt-dlp output
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
