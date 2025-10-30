package com.videodownloaderX;

import picocli.CommandLine;
import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import jakarta.inject.Inject;

@QuarkusMain
@CommandLine.Command(name = "videodownloaderx", description = "Download videos from any platform")
public class VideoDownloaderApp implements QuarkusApplication, Runnable {

    @Inject
    OAuthDeviceFlowService oauthService;

    @Inject
    DownloadService downloadService;

    @CommandLine.Parameters(index = "0", description = "Command: download, logout, status", arity = "0..1")
    String command;

    @CommandLine.Parameters(index = "1..*", arity = "0..*")
    String[] args;

    @Override
    public int run(String... args) throws Exception {
        return new CommandLine(this).execute(args);
    }

    @Override
    public void run() {
        try {
            if (command == null || command.isEmpty()) {
                showHelp();
                return;
            }

            switch (command.toLowerCase()) {
                case "download":
                    handleDownload();
                    break;
                case "logout":
                    handleLogout();
                    break;
                case "status":
                    handleStatus();
                    break;
                default:
                    System.out.println("❌ Unknown command: " + command);
                    showHelp();
            }
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
        }
    }

    private void handleDownload() throws Exception {
        if (!oauthService.isAuthenticated()) {
            System.out.println("🔐 First-time setup: Authentication required\n");
            oauthService.authenticate();
        }

        downloadService.processDownloadRequests(args);
    }

    private void handleLogout() {
        if (oauthService.logout()) {
            System.out.println("\n✅ Logged out successfully\n");
        } else {
            System.out.println("\n⚠️  Not currently logged in\n");
        }
    }

    private void handleStatus() throws Exception {
        oauthService.displayStatus();
    }

    private void showHelp() {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📹 Video Downloader CLI");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
        System.out.println("Usage:");
        System.out.println("  videodownloaderx download <url1> [url2] ... [url5]");
        System.out.println("  videodownloaderx logout");
        System.out.println("  videodownloaderx status\n");
        System.out.println("Examples:");
        System.out.println("  videodownloaderx download https://youtube.com/watch?v=dQw4w9WgXcQ");
        System.out.println("  videodownloaderx download url1 url2 url3\n");
        System.out.println("Features:");
        System.out.println("  • GitHub OAuth2 authentication (Device Flow)");
        System.out.println("  • Multithreaded downloads (max 2 concurrent)");
        System.out.println("  • Rate limiting (500ms delays)");
        System.out.println("  • Support for 1000+ platforms via yt-dlp\n");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
    }
}
