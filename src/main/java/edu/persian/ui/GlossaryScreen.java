package edu.persian.ui;

import edu.persian.model.GlossaryEntry;
import edu.persian.service.GlossaryService;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public final class GlossaryScreen {
    private GlossaryScreen() { }
    public static void open(Node owner,long chapter,GlossaryService service,Runnable changed) {
        ListView<GlossaryEntry> entries=new ListView<>(); Ui.rtl(entries); entries.setPrefHeight(300);
        entries.setPlaceholder(new Label("No words yet · ابھی کوئی لفظ نہیں"));
        Label feedback=Ui.message();
        Runnable refresh=() -> { entries.getItems().setAll(service.list(chapter)); changed.run(); };
        refresh.run();
        Button edit=Ui.button("Edit · ترمیم",() -> edit(entries,chapter,entries.getSelectionModel().getSelectedItem(),service,refresh));
        Button delete=Ui.button("Delete · حذف",() -> {
            GlossaryEntry entry=entries.getSelectionModel().getSelectedItem();
            if(entry!=null && Ui.confirm(entries,"Delete this word? · یہ لفظ حذف کریں؟\n"+entry.word()))
                Ui.run(feedback,() -> { service.delete(chapter,entry.id(),true); refresh.run(); });
        });
        edit.disableProperty().bind(entries.getSelectionModel().selectedItemProperty().isNull()); delete.disableProperty().bind(edit.disableProperty());
        VBox form=Ui.box(Ui.title("Chapter glossary · باب کی فرہنگ"),new Label("Persian word — Urdu meaning · فارسی لفظ — اردو معنی"),entries,
                Ui.row(Ui.button("Add word · لفظ شامل کریں",() -> edit(entries,chapter,null,service,refresh)),edit,delete),feedback);
        form.setPrefWidth(730); Ui.dialog(owner,"Glossary · فرہنگ",form).showAndWait();
    }
    private static void edit(Node owner,long chapter,GlossaryEntry entry,GlossaryService service,Runnable refresh) {
        TextField word=new TextField(entry==null ? "" : entry.word()); Ui.leftEntry(word);
        TextArea meaning=new TextArea(entry==null ? "" : entry.meaning()); Ui.leftEntry(meaning); meaning.setWrapText(true); meaning.setPrefRowCount(4);
        Label feedback=Ui.message();
        VBox form=Ui.box(new Label("Persian word · فارسی لفظ"),word,new Label("Urdu meaning · اردو معنی"),meaning,feedback); form.setPrefWidth(600);
        Ui.saveDialog(owner,"Glossary entry · فرہنگ کا اندراج",form,feedback,() -> {
            if(entry==null) service.add(chapter,word.getText(),meaning.getText());
            else service.edit(chapter,entry.id(),word.getText(),meaning.getText());
            refresh.run();
        });
    }
}
