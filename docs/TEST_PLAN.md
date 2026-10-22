# Test plan

Start XAMPP **MySQL** first. Then run `powershell -ExecutionPolicy Bypass -File scripts/maven.ps1 test` or, with installed tools, `mvn test`. JUnit reports are in `target/surefire-reports/`. Each test creates a uniquely named `persian_test_*` MariaDB schema and drops it afterward; no test reads or changes the application's `persian_learning` database. No Mockito or other mocking framework is used. The local test account defaults to XAMPP `root` with an empty password; override its JDBC URL/user/password with `persian.test.*` JVM properties when required.

## Traceability

| Requirements | Test class and scenarios |
| --- | --- |
| FR-CH-01–05,08; BR-01,06–07 | ChapterServiceTest: create/list/view/rename/reopen; generated IDs; blank/null/Unicode-space titles; punctuation and single character; missing records |
| FR-CH-06–07; BR-04–05; NFR-RL-03 | ChapterServiceTest: cancelled/confirmed deletion, dependent content/glossary row counts; UI manual check for warning wording |
| FR-CS-01–03,05–06; NFR-MT-02 | StructureServiceTest: all seven types, reverse order/reopen, chapter independence, deselection/reselection retention, null/duplicate rejection, missing chapter and rollback |
| FR-CS-04,07; BR-02,06 | ContentServiceTest: exact Persian/Urdu/mixed Unicode, edit/reopen, empty required text rejection, deliberate removal, completeness, missing/unconfigured/wrong-chapter association |
| FR-GL-01–06; BR-03–04,06 | GlossaryServiceTest: add/view/edit/reopen, missing word/meaning, cancelled/confirmed deletion, cross-chapter rejection, duplicate allowance and missing records |
| NFR-RL-01–03 | DatabaseTest plus reopen tests: InnoDB FK enforcement, transaction rollback and non-destructive repeated initialization |
| Four-use-case integration | LearningStudioFacadeTest: create, configure, write, add glossary, reopen, validate, remove and delete through the same UI-facing API |
| NFR-US-01–06 | UI smoke and manual checklist below: layout, bilingual labels, RTL, validation feedback and confirmation dialogs |
| NFR-PF-01–02 | Measure on laboratory hardware with representative classroom data; not proven by unit tests |

## Manual acceptance exercise

1. Launch at 1120×760 and 900×620. Create `درس ۱ — سلام`. Attempt a blank title; error must identify title and leave form open. Cancel then rename a valid chapter.
2. Add dialogue, explanation and glossary. Move glossary up/down; save, reopen and verify order. Create a second chapter with another structure.
3. Enter `سلام! می‌روم — یہ اُردو ہے۔ Lesson 2: ۱۲۳ (45)؟` in a text component. Check joining, punctuation, numbers, line wrapping and cursor entry with a Persian/Urdu keyboard. Save, edit and reopen.
4. Blank the content and Save; previous content must survive and editor must remain open. Cancel. Use Remove content and confirm; chapter becomes draft.
5. Deselect a component with saved content; reselect it and verify restoration. Cancel a structure edit and verify saved order remains.
6. Add/edit glossary word `آب` and meaning `پانی`. Attempt each blank field. Cancel a deletion; confirm another. Second chapter must remain untouched.
7. Close/relaunch the app. Verify chapter, structure, Unicode text and glossary persisted.
8. Delete a populated chapter: warning must name cascading components/text/glossary. Cancel first; then confirm. Verify second chapter survives.
9. On target lab hardware, measure opening a chapter and saving content/entry using representative classroom-sized data. Both should be under two seconds. Record hardware, dataset size and elapsed times.

## Verification status

See ACCEPTANCE_CHECKLIST.md for actual execution results and outstanding manual checks. Automated service tests do not prove font rendering, natural-language quality or target-machine performance.

Optional real-window smoke check on this Windows portable setup:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/maven.ps1 test-compile
powershell -ExecutionPolicy Bypass -File scripts/ui-smoke.ps1
```

This briefly opens JavaFX windows with test-only data in an isolated MariaDB schema, drives dialog buttons, and saves screenshots beneath `target/ui-smoke/`. It is separate from the UI-free JUnit suite. Requires XAMPP MySQL, portable tools and previously resolved dependencies; it is not a general installation launcher. Previous SQLite results from 18 October 2026 do not count as MariaDB verification; see the acceptance checklist for the latest result.
