package edu.persian;

import edu.persian.repository.*;
import edu.persian.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.UUID;

abstract class ServiceTestBase {
    String schema;
    String baseUrl = System.getProperty("persian.test.url", "jdbc:mariadb://127.0.0.1:3306/");
    String testUser = System.getProperty("persian.test.user", "root");
    String testPassword = System.getProperty("persian.test.password", "");
    Database db;
    ChapterService chapters;
    StructureService structure;
    ContentService content;
    GlossaryService glossary;
    @BeforeEach void setup() throws SQLException {
        schema = "persian_test_" + UUID.randomUUID().toString().replace("-", "");
        try (var c = DriverManager.getConnection(baseUrl, testUser, testPassword); var s = c.createStatement()) {
            s.executeUpdate("CREATE DATABASE `" + schema + "` CHARACTER SET utf8mb4 COLLATE utf8mb4_bin");
        }
        reopen();
    }
    @AfterEach void cleanup() throws SQLException {
        if (schema != null) try (var c = DriverManager.getConnection(baseUrl, testUser, testPassword); var s = c.createStatement()) {
            s.executeUpdate("DROP DATABASE `" + schema + "`");
        }
    }
    void reopen() {
        db=new Database(baseUrl + schema, testUser, testPassword);
        chapters=new ChapterService(new ChapterRepository(db));
        structure=new StructureService(chapters,new StructureRepository(db));
        glossary=new GlossaryService(chapters,new GlossaryRepository(db));
        content=new ContentService(new ContentRepository(db),structure,glossary);
    }
    long chapter() { return chapters.create("درس ۱ — سلام").id(); }
}
