package edu.persian.ui;

import edu.persian.repository.DataException;
import edu.persian.service.ValidationException;
import javafx.geometry.*;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

final class Ui {
    private Ui() { }
    static Button button(String text, Runnable action) {
        Button button = new Button(text); button.setOnAction(e -> action.run()); return button;
    }
    static Button primaryButton(String text, Runnable action) {
        Button button = button(text, action); button.getStyleClass().add("primary-button"); return button;
    }
    static Button dangerButton(String text, Runnable action) {
        Button button = button(text, action); button.getStyleClass().add("danger-button"); return button;
    }
    static Label title(String text) { Label l = new Label(text); l.getStyleClass().add("heading"); l.setWrapText(true); return l; }
    static VBox sectionCard(String text, int position) {
        Label index = new Label(String.format("%02d", position)); index.getStyleClass().add("section-index");
        Label heading = title(text); heading.getStyleClass().add("section-title");
        return card(row(index, heading));
    }
    private static VBox card(Node heading) { VBox card=box(heading); card.getStyleClass().add("card"); return card; }
    static Label message() { Label l=new Label(); l.setWrapText(true); l.getStyleClass().add("message"); return l; }
    static VBox box(Node... children) { VBox box=new VBox(14,children); box.setPadding(new Insets(22)); return box; }
    static HBox row(Node... children) { HBox row=new HBox(10,children); row.setAlignment(Pos.CENTER_LEFT); return row; }
    static void rtl(Node node) { node.setNodeOrientation(NodeOrientation.RIGHT_TO_LEFT); node.getStyleClass().add("rtl-text"); }
    static void leftEntry(TextInputControl field) {
        field.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
        field.getStyleClass().add("left-entry");
    }
    static boolean run(Label feedback, Runnable action) {
        try { action.run(); feedback.setText("Saved · محفوظ ہو گیا"); return true; }
        catch (ValidationException | DataException e) { feedback.setText(e.getMessage()); return false; }
    }
    static void guard(Label feedback, Runnable action) {
        try { action.run(); }
        catch (ValidationException | DataException e) { feedback.setText(e.getMessage()); }
    }
    static boolean confirm(Node owner, String text, String action) {
        ButtonType actionType=new ButtonType(action,ButtonBar.ButtonData.YES);
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().setAll(actionType,new ButtonType("Cancel · منسوخ",ButtonBar.ButtonData.CANCEL_CLOSE));
        alert.initOwner(owner.getScene().getWindow()); alert.setTitle(action); alert.setHeaderText(null);
        Label warning=new Label(text); warning.setWrapText(true); warning.setPrefWidth(500); warning.setMinHeight(92);
        alert.getDialogPane().setContent(warning); alert.getDialogPane().setMinWidth(600);
        alert.getDialogPane().getStylesheets().add(Ui.class.getResource("/styles.css").toExternalForm());
        alert.getDialogPane().lookupButton(actionType).getStyleClass().add("danger-button");
        return alert.showAndWait().map(b -> b==actionType).orElse(false);
    }
    static Dialog<Void> dialog(Node owner, String title, Node content) {
        Dialog<Void> dialog=new Dialog<>(); dialog.initOwner(owner.getScene().getWindow()); dialog.setTitle(title);
        dialog.getDialogPane().setContent(content); dialog.setResizable(true);
        dialog.getDialogPane().getStylesheets().add(Ui.class.getResource("/styles.css").toExternalForm());
        dialog.getDialogPane().getButtonTypes().add(new ButtonType("Close · بند کریں",ButtonBar.ButtonData.CANCEL_CLOSE));
        sizeButtons(dialog);
        return dialog;
    }
    static void saveDialog(Node owner, String title, VBox form, Label feedback, Runnable save) {
        Dialog<Void> dialog=dialog(owner,title,form);
        ButtonType saveType=new ButtonType("Save · محفوظ کریں",ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(saveType,new ButtonType("Cancel · منسوخ",ButtonBar.ButtonData.CANCEL_CLOSE));
        sizeButtons(dialog);
        dialog.getDialogPane().lookupButton(saveType).getStyleClass().add("primary-button");
        dialog.getDialogPane().lookupButton(saveType).addEventFilter(javafx.event.ActionEvent.ACTION,e -> {
            if (!run(feedback,save)) e.consume();
        });
        dialog.showAndWait();
    }
    private static void sizeButtons(Dialog<?> dialog) {
        for(ButtonType type:dialog.getDialogPane().getButtonTypes())
            ((Button)dialog.getDialogPane().lookupButton(type)).setMinWidth(Region.USE_PREF_SIZE);
    }
}
