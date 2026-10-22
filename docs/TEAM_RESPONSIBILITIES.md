# Team responsibilities

This is one shared codebase prepared for four real group members to review and maintain. It does not represent work completed independently by four people.

All Java paths below are relative to `src/main/java/edu/persian/`; tests are in `src/test/java/edu/persian/`.

| Owner / use case | GUI file | BLL file | DAL file | Model and test |
| --- | --- | --- | --- | --- |
| Member A — UC-01: Manage Chapters | `ui/ChapterScreen.java` | `service/ChapterService.java` | `repository/ChapterRepository.java` | `model/Chapter.java`; `ChapterServiceTest.java` |
| Member B — UC-02: Configure Chapter Structure | `ui/StructureScreen.java` | `service/StructureService.java` | `repository/StructureRepository.java` | `model/ComponentType.java`; `StructureServiceTest.java` |
| Member C — UC-03: Manage Instructional Content | `ui/ContentScreen.java` | `service/ContentService.java` | `repository/ContentRepository.java` | `ContentServiceTest.java` |
| Member D — UC-04: Manage Glossary | `ui/GlossaryScreen.java` | `service/GlossaryService.java` | `repository/GlossaryRepository.java` | `model/GlossaryEntry.java`; `GlossaryServiceTest.java` |

Shared infrastructure: `App.java`, `application/LearningStudioFacade.java`, `repository/Database.java`, `repository/DataException.java`, `service/Rules.java`, `service/ValidationException.java`, `ui/Ui.java`, CSS, Maven configuration, scripts and documentation. Shared tests: `ServiceTestBase.java`, `DatabaseTest.java`, `LearningStudioFacadeTest.java`; optional `UiSmoke.java` checks integration. Agree on changes to these before merging. UC-02 and UC-03 use separate DAL classes against the same `components` table, so schema changes still require coordination.

Implementation order: shared model/schema → Member A → Member B → Member C → Member D → integration and full validation. The small glossary service is wired before completeness calculation because a selected glossary's completeness depends on its entries; this does not split the application.

Integration contracts: IDs are generated longs; services accept chapter IDs and validate existence; ordered component lists contain unique enum values; text is stored exactly as entered after blank validation; glossary updates/deletes include both parent and entry ID; deleting a chapter cascades in MariaDB. UI never performs SQL. All modules use the same database and exception conventions.

Recommended branches: `member-a-chapters`, `member-b-structure`, `member-c-content`, `member-d-glossary`. Start from the agreed shared baseline. Each member should review and make their own GUI, BLL and DAL contributions in the assigned files; this repository's existing commits do not prove four independent authors. Keep commits focused on owned files; pull merged changes before beginning another change; request review for schema/common helper changes. Never duplicate shared classes or commit `.tools`, `target`, or database files. Run the entire test suite before integration, not just your owned tests.
