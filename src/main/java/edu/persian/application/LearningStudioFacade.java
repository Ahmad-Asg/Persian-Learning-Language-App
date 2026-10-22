package edu.persian.application;

import edu.persian.model.Chapter;
import edu.persian.model.ComponentType;
import edu.persian.model.GlossaryEntry;
import edu.persian.repository.ChapterRepository;
import edu.persian.repository.ComponentRepository;
import edu.persian.repository.Database;
import edu.persian.repository.GlossaryRepository;
import edu.persian.service.ChapterService;
import edu.persian.service.ContentService;
import edu.persian.service.GlossaryService;
import edu.persian.service.StructureService;
import java.util.List;
import java.util.Objects;

/** The UI's single entry point for the four chapter-authoring use cases. */
public final class LearningStudioFacade {
    private final ChapterService chapters;
    private final StructureService structure;
    private final ContentService content;
    private final GlossaryService glossary;

    private LearningStudioFacade(ChapterService chapters, StructureService structure,
                                 ContentService content, GlossaryService glossary) {
        this.chapters = chapters;
        this.structure = structure;
        this.content = content;
        this.glossary = glossary;
    }

    /** Assemble collaborators once around the same database. */
    public static LearningStudioFacade create(Database database) {
        Objects.requireNonNull(database, "database");
        ChapterService chapters = new ChapterService(new ChapterRepository(database));
        ComponentRepository components = new ComponentRepository(database);
        StructureService structure = new StructureService(chapters, components);
        GlossaryService glossary = new GlossaryService(chapters, new GlossaryRepository(database));
        ContentService content = new ContentService(components, structure, glossary);
        return new LearningStudioFacade(chapters, structure, content, glossary);
    }

    public List<Chapter> listChapters() { return chapters.list(); }
    public Chapter chapter(long id) { return chapters.get(id); }
    public Chapter createChapter(String title) { return chapters.create(title); }
    public void renameChapter(long id, String title) { chapters.rename(id, title); }
    public boolean deleteChapter(long id, boolean confirmed) { return chapters.delete(id, confirmed); }

    public List<ComponentType> structure(long chapterId) { return structure.get(chapterId); }
    public void saveStructure(long chapterId, List<ComponentType> types) { structure.save(chapterId, types); }

    public String content(long chapterId, ComponentType type) { return content.get(chapterId, type); }
    public void saveContent(long chapterId, ComponentType type, String text) { content.save(chapterId, type, text); }
    public void removeContent(long chapterId, ComponentType type) { content.remove(chapterId, type); }
    public boolean isComplete(long chapterId) { return content.complete(chapterId); }

    public List<GlossaryEntry> glossary(long chapterId) { return glossary.list(chapterId); }
    public GlossaryEntry addWord(long chapterId, String word, String meaning) { return glossary.add(chapterId, word, meaning); }
    public void editWord(long chapterId, long entryId, String word, String meaning) { glossary.edit(chapterId, entryId, word, meaning); }
    public boolean deleteWord(long chapterId, long entryId, boolean confirmed) { return glossary.delete(chapterId, entryId, confirmed); }
}
