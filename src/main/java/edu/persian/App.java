package edu.persian;

import edu.persian.repository.*;
import edu.persian.service.*;
import edu.persian.ui.ChapterScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public final class App extends Application {
    @Override public void start(Stage stage) {
        try {
            Database db = Database.local();
            ChapterService chapters = new ChapterService(new ChapterRepository(db));
            ComponentRepository components = new ComponentRepository(db);
            StructureService structure = new StructureService(chapters,components);
            GlossaryService glossary = new GlossaryService(chapters,new GlossaryRepository(db));
            ContentService content = new ContentService(components,structure,glossary);
            Scene scene = new Scene(new ChapterScreen(chapters,structure,content,glossary),1120,760);
            scene.getStylesheets().add(App.class.getResource("/styles.css").toExternalForm());
            stage.setTitle("Persian Learning Studio · فارسی زبان");
            stage.setMinWidth(900); stage.setMinHeight(620); stage.setScene(scene); stage.show();
        } catch (RuntimeException e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Cannot open the app. Start MySQL in XAMPP and check config/local.properties. · ایپ نہیں کھل سکی۔\n"+e.getMessage()).showAndWait();
        }
    }
    public static void main(String[] args) { launch(args); }
}
