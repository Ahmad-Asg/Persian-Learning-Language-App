package edu.persian.ui;

import edu.persian.application.LearningStudioFacade;
import edu.persian.model.ComponentType;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.util.ArrayList;

public final class StructureScreen {
    private StructureScreen() { }
    public static void open(Node owner,long chapter,LearningStudioFacade studio,Runnable refresh) {
        ListView<ComponentType> selected=new ListView<>(); selected.getItems().setAll(studio.structure(chapter)); selected.setPrefHeight(270);
        ComboBox<ComponentType> available=new ComboBox<>(); available.getItems().setAll(ComponentType.values()); available.getSelectionModel().selectFirst(); available.setMaxWidth(Double.MAX_VALUE);
        Label feedback=Ui.message();
        Button add=Ui.button("Add section · جزو شامل کریں",() -> {
            ComponentType type=available.getValue();
            if (!selected.getItems().contains(type)) { selected.getItems().add(type); selected.getSelectionModel().select(type); feedback.setText(""); }
            else feedback.setText("Already selected · پہلے سے منتخب ہے");
        });
        Button remove=Ui.button("Deselect · ہٹائیں",() -> { int i=selected.getSelectionModel().getSelectedIndex(); if(i>=0) selected.getItems().remove(i); });
        VBox form=Ui.box(Ui.title("Chapter structure · باب کی ترتیب"),new Label("Choose sections, then arrange their reading order. · اجزا منتخب کرکے ترتیب دیں۔"),available,add,selected,
                Ui.row(Ui.button("↑ Up · اوپر",() -> move(selected,-1)),Ui.button("↓ Down · نیچے",() -> move(selected,1)),remove),
                new Label("Deselected text is retained for reuse. · ہٹائے گئے جزو کا متن محفوظ رہے گا۔"),feedback);
        form.setPrefWidth(650);
        Ui.saveDialog(owner,"Structure · ترتیب",form,feedback,() -> { studio.saveStructure(chapter,new ArrayList<>(selected.getItems())); refresh.run(); });
    }
    private static void move(ListView<ComponentType> list,int offset) {
        int index=list.getSelectionModel().getSelectedIndex(), target=index+offset;
        if(index<0 || target<0 || target>=list.getItems().size()) return;
        ComponentType type=list.getItems().remove(index); list.getItems().add(target,type); list.getSelectionModel().select(target);
    }
}
