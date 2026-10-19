package edu.persian;

import edu.persian.repository.*;
import org.junit.jupiter.api.Test;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest extends ServiceTestBase {
    @Test void foreignKeysAreEnabledOnEveryConnection() throws Exception {
        for(int i=0;i<2;i++) try(var c=db.open();var s=c.createStatement()) {
            assertThrows(SQLException.class,() -> s.executeUpdate("INSERT INTO glossary(chapter_id,word,meaning) VALUES (999,'a','b')"));
            assertThrows(SQLException.class,() -> s.executeUpdate("INSERT INTO components(chapter_id,type,position,content) VALUES (999,'READING',0,'')"));
        }
    }
    @Test void failedTransactionDoesNotLeavePartialData() {
        assertThrows(DataException.class,() -> db.transaction(c -> {
            try(var s=c.createStatement()) { s.executeUpdate("INSERT INTO chapters(title) VALUES ('valid')"); s.executeUpdate("INSERT INTO chapters(title) VALUES ('')"); } return null;
        })); assertTrue(chapters.list().isEmpty());
    }
    @Test void initializationPreservesExistingData() { long id=chapter(); reopen(); reopen(); assertEquals(id,chapters.list().getFirst().id()); }
}
