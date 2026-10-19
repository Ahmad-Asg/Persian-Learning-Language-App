## Software to install (Windows)

- [Git for Windows](https://git-scm.com/downloads/win)
- [JDK 21](https://adoptium.net/temurin/releases/?version=21&os=windows) — choose **JDK**, not JRE
- [Eclipse IDE for Java Developers](https://www.eclipse.org/downloads/packages/)
- [XAMPP](https://www.apachefriends.org/download.html) — install in `C:\xampp`

## Clone and run

1. In PowerShell:

   ```powershell
   git clone https://github.com/Ahmad-Asg/Persian-Learning-Language-App.git
   cd Persian-Learning-Language-App
   ```

2. Open XAMPP and start **MySQL**. You do not need Apache.
3. In the same PowerShell window, set up the database once:

   ```powershell
   powershell -ExecutionPolicy Bypass -File scripts/setup-database.ps1
   ```

   If XAMPP is installed elsewhere, add `-XamppPath "your XAMPP folder"` to that command.
4. In Eclipse: **File → Import → Maven → Existing Maven Projects**. Select the cloned folder and use **JDK 21**.
5. Right-click the imported project → **Run As → Maven build…** → enter `javafx:run` in **Goals** → **Run**. The first run needs internet.

Start MySQL in XAMPP before opening the app each time.

## How to use

Click **New chapter** to create a lesson. Use **Structure** to choose sections, **Edit content** to write text, and **Glossary** to add Persian words with Urdu meanings. Click **Save** to keep your changes.
