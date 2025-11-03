# 🎥 VideoDownloaderX

> ⚡️ A smart, concurrent, and rate-limited Spring Boot CLI app for seamless video downloads using **Java + yt-dlp**, with GitHub OAuth authentication.





step 1-## 📦 Dependencies

## ⚙️ External Dependencies

Your system needs the following dependencies to run **VideoDownloaderX** smoothly.  
Installation instructions are provided separately for **Linux**, **Windows**, and **macOS**.

---

### 🐧 Linux Setup

| Dependency | Purpose | Installation Command |
|-------------|----------|----------------------|
| **Java 17+** | Core runtime for Spring Boot application | `sudo apt update && sudo apt install openjdk-17-jdk -y` |
Path for java should be configured--
| **Python 3** | Required runtime for yt-dlp | `sudo apt install python3 -y` |
| **yt-dlp** | External video downloader | `pip install yt-dlp` |
| **GitHub OAuth App** | Enables OAuth 2.0 device flow authentication | Create one via [GitHub Developer Settings](https://github.com/settings/developers) (no client ID needed at setup time) |

---

### 🪟 Windows Setup

| Dependency | Purpose | Installation Guide |
|-------------|----------|--------------------|
| **Java 17+** | Core runtime for Spring Boot application | [Download JDK 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) → Run installer → Add `JAVA_HOME` to environment variables |
Path for java should be configured--
| **Python 3** | Required runtime for yt-dlp | [Download Python](https://www.python.org/downloads/windows/) → Check “Add to PATH” during install |
| **yt-dlp** | External video downloader | Open CMD → `pip install yt-dlp` |
| **GitHub OAuth App** | Enables OAuth 2.0 device flow authentication | Create one via [GitHub Developer Settings](https://github.com/settings/developers) |

---

### 🍎 macOS Setup

| Dependency | Purpose | Installation Command |
|-------------|----------|----------------------|
| **Java 17+** | Core runtime for Spring Boot application | `brew install openjdk@17` |
Path for java should be configured--
| **Python 3** | Required runtime for yt-dlp | `brew install python` |
| **yt-dlp** | External video downloader | `pip3 install yt-dlp` |
| **GitHub OAuth App** | Enables OAuth 2.0 device flow authentication | Create one via [GitHub Developer Settings](https://github.com/settings/developers) |

---

💡 **Tip:**  
After installing all dependencies, verify everything works:
bash--
java -version
python3 --version
yt-dlp --version


step -2 
## 🧩 How to Run the Project

Once dependencies are installed, follow these steps to run **VideoDownloaderX** locally.

---

### 🛠️ 1. Clone the Repository
bash
git clone https://github.com/Jaunty11/VideoDownloaderX.git
cd VideoDownloaderX


step -3 
⚙️  Build the Project
Make sure you’re in the project root (where pom.xml is located).
./mvnw clean package
💡 If mvnw doesn’t work, install Maven manually and use:
mvn clean package


step -4
from your VideodownloaderX level -

java -jar target/VideoDownloaderX-0.0.1-SNAPSHOT.jar download \
"https://site.com/watch?v=AAA" \
"https://site.com/watch?v=BBB" \         ##for multiple URLs put Backslash but in last URL dont put Backslash after URL
"https://site.com/watch?v=CCC"           ## Max downloads ---- 5 suggested concurrently       

step -5

Your CLI will prompt ---

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
🔐 GitHub Authentication Required
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Please complete authentication:

  1️⃣  Visit: https://github.com/login/device
  2️⃣  Enter code: X7TF-H9PK

Waiting for you to authorize...
(Press Ctrl+C to cancel)


step -6

after verification your application will start downloading.... CONCURRENTLY------



step -7

java -jar target/VideoDownloaderX-0.0.1-SNAPSHOT.jar status



step -8 (Optional)

java -jar target/VideoDownloaderX-0.0.1-SNAPSHOT.jar logout



If not working try

8) Troubleshooting (common issues)
yt-dlp not found:

yt-dlp --version
try updating / upgrading 


🐧 Linux / 🍎 macOS
If installed via pip:
pip install -U yt-dlp
or (if your system uses pip3):
pip3 install -U yt-dlp

MAC-
If installed via package manager (Homebrew for macOS):
brew upgrade yt-dlp


🪟 Windows
If installed via pip:
pip install -U yt-dlp
or if you use Python 3 explicitly:
python -m pip install -U yt-dlp


