package com.videodownloaderX.Get_it.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
public class DownloadService {

    @Autowired
    @Qualifier("downloadExecutor")
    private ExecutorService executor;

    @Autowired
    private RateLimiterService rateLimiter;

    @Autowired
    private VideoDownloadUtil downloadUtil;

    @Autowired
    private OAuthDeviceFlowService oauthService;

    public void processDownloadRequests(String... args) throws Exception {
        // Get authenticated username
        String username = oauthService.getAuthenticatedUsername();
        System.out.println("✅ Authenticated as: " + username + "\n");

        // Parse URLs from arguments
        List<String> urls = parseUrls(args);

        if (urls.isEmpty()) {
            System.out.println("❌ No URLs provided");
            System.out.println("Usage: videodownloader download <url1> [url2] ... [url5]");
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
        System.out.println("⏱️  Rate limit: 500ms delay between operations");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        List<Future<?>> futures = new ArrayList<>();

        for (String url : urls) {
            Future<?> future = executor.submit(() -> {
                try {
                    rateLimiter.executeWithRateLimit(() -> {
                        String threadName = Thread.currentThread().getName();
                        System.out.println("[" + threadName + "] [" + username + "] Starting: " + url);
                        downloadUtil.download(url);
                        System.out.println("[" + threadName + "] [" + username + "] Finished: " + url + "\n");
                    });
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.err.println("❌ Download interrupted: " + url);
                }
            });

            futures.add(future);
        }

        // Wait for all downloads to complete
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

        // Skip first argument (command: "download")
        for (int i = 1; i < args.length; i++) {
            if (!args[i].startsWith("--")) {
                urls.add(args[i]);
            }
        }

        return urls;
    }
}
