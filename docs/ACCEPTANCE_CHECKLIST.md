# Acceptance checklist

Implementation is limited to the four Iteration 1 use cases.

- [x] UC-01 implemented: create/list/view/rename, generated IDs, required titles, confirmed deletion with cascade.
- [x] UC-02 implemented: seven types, selection/order, chapter independence, persistent configuration, retained deselected text.
- [x] UC-03 implemented: ordered preview, RTL editing, nonblank save, explicit removal, completeness and Unicode preservation.
- [x] UC-04 implemented: chapter-specific glossary CRUD, required fields and confirmed deletion.
- [x] Shared MariaDB layer, prepared SQL, InnoDB foreign keys, transactions and no production seed data.
- [x] JUnit 5 test source for all use cases, no mocking framework.
- [x] README, requirements, architecture, team ownership and test documentation.
- [x] No later-iteration functionality.
- [x] Maven build and full suite executed successfully against MariaDB: 34 tests, zero failures/errors/skips.
- [x] JavaFX startup and real dialog smoke exercise passed against MariaDB.
- [ ] Full manual acceptance exercise completed by team on target lab machines.
- [ ] Urdu/Persian language, keyboard and mixed-direction rendering signed off by an Urdu-speaking reviewer.
- [ ] Two-second performance target measured on laboratory hardware.

Implementation checks above identify present behavior, not a claim of full manual acceptance. Record actual checks below when completed.

## Recorded verification — 18 October 2026

- Verified portable Temurin JDK/javac 21.0.12.1 and Maven 3.9.9.
- Before the MariaDB conversion, `scripts/maven.ps1 package` passed 34 JUnit cases against SQLite. Those results are historical; the MariaDB rerun is recorded below.
- Before the MariaDB conversion, `scripts/ui-smoke.ps1` passed using isolated SQLite test data. That result is historical; the MariaDB rerun is recorded below.
- Rendered chapter, structure, content, glossary and compact layout screenshots inspected. A clipped bilingual dialog button was corrected with sufficient width.
- Maven dependency downloads required network permission. A restricted test run encountered access-denied errors cleaning Windows temporary directories; the approved unrestricted rerun passed. These were environment restrictions, not waived tests.
- Full human keyboard/font/language review, all manual acceptance steps and lab-machine performance remain outstanding as marked above. No claim of full SRS acceptance is made before these checks.

## MariaDB change — 19 October 2026

The user replaced the local persistence choice with XAMPP MariaDB. The existing SQLite file was read-only inspected and contained zero chapters, zero components, and zero glossary entries. No data migration was required; the SQLite file remains untouched. The new app reads a Git-ignored MariaDB connection file and initializes tables in `persian_learning`.

- XAMPP 8.2.12 with MariaDB 10.4.32 was downloaded from the Apache Friends linked SourceForge package, verified against its SHA-256, and extracted in ignored `.tools/xampp/`. MariaDB is bound to `127.0.0.1`; XAMPP Control Panel opens.
- `scripts/setup-database.ps1` created `persian_learning` and a dedicated application account. The generated password is only in Git-ignored `config/local.properties`.
- `scripts/maven.ps1 package`: passed against running MariaDB, including all 34 JUnit cases (A: 10, B: 5, C: 8, D: 8, shared database: 3).
- `scripts/ui-smoke.ps1`: passed in an isolated MariaDB schema, including real `App.start` startup and all four use-case dialogs.
- A read-only `Database.local()` connection using the saved application account succeeded; the production chapter table was empty.
