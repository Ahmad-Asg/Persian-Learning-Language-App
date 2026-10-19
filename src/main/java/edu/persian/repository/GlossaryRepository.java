package edu.persian.repository;

import edu.persian.model.GlossaryEntry;
import java.util.*;
import java.sql.Statement;

public final class GlossaryRepository {
    private final Database db;
    public GlossaryRepository(Database db) { this.db=db; }
    public List<GlossaryEntry> list(long chapter) {
        return db.read(c -> {
            List<GlossaryEntry> result = new ArrayList<>();
            try (var s = c.prepareStatement("SELECT id,word,meaning FROM glossary WHERE chapter_id=? ORDER BY id")) {
                s.setLong(1,chapter);
                try (var r = s.executeQuery()) { while(r.next()) result.add(new GlossaryEntry(r.getLong(1),chapter,r.getString(2),r.getString(3))); }
            }
            return result;
        });
    }
    public GlossaryEntry create(long chapter, String word, String meaning) {
        return db.transaction(c -> {
            try (var s = c.prepareStatement("INSERT INTO glossary(chapter_id,word,meaning) VALUES (?,?,?)", Statement.RETURN_GENERATED_KEYS)) {
                s.setLong(1,chapter); s.setString(2,word); s.setString(3,meaning); s.executeUpdate();
                try (var r = s.getGeneratedKeys()) { if (!r.next()) throw new java.sql.SQLException("Missing generated glossary ID"); return new GlossaryEntry(r.getLong(1),chapter,word,meaning); }
            }
        });
    }
    public boolean update(long chapter, long id, String word, String meaning) {
        return db.read(c -> {
            try (var s = c.prepareStatement("UPDATE glossary SET word=?,meaning=? WHERE id=? AND chapter_id=?")) {
                s.setString(1,word); s.setString(2,meaning); s.setLong(3,id); s.setLong(4,chapter); return s.executeUpdate()==1;
            }
        });
    }
    public boolean delete(long chapter, long id) {
        return db.read(c -> {
            try (var s = c.prepareStatement("DELETE FROM glossary WHERE id=? AND chapter_id=?")) { s.setLong(1,id); s.setLong(2,chapter); return s.executeUpdate()==1; }
        });
    }
}
