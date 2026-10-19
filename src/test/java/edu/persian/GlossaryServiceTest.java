package edu.persian;

import edu.persian.service.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class GlossaryServiceTest extends ServiceTestBase {
    @Test void addViewEditAndReopen() {
        long id=chapter(); var entry=glossary.add(id,"آب","پانی"); glossary.edit(id,entry.id(),"دوست","دوست، ساتھی"); reopen();
        var saved=glossary.list(id).getFirst(); assertEquals(entry.id(),saved.id()); assertEquals("دوست",saved.word()); assertEquals("دوست، ساتھی",saved.meaning()); assertEquals(id,saved.chapterId());
    }
    @ParameterizedTest @NullAndEmptySource @ValueSource(strings={" ","\t","\u00a0"})
    void requiredFieldsRejectBlankWithoutOverwriting(String blank) {
        long id=chapter(); var entry=glossary.add(id,"آب","پانی");
        assertThrows(ValidationException.class,() -> glossary.add(id,blank,"پانی")); assertThrows(ValidationException.class,() -> glossary.add(id,"آب",blank));
        assertThrows(ValidationException.class,() -> glossary.edit(id,entry.id(),blank,"پانی")); assertThrows(ValidationException.class,() -> glossary.edit(id,entry.id(),"آب",blank));
        assertEquals(entry,glossary.list(id).getFirst()); assertEquals(1,glossary.list(id).size());
    }
    @Test void confirmationAndChapterAssociation() {
        long a=chapter(),b=chapter(); var entry=glossary.add(a,"آب","پانی");
        assertTrue(glossary.list(b).isEmpty()); assertThrows(ValidationException.class,() -> glossary.edit(b,entry.id(),"x","y")); assertThrows(ValidationException.class,() -> glossary.delete(b,entry.id(),true));
        assertFalse(glossary.delete(a,entry.id(),false)); assertEquals(1,glossary.list(a).size()); assertTrue(glossary.delete(a,entry.id(),true)); reopen(); assertTrue(glossary.list(a).isEmpty());
    }
    @Test void duplicatesAreAllowedAndMissingRecordsRejected() {
        long id=chapter(); glossary.add(id,"آب","پانی"); glossary.add(id,"آب","پانی"); assertEquals(2,glossary.list(id).size());
        assertThrows(ValidationException.class,() -> glossary.add(999,"x","y")); assertThrows(ValidationException.class,() -> glossary.edit(id,999,"x","y")); assertThrows(ValidationException.class,() -> glossary.delete(id,999,true));
    }
}
