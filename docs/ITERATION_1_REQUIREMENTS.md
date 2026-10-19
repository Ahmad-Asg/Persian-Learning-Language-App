# Iteration 1 requirements

Authority: `PERSIAN_LANGUAGE_LEARNING_APP_SRS_ITERATION_1.pdf`, version 1.0, 14 October 2026, all three pages reviewed. The user's pasted request supplies implementation and documentation instructions. The PDF supplies product requirements. This implementation is one administrator desktop application for Urdu-speaking users preparing Persian lessons.

## Scope and acceptance

| Use case | Owner | Acceptance criteria |
| --- | --- | --- |
| UC-01 Manage chapters | Member A | Create with nonblank title and generated ID; list/open/rename; confirm or cancel deletion; cascade all dependent data; restore after restart. |
| UC-02 Configure structure | Member B | Select any of seven supported types, arrange their order, save/reload independently per chapter; reject duplicate/null types without changing saved configuration. |
| UC-03 Instructional content | Member C | View/edit configured sections in saved order; preserve exact Unicode; reject blank saves without replacing saved text; explicitly remove content; report incomplete sections. |
| UC-04 Glossary | Member D | Add/view/edit/delete chapter-owned Persian words and Urdu meanings; require both fields; confirm deletion; preserve ownership and persistence. |

## Functional requirements

- FR-CH-01–08: chapter CRUD, unique generated IDs, cascade deletion, session persistence.
- FR-CS-01–07: learning objectives, Persian dialogue, Persian reading, Urdu translation/explanation, grammar/language note, examples, glossary; chapter-specific selection/order; content CRUD and Unicode preservation.
- FR-GL-01–06: each chapter has an accessible glossary, each entry belongs to exactly one chapter, complete entry CRUD and confirmation.

## Validation and persistence

- BR-01: title must contain a non-whitespace character, including rejection of Unicode space-only input.
- BR-02: selected components require content before a chapter is complete. Empty draft configurations/components are allowed. Saving blank instructional text fails; explicit Remove content clears it and makes the chapter incomplete.
- BR-03: both glossary fields are required.
- BR-04–05: chapter/glossary deletion requires UI confirmation; chapter warning explicitly includes components, saved text and glossary entries.
- BR-06–07: validation runs before writes; dialogs retain entered text after failure; errors name affected fields. Cancel closes without writing.
- No duplicate-title or duplicate-word restriction; no arbitrary length limit or language-character filter.
- MariaDB persists chapters, components, text and glossary; prepared statements, InnoDB foreign keys, cascade deletion and transactional reordering prevent orphan/partial data.

## Decisions and ambiguity resolutions

1. **User confirmed:** deselecting a section retains its text for reuse. The row remains with NULL position; reselecting restores the content. Chapter deletion removes retained data too.
2. Each supported type may occur once per chapter: a documented technical choice for selecting types rather than managing repeated section instances. Null and duplicate types are invalid configurations.
3. Glossary is a structured list, not a second free-text field. It is always accessible for every chapter. Selecting it in the structure places its entries in chapter order. One or more valid entries satisfy its content requirement.
4. No structure is considered a draft rather than a complete chapter. Completeness is computed, never a new publication workflow. There is no minimum structure required to save a draft.
5. Content can be deliberately removed even though a blank Save is invalid. This reconciles FR-CS-04 with BR-02 and the empty-required-content test requirement.
6. Java 21 LTS, JavaFX, Maven, JDBC/MariaDB and JUnit 5 follow the user's revised technology choices. XAMPP supplies local MariaDB. No administrator login system is needed.

## Nonfunctional requirements

- NFR-US-01–06: bilingual English/Urdu controls and messages, mixed-text preservation, Unicode-capable font, explicit actions and field-identifying errors. The user's later UI request changes entry controls to left-starting while saved Persian/Urdu previews remain RTL. Visual language correctness needs user/lab review.
- NFR-RL-01–03: persistent saved state, no malformed writes on validation failure, no orphan rows on deletion.
- NFR-PF-01–02: opening/saving classroom-sized content should take under two seconds on target lab hardware. Target-machine measurement is still an acceptance task; local timing is not a substitute.
- NFR-MT-01–03: services test without UI; structure rules are independent; tests create controlled temporary databases with no manually prepared data.

## Testing requirements

Every use case has JUnit 5 tests for successful, alternate, invalid and relevant boundary flows. Chapter and glossary tests include cancelled/confirmed deletion; structure tests include saved-order preservation and removal; content tests include exact Persian/Urdu round trips and blank-save preservation. Tests use real isolated MariaDB test databases; no mocking framework is present.

## Excluded

Student/teacher accounts, authentication, quizzes, AI, speech, pronunciation recognition, student dashboards, analytics and cloud synchronization. No Iteration 2 functionality is implemented.
