# GRASP and Facade design

The four use cases remain UC-01 Chapters, UC-02 Structure, UC-03 Content and UC-04 Glossary. Their behavior is unchanged. The design makes the responsibility boundaries visible in code:

`ChapterScreen` / dialog screens → `LearningStudioFacade` → four use-case services → repositories → MariaDB.

## Facade pattern

`application/LearningStudioFacade.java` is the single public entry point used by the JavaFX screens. It offers chapter, structure, content and glossary operations while hiding service and repository construction. The UI neither imports the four service classes nor accesses JDBC. The facade delegates to the responsible service and does not become a second place for business rules.

`App.java` creates the local `Database` and calls `LearningStudioFacade.create(database)`. The factory constructs the repositories and services once, sharing the same `ComponentRepository` between structure and content. This avoids separate, inconsistent object graphs in production and the UI smoke exercise.

## GRASP responsibilities

| Principle | Actual assignment | Why |
| --- | --- | --- |
| Controller | `LearningStudioFacade` receives system operations from the screens and directs them to use-case services. Screen classes handle JavaFX events and dialogs only. | The UI does not become the owner of validation or SQL. |
| Creator | `LearningStudioFacade.create(Database)` assembles the services and repositories it holds; repositories create model records from stored data. | The creator knows the dependencies and uses the created objects. |
| Information Expert | `ChapterService`, `StructureService`, `ContentService` and `GlossaryService` enforce their respective use-case rules. Repositories know persistence details and schema relationships. | Each rule sits with the class that has the needed information. |
| Pure Fabrication | Services and repositories are technical classes separate from the real-world `Chapter` and `GlossaryEntry` records. | Persistence and workflow rules do not overload the domain data records. |
| Low Coupling | Screens depend on `LearningStudioFacade`, services depend on repositories, and repositories depend on `Database`; calls do not skip layers. | UI changes need not change SQL and database changes need not change screen event code. |
| High Cohesion | Each service and repository focuses on one use case or data area. The facade only assembles and delegates. | A single class does not own UI, validation and persistence together. |
| Indirection / Protected Variations | The facade separates the UI from service construction, while repositories separate services from SQL. | Internal service or storage changes have fewer callers to update. |

This project does not use inheritance or polymorphism as a main design mechanism. No extra class hierarchy was introduced merely to claim those GRASP principles. `LearningStudioFacadeTest` checks the public facade across all four use cases and persistence after reopening; the existing service tests check each use case's rules.
