package edu.persian;

import edu.persian.application.LearningStudioFacade;
import edu.persian.model.ComponentType;
import edu.persian.service.ValidationException;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class LearningStudioFacadeTest extends ServiceTestBase {
    @Test void oneFacadeCoordinatesAllFourUseCasesAcrossReopen() {
        LearningStudioFacade studio = LearningStudioFacade.create(db);
        long chapterId = studio.createChapter("درس ۱").id();
        studio.renameChapter(chapterId, "سلام اور تعارف");
        studio.saveStructure(chapterId, List.of(ComponentType.DIALOGUE, ComponentType.GLOSSARY));
        studio.saveContent(chapterId, ComponentType.DIALOGUE, "سلام! حال شما چطور است؟");
        long wordId = studio.addWord(chapterId, "دوست", "ساتھی").id();

        assertEquals("سلام اور تعارف", studio.chapter(chapterId).title());
        assertTrue(studio.isComplete(chapterId));
        assertFalse(studio.deleteWord(chapterId, wordId, false));
        assertFalse(studio.deleteChapter(chapterId, false));

        LearningStudioFacade reopened = LearningStudioFacade.create(new edu.persian.repository.Database(baseUrl + schema, testUser, testPassword));
        assertEquals(List.of(ComponentType.DIALOGUE, ComponentType.GLOSSARY), reopened.structure(chapterId));
        assertEquals("سلام! حال شما چطور است؟", reopened.content(chapterId, ComponentType.DIALOGUE));
        assertEquals("دوست", reopened.glossary(chapterId).getFirst().word());
        assertThrows(ValidationException.class, () -> reopened.saveContent(chapterId, ComponentType.DIALOGUE, " "));
        assertTrue(reopened.isComplete(chapterId));

        reopened.removeContent(chapterId, ComponentType.DIALOGUE);
        assertFalse(reopened.isComplete(chapterId));
        assertTrue(reopened.deleteChapter(chapterId, true));
        assertTrue(reopened.listChapters().isEmpty());
    }
}
