# Persian Learning Studio

A desktop app for preparing Persian lessons with Urdu explanations. You can create chapters, choose and order lesson sections, write lesson text, and add Persian–Urdu glossary words. This is **Iteration 1** of the project.

## What to install on each Windows laptop

- **JDK 21** (Java Development Kit), not just the Java runtime.
- **Eclipse IDE for Java Developers** to open and run the project. VS Code with the Java Extension Pack also works.
- **XAMPP** for the local MariaDB database. Install it in `C:\xampp` for the easiest setup. You only need to start **MySQL** in its control panel; Apache is not needed.
- **Git** to clone the project. You can also download the repository ZIP from GitHub.

Eclipse can run the Maven project directly. If you prefer PowerShell commands instead of Eclipse, also install **Apache Maven** and make sure `mvn` works. Maven downloads JavaFX, the MariaDB driver and JUnit 5 automatically; do not install those separately. The first run needs internet access.

## Clone and run

1. Clone the repository and open its folder:

   ```powershell
   git clone https://github.com/Ahmad-Asg/Persian-Learning-Language-App.git
   cd Persian-Learning-Language-App
   ```

2. Open the **XAMPP Control Panel** and click **Start** beside **MySQL**.
3. In the project folder, run this once in PowerShell to create your own local database and configuration:

   ```powershell
   powershell -ExecutionPolicy Bypass -File scripts/setup-database.ps1
   ```

   If XAMPP is not in `C:\xampp`, use `-XamppPath "C:\path\to\xampp"` at the end of that command. The script also recognizes this project's `.tools\xampp` folder. It expects the usual fresh-XAMPP `root` account with no password. Do not share the generated `config/local.properties` file.

4. Open **Eclipse** → **File** → **Import** → **Maven** → **Existing Maven Projects**. Choose the cloned folder (the one containing `pom.xml`) and click **Finish**. If Eclipse asks for a Java runtime, select **JDK 21**.
5. Right-click the imported project in Eclipse → **Run As** → **Maven build…**. Type `javafx:run` in **Goals**, then click **Run**. Start MySQL in XAMPP each time before opening the app.

To run from PowerShell instead, after installing Maven:

```powershell
mvn test
mvn javafx:run
```

If Eclipse shows errors after import, right-click the project → **Maven** → **Update Project**, and check that Eclipse is using JDK 21. Do not double-click the built JAR to launch the app; use `javafx:run`.

## Using the app

Click **New chapter**, give it a title, and select it from the left list. Click **Structure** to add and order sections. Use **Edit content** to write lesson text and **Glossary** to add Persian words with Urdu meanings. A chapter shows **Complete** when all selected sections have content; otherwise it remains a draft. Typing starts at the left edge, and saved Persian/Urdu previews display right-to-left. Your data remains in your own laptop's MariaDB database between sessions.

## Team files and tests

The four use cases are UC-01 Chapters, UC-02 Structure, UC-03 Content and UC-04 Glossary. See [the exact files for each team member](docs/TEAM_RESPONSIBILITIES.md), [requirements](docs/ITERATION_1_REQUIREMENTS.md), [architecture](docs/ARCHITECTURE.md), [test plan](docs/TEST_PLAN.md), and [acceptance checklist](docs/ACCEPTANCE_CHECKLIST.md).

Run tests in Eclipse with **Run As → Maven build… → Goals: `test`**, or use `mvn test`. MySQL must be running. Tests use temporary test databases and remove them afterward.

The repository excludes local XAMPP/JDK/Maven downloads, database files, passwords and compiled output. Each teammate installs the required software and creates their own local database; cloning does not copy another person's lessons.
