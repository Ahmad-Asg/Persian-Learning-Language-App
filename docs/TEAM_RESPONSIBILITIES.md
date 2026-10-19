# Team responsibilities

This is one shared codebase prepared for four real group members to review and maintain. It does not represent work completed independently by four people.

All Java paths below are relative to `src/main/java/edu/persian/`; tests are in `src/test/java/edu/persian/`.

| Owner | Primary files | Responsibilities and tests |
| --- | --- | --- |
| Member A — UC-01: Manage Chapters | `model/Chapter.java`, `repository/ChapterRepository.java`, `service/ChapterService.java`, `ui/ChapterScreen.java` | Chapter CRUD, titles, IDs, cascade confirmation; `ChapterServiceTest.java`. Own UC-01 acceptance. |
| Member B — UC-02: Configure Chapter Structure | `model/ComponentType.java`, configuration methods in `repository/ComponentRepository.java`, `service/StructureService.java`, `ui/StructureScreen.java` | Selection, ordering, retained text and independent structures; `StructureServiceTest.java`. Own UC-02 acceptance. |
| Member C — UC-03: Manage Instructional Content | content methods in `repository/ComponentRepository.java`, `service/ContentService.java`, `ui/ContentScreen.java` | Exact text round trips, editing/removal, component association, completeness; `ContentServiceTest.java`. Own UC-03 acceptance. |
| Member D — UC-04: Manage Glossary | `model/GlossaryEntry.java`, `repository/GlossaryRepository.java`, `service/GlossaryService.java`, `ui/GlossaryScreen.java` | Word/meaning CRUD, chapter ownership, validation/confirmation; `GlossaryServiceTest.java`. Own UC-04 acceptance. |

Shared infrastructure: `App.java`, `repository/Database.java`, `repository/DataException.java`, `service/Rules.java`, `service/ValidationException.java`, `ui/Ui.java`, CSS, Maven configuration, scripts and documentation. Shared tests: `ServiceTestBase.java`, `DatabaseTest.java`; optional `UiSmoke.java` checks integration. Agree on changes to these before merging. B and C share one component repository deliberately; they must coordinate its edits.

Implementation order: shared model/schema → Member A → Member B → Member C → Member D → integration and full validation. The small glossary service is wired before completeness calculation because a selected glossary's completeness depends on its entries; this does not split the application.

Integration contracts: IDs are generated longs; services accept chapter IDs and validate existence; ordered component lists contain unique enum values; text is stored exactly as entered after blank validation; glossary updates/deletes include both parent and entry ID; deleting a chapter cascades in MariaDB. UI never performs SQL. All modules use the same database and exception conventions.

Recommended branches: `member-a-chapters`, `member-b-structure`, `member-c-content`, `member-d-glossary`. Start from the agreed shared baseline. Keep commits focused on owned files; pull merged changes before beginning another change; request review for schema/common helper changes. Never duplicate shared classes or commit `.tools`, `target`, or database files. Run the entire test suite before integration, not just your owned tests.
