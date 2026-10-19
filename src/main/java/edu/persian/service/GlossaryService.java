package edu.persian.service;

import edu.persian.model.GlossaryEntry;
import edu.persian.repository.GlossaryRepository;
import java.util.List;

public final class GlossaryService {
    private final ChapterService chapters;
    private final GlossaryRepository repository;
    public GlossaryService(ChapterService chapters, GlossaryRepository repository) { this.chapters=chapters; this.repository=repository; }
    public List<GlossaryEntry> list(long chapter) { chapters.get(chapter); return repository.list(chapter); }
    public GlossaryEntry add(long chapter, String word, String meaning) {
        chapters.get(chapter); validate(word,meaning); return repository.create(chapter,word,meaning);
    }
    public void edit(long chapter, long id, String word, String meaning) {
        chapters.get(chapter); validate(word,meaning); Rules.found(repository.update(chapter,id,word,meaning));
    }
    private void validate(String word, String meaning) {
        Rules.required(word,"Persian word / فارسی لفظ"); Rules.required(meaning,"Urdu meaning / اردو معنی");
    }
    public boolean delete(long chapter, long id, boolean confirmed) {
        Rules.found(list(chapter).stream().anyMatch(e -> e.id()==id));
        if (!confirmed) return false;
        Rules.found(repository.delete(chapter,id)); return true;
    }
}
