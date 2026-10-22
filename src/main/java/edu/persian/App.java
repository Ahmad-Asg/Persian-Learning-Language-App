package edu.persian;

import edu.persian.application.LearningStudioFacade;
import edu.persian.repository.Database;
import edu.persian.ui.ChapterScreen;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

public final class App extends Application {
    @Override public void start(Stage stage) {
        try {
            LearningStudioFacade studio = LearningStudioFacade.create(Database.local());
            Scene scene = new Scene(new ChapterScreen(studio),1120,760);
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
