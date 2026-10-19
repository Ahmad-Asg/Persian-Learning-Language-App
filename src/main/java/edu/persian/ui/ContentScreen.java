package edu.persian.ui;

import edu.persian.model.ComponentType;
import edu.persian.service.ContentService;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public final class ContentScreen {
    private ContentScreen() { }
    public static void open(Node owner,long chapter,ComponentType type,ContentService service,Runnable refresh) {
        TextArea editor=new TextArea(service.get(chapter,type)); editor.setWrapText(true); editor.setPrefRowCount(15); Ui.leftEntry(editor);
        Label feedback=Ui.message();
        VBox form=Ui.box(Ui.title(type.toString()),new Label("Instructional text · تدریسی متن"),editor,
            new Label("Save needs text. Use Remove content on the chapter screen to clear it.\nمحفوظ کرنے کے لیے متن لکھیں۔ متن حذف کرنے کے لیے باب کے صفحے پر جائیں۔"),feedback);
        form.setPrefWidth(720); VBox.setVgrow(editor,javafx.scene.layout.Priority.ALWAYS);
        Ui.saveDialog(owner,"Content editor · متن کی تدوین",form,feedback,() -> { service.save(chapter,type,editor.getText()); refresh.run(); });
    }
}
