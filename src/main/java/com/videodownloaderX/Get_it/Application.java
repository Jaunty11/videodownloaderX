package com.videodownloaderX.Get_it;

import com.videodownloaderX.Get_it.service.DownloadService;
import com.videodownloaderX.Get_it.service.OAuthDeviceFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.File;

@SpringBootApplication
public class Application implements CommandLineRunner {

	@Autowired
	private OAuthDeviceFlowService oauthService;

	@Autowired
	private DownloadService downloadService;

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.setBannerMode(Banner.Mode.OFF);
		System.exit(SpringApplication.exit(app.run(args)));
	}

	@Override
	public void run(String... args) throws Exception {
		if (args.length == 0) {
			showHelp();
			return;
		}

		String command = args[0];

		switch (command.toLowerCase()) {
			case "download":
				handleDownload(args);
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
	}

	private void handleDownload(String[] args) throws Exception {
		// Check OAuth authentication
		if (!oauthService.isAuthenticated()) {
			System.out.println("🔐 First-time setup: Authentication required\n");
			oauthService.authenticate();
		}

		// Process downloads
		downloadService.processDownloadRequests(args);
	}

	private void handleLogout() {
		File tokenFile = new File(System.getProperty("user.home") +
				"/.videodownloader/token.json");
		if (tokenFile.delete()) {
			System.out.println("\n✅ Logged out successfully");
			System.out.println("Your OAuth token has been deleted.\n");
		} else {
			System.out.println("\n⚠️  Not currently logged in\n");
		}
	}

	private void handleStatus() {
		if (oauthService.isAuthenticated()) {
			try {
				String username = oauthService.getAuthenticatedUsername();
				System.out.println("\n✅ Authenticated");
				System.out.println("👤 GitHub User: " + username);
				System.out.println("📁 Token: ~/.videodownloader/token.json\n");
			} catch (Exception e) {
				System.out.println("\n❌ Authentication error: " + e.getMessage() + "\n");
			}
		} else {
			System.out.println("\n❌ Not authenticated");
			System.out.println("Run 'download' command to authenticate\n");
		}
	}

	private void showHelp() {
		System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
		System.out.println("📹 Video Downloader CLI");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
		System.out.println("Usage:");
		System.out.println("  videodownloader download <url1> [url2] ... [url5]");
		System.out.println("  videodownloader logout");
		System.out.println("  videodownloader status\n");
		System.out.println("Examples:");
		System.out.println("  videodownloader download https://youtube.com/watch?v=dQw4w9WgXcQ");
		System.out.println("  videodownloader download url1 url2 url3\n");
		System.out.println("Features:");
		System.out.println("  • GitHub OAuth2 authentication (Device Flow)");
		System.out.println("  • Multithreaded downloads (max 2 concurrent)");
		System.out.println("  • Rate limiting (500ms delays)");
		System.out.println("  • Support for 1000+ platforms via yt-dlp\n");
		System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");
	}
}
