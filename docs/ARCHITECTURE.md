# Architecture

JavaFX UI → `LearningStudioFacade` → use-case services → JDBC repositories → local XAMPP MariaDB server.

Immutable model records carry data. Services enforce validation and association rules without JavaFX. Repositories contain prepared SQL. `App` starts the application and asks `LearningStudioFacade.create(Database)` to assemble one set of collaborating services over a shared database. All four screen classes call this facade rather than knowing individual services; `Ui` supplies common dialogs and feedback. The facade delegates operations and does not duplicate validation or SQL.

See [GRASP and Facade design](GRASP_AND_FACADE.md) for the specific responsibilities and limits of each pattern claim.

## Schema

| Table | Key / relationships | Stored values |
| --- | --- | --- |
| chapters | generated integer primary key | required title |
| components | composite key `(chapter_id,type)`; parent FK cascades | optional ordered position, exact text; unique `(chapter_id,position)` |
| glossary | generated integer primary key; parent FK cascades | required Persian word and Urdu meaning |

Instructional content is stored in its component row to avoid a redundant one-to-one table. NULL position means deselected; content is retained. Multiple NULL positions are legal in MariaDB unique indexes. Positions of active rows are contiguous starting at zero and established transactionally. Type values are constrained in SQL. Glossary has a parent index for chapter-specific reads.

Connections are short-lived. InnoDB enforces foreign keys, and the client sets a five-second connection timeout. Schema creation is idempotent and does not reset existing data. Inserts use generated keys from their statement; reordering first clears active positions then upserts selected types in one transaction. A single chapter DELETE cascades through both child tables, including inactive content. Transactions roll back on SQL or runtime failure. All tables use `utf8mb4` with binary collation to preserve Persian and Urdu text exactly.

The app reads `config/local.properties`, ignored by Git, with a MariaDB JDBC URL and dedicated local credentials. MariaDB stores the `persian_learning` database under XAMPP's `mysql/data` directory. The `persian.config` JVM property overrides the config-file path for isolated smoke runs. JUnit uses uniquely named temporary MariaDB schemas and drops only those schemas. Future schema changes must introduce explicit migrations; this iteration only initializes schema version 1.

## UI and language

Chapter list at left, ordered section preview at right. Structure, content and glossary use modal dialogs. New/rename and glossary entry dialogs have Save/Cancel; a failed save remains open. Deletion prompts include the target and cascade impact. Content removal has its own warning. Form cancellation discards unsaved edits without modifying saved state. There are no editable background forms to lose during navigation.

English/Urdu action labels support the administrator. At the user's request, text-entry controls start at the left edge; saved Persian and Urdu previews retain RTL orientation. Text is never transliterated, normalized or trimmed; Unicode whitespace is considered during required-field validation only. Confirm mixed bidi text and optional font preference on the target Windows laboratory machines.

## Boundaries and limits

No authentication, learning/student UI or network service. One administrator instance is the intended classroom deployment; multi-user editing and conflict merging are not provided. Small synchronous database calls simplify the JavaFX application; measure the two-second requirement on target hardware. Large rich documents and rich-text formatting are outside this SRS. Local disk failures show a recoverable message; technical causes are available in exceptions/log output. No production sample data is inserted.
