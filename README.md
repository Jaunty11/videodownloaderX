# 🎥 VideoDownloaderX

> ⚡️ A smart, concurrent, and rate-limited Spring Boot CLI app for seamless video downloads using **Java + yt-dlp**, with GitHub OAuth authentication.





## 📦 Dependencies

### 🧰 External Dependencies

## ⚙️ External Dependencies

Your system needs the following dependencies to run **VideoDownloaderX** smoothly.  
Installation instructions are provided separately for **Linux**, **Windows**, and **macOS**.

---

### 🐧 Linux Setup

| Dependency | Purpose | Installation Command |
|-------------|----------|----------------------|
| **Java 17+** | Core runtime for Spring Boot application | `sudo apt update && sudo apt install openjdk-17-jdk -y` |
| **Python 3** | Required runtime for yt-dlp | `sudo apt install python3 -y` |
| **yt-dlp** | External video downloader | `pip install yt-dlp` |
| **GitHub OAuth App** | Enables OAuth 2.0 device flow authentication | Create via [GitHub Developer Settings](https://github.com/settings/developers) |

---

### 🪟 Windows Setup

| Dependency | Purpose | Installation Guide |
|-------------|----------|--------------------|
| **Java 17+** | Core runtime for Spring Boot application | [Download JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) → Run installer → Add `JAVA_HOME` to environment variables |
| **Python 3** | Required runtime for yt-dlp | [Download Python](https://www.python.org/downloads/windows/) → Check “Add to PATH” during install |
| **yt-dlp** | External video downloader | Open CMD → `pip install yt-dlp` |
| **GitHub OAuth App** | Enables OAuth 2.0 device flow authentication | Create via [GitHub Developer Settings](https://github.com/settings/developers) |

---

### 🍎 macOS Setup

| Dependency | Purpose | Installation Command |
|-------------|----------|----------------------|
| **Java 17+** | Core runtime for Spring Boot application | `brew install openjdk@17` |
| **Python 3** | Required runtime for yt-dlp | `brew install python` |
| **yt-dlp** | External video downloader | `pip3 install yt-dlp` |
| **GitHub OAuth App** | Enables OAuth 2.0 device flow authentication | Create via [GitHub Developer Settings](https://github.com/settings/developers) |

---

💡 **Tip:**  
After installing all dependencies, verify everything works:
```bash
java -version
python3 --version
yt-dlp --version
