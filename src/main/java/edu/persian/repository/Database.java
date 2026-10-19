package edu.persian.repository;

import java.io.IOException;
import java.nio.file.*;
import java.sql.*;
import java.util.Properties;

/** Short-lived MariaDB JDBC connections and idempotent schema setup. */
public final class Database {
    private final String url;
    private final String user;
    private final String password;

    public static Database local() {
        Path configuration = Path.of(System.getProperty("persian.config", Path.of("config", "local.properties").toString()));
        if (!Files.isRegularFile(configuration)) {
            throw new IllegalStateException("Database setup is missing. Copy config/local.properties.example to config/local.properties and follow the README. · ڈیٹابیس کی ترتیبات موجود نہیں ہیں۔");
        }
        Properties properties = new Properties();
        try (var input = Files.newInputStream(configuration)) { properties.load(input); }
        catch (IOException e) { throw new DataException(e); }
        return new Database(properties.getProperty("db.url", ""), properties.getProperty("db.user", ""), properties.getProperty("db.password", ""));
    }

    public Database(String url, String user, String password) {
        if (url == null || !url.startsWith("jdbc:mariadb://") || user == null || user.isBlank() || password == null) {
            throw new IllegalArgumentException("Set a MariaDB JDBC URL, user and password in the local configuration.");
        }
        this.url = url;
        this.user = user;
        this.password = password;
        read(c -> {
            try (Statement s = c.createStatement()) {
                s.executeUpdate("CREATE TABLE IF NOT EXISTS chapters (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, title TEXT NOT NULL, CONSTRAINT chapters_title_required CHECK (CHAR_LENGTH(TRIM(title)) > 0)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin");
                s.executeUpdate("CREATE TABLE IF NOT EXISTS components (chapter_id BIGINT NOT NULL, type VARCHAR(20) NOT NULL, position INT NULL, content LONGTEXT NOT NULL, PRIMARY KEY(chapter_id,type), UNIQUE KEY chapter_position(chapter_id,position), CONSTRAINT components_chapter_fk FOREIGN KEY(chapter_id) REFERENCES chapters(id) ON DELETE CASCADE, CONSTRAINT component_type_supported CHECK (type IN ('OBJECTIVES','DIALOGUE','READING','TRANSLATION','GRAMMAR','EXAMPLES','GLOSSARY')), CONSTRAINT component_position_valid CHECK (position IS NULL OR position >= 0)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin");
                s.executeUpdate("CREATE TABLE IF NOT EXISTS glossary (id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, chapter_id BIGINT NOT NULL, word TEXT NOT NULL, meaning TEXT NOT NULL, KEY glossary_chapter(chapter_id), CONSTRAINT glossary_chapter_fk FOREIGN KEY(chapter_id) REFERENCES chapters(id) ON DELETE CASCADE, CONSTRAINT glossary_word_required CHECK (CHAR_LENGTH(TRIM(word)) > 0), CONSTRAINT glossary_meaning_required CHECK (CHAR_LENGTH(TRIM(meaning)) > 0)) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_bin");
            }
            return null;
        });
    }

    public Connection open() throws SQLException { return DriverManager.getConnection(url, user, password); }
    @FunctionalInterface public interface Work<T> { T run(Connection connection) throws SQLException; }
    public <T> T read(Work<T> work) {
        try (Connection c = open()) { return work.run(c); }
        catch (SQLException e) { throw new DataException(e); }
    }
    public <T> T transaction(Work<T> work) {
        return read(c -> {
            c.setAutoCommit(false);
            try { T result = work.run(c); c.commit(); return result; }
            catch (SQLException | RuntimeException e) { c.rollback(); throw e; }
        });
    }
}
