package edu.persian.repository;

import edu.persian.model.ComponentType;
import java.util.*;

public final class ComponentRepository {
    private final Database db;
    public ComponentRepository(Database db) { this.db = db; }
    public List<ComponentType> selected(long chapter) {
        return db.read(c -> {
            List<ComponentType> result = new ArrayList<>();
            try (var s = c.prepareStatement("SELECT type FROM components WHERE chapter_id=? AND position IS NOT NULL ORDER BY position")) {
                s.setLong(1,chapter);
                try (var r = s.executeQuery()) { while(r.next()) result.add(ComponentType.valueOf(r.getString(1))); }
            }
            return result;
        });
    }
    public void configure(long chapter, List<ComponentType> types) {
        db.transaction(c -> {
            // NULL positions retain deselected content and avoid unique-position conflicts during reorder.
            try (var s = c.prepareStatement("UPDATE components SET position=NULL WHERE chapter_id=?")) { s.setLong(1,chapter); s.executeUpdate(); }
            try (var s = c.prepareStatement("INSERT INTO components(chapter_id,type,position,content) VALUES (?,?,?,'') ON DUPLICATE KEY UPDATE position=VALUES(position)")) {
                for (int i=0;i<types.size();i++) { s.setLong(1,chapter); s.setString(2,types.get(i).name()); s.setInt(3,i); s.addBatch(); }
                s.executeBatch();
            }
            return null;
        });
    }
    public Optional<String> content(long chapter, ComponentType type) {
        return db.read(c -> {
            try (var s = c.prepareStatement("SELECT content FROM components WHERE chapter_id=? AND type=? AND position IS NOT NULL")) {
                s.setLong(1,chapter); s.setString(2,type.name());
                try (var r = s.executeQuery()) { return r.next() ? Optional.of(r.getString(1)) : Optional.empty(); }
            }
        });
    }
    public boolean saveContent(long chapter, ComponentType type, String text) {
        return db.read(c -> {
            try (var s = c.prepareStatement("UPDATE components SET content=? WHERE chapter_id=? AND type=? AND position IS NOT NULL")) {
                s.setString(1,text); s.setLong(2,chapter); s.setString(3,type.name()); return s.executeUpdate()==1;
            }
        });
    }
}
