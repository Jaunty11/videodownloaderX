package com.videodownloaderX;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

@ApplicationScoped
public class DownloadService {

    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @ConfigProperty(name = "videodownloader.rate-limit.delay-ms", defaultValue = "500")
    long delayMs;

    @Inject
    VideoDownloadUtil downloadUtil;

    @Inject
    OAuthDeviceFlowService oauthService;

    public void processDownloadRequests(String[] args) throws Exception {
        String username = oauthService.getAuthenticatedUsername();
        System.out.println("✅ Authenticated as: " + username + "\n");

        List<String> urls = parseUrls(args);

        if (urls.isEmpty()) {
            System.out.println("❌ No URLs provided");
            System.out.println("Usage: videodownloaderx download <url1> [url2] ... [url5]");
            return;
        }

        if (urls.size() > 5) {
            System.out.println("⚠️  Maximum 5 URLs allowed. Processing first 5...\n");
            urls = urls.subList(0, 5);
        }

        executeDownloads(urls, username);
    }

    private void executeDownloads(List<String> urls, String username) {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 Processing " + urls.size() + " download(s)");
        System.out.println("👤 User: " + username);
        System.out.println("🧵 Max concurrent threads: 2");
        System.out.println("⏱️  Rate limit: " + delayMs + "ms delay");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        List<Future<?>> futures = new ArrayList<>();

        for (String url : urls) {
            Future<?> future = executor.submit(() -> {
                try {
                    String threadName = Thread.currentThread().getName();
                    System.out.println("[" + threadName + "] Starting: " + url);
                    downloadUtil.download(url);
                    System.out.println("[" + threadName + "] Finished: " + url + "\n");
                    Thread.sleep(delayMs);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("❌ Download interrupted: " + url);
                }
            });

            futures.add(future);
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                System.err.println("❌ Download task failed: " + e.getMessage());
            }
        }

        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✅ All downloads complete!");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }

    private List<String> parseUrls(String[] args) {
        List<String> urls = new ArrayList<>();
        for (String arg : args) {
            if (arg != null && !arg.startsWith("--")) {
                urls.add(arg);
            }
        }
        return urls;
    }
}
