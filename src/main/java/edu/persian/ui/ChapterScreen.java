package edu.persian.ui;

import edu.persian.application.LearningStudioFacade;
import edu.persian.model.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public final class ChapterScreen extends BorderPane {
    private final LearningStudioFacade studio;
    private final ListView<Chapter> list=new ListView<>();
    private final VBox detail=Ui.box();
    private final Label feedback=Ui.message();

    public ChapterScreen(LearningStudioFacade studio) {
        this.studio=java.util.Objects.requireNonNull(studio,"studio");
        Label brand=new Label("PERSIAN  /  LEARNING STUDIO"); brand.getStyleClass().add("brand");
        Label subtitle=new Label("A quiet workspace for building Persian lessons · فارسی زبان"); subtitle.getStyleClass().add("banner-subtitle");
        VBox masthead=new VBox(5,brand,subtitle); masthead.getStyleClass().add("masthead");
        Label edition=new Label("ITERATION 01  •  ADMIN"); edition.getStyleClass().add("edition");
        BorderPane banner=new BorderPane(); banner.setLeft(masthead); banner.setRight(edition); BorderPane.setAlignment(edition,Pos.CENTER_RIGHT); banner.getStyleClass().add("banner"); setTop(banner);
        list.setPlaceholder(new Label("No chapters yet\nپہلا باب بنائیں"));
        list.setCellFactory(view -> new ListCell<>() {
            @Override protected void updateItem(Chapter chapter, boolean empty) {
                super.updateItem(chapter,empty);
                if(empty || chapter==null) { setText(null); setGraphic(null); return; }
                Label name=new Label(chapter.title()); name.setWrapText(true); name.getStyleClass().add("chapter-name");
                Label number=new Label("CHAPTER  "+String.format("%02d",chapter.id())); number.getStyleClass().add("chapter-number");
                VBox item=new VBox(3,name,number); setGraphic(item); setText(null);
            }
        });
        list.getSelectionModel().selectedItemProperty().addListener((o,old,value) -> Ui.guard(feedback,this::render));
        Label sidebarTitle=Ui.title("Chapters · ابواب");
        Label sidebarHint=new Label("Organize your lesson library"); sidebarHint.getStyleClass().add("muted");
        Button create=Ui.primaryButton("+ New chapter · نیا باب",() -> editTitle(null)); create.setMaxWidth(Double.MAX_VALUE);
        VBox sidebar=new VBox(8,sidebarTitle,sidebarHint,create,list); sidebar.getStyleClass().add("sidebar");
        VBox.setMargin(create,new Insets(16,0,8,0));
        sidebar.setPrefWidth(300); VBox.setVgrow(list,Priority.ALWAYS); setLeft(sidebar);
        detail.getStyleClass().add("detail");
        ScrollPane scroll=new ScrollPane(detail); scroll.setFitToWidth(true); scroll.getStyleClass().add("content-scroll"); setCenter(scroll);
        HBox footer=new HBox(feedback); footer.getStyleClass().add("footer"); setBottom(footer);
        reload(null);
    }
    private void reload(Long selected) {
        list.getItems().setAll(studio.listChapters());
        if(selected!=null) list.getItems().stream().filter(c -> c.id()==selected).findFirst().ifPresent(c -> list.getSelectionModel().select(c));
        render();
    }
    private void editTitle(Chapter chapter) {
        TextField title=new TextField(chapter==null ? "" : chapter.title()); Ui.leftEntry(title);
        title.setPromptText("Enter a chapter title");
        Label error=Ui.message(); VBox form=Ui.box(new Label("Chapter title · باب کا عنوان"),title,error); form.setPrefWidth(560);
        Ui.saveDialog(this,chapter==null ? "New chapter · نیا باب" : "Rename chapter · عنوان بدلیں",form,error,() -> {
            long id;
            if(chapter==null) id=studio.createChapter(title.getText()).id();
            else { studio.renameChapter(chapter.id(),title.getText()); id=chapter.id(); }
            reload(id);
        });
    }
    private void render() {
        detail.getChildren().clear(); Chapter selected=list.getSelectionModel().getSelectedItem();
        if(selected==null) {
            Label eyebrow=new Label("YOUR WORKSPACE"); eyebrow.getStyleClass().add("eyebrow");
            Label welcome=Ui.title("Build a lesson, one chapter at a time"); welcome.getStyleClass().add("hero-title");
            Label help=new Label("Create a chapter on the left, then add sections, write content and collect Persian–Urdu vocabulary.\nبائیں طرف نیا باب بنائیں، پھر اجزا، متن اور الفاظ شامل کریں۔"); help.setWrapText(true); help.getStyleClass().add("hero-copy");
            VBox empty=Ui.box(eyebrow,welcome,help,Ui.primaryButton("+ Create your first chapter",() -> editTitle(null))); empty.getStyleClass().add("empty-state");
            detail.getChildren().add(empty); return;
        }
        long id=selected.id(); Chapter chapter=studio.chapter(id);
        Label eyebrow=new Label("CHAPTER  "+String.format("%02d",id)); eyebrow.getStyleClass().add("eyebrow");
        Label title=Ui.title(chapter.title()); title.getStyleClass().add("chapter-title");
        boolean complete=studio.isComplete(id);
        Label status=new Label(complete ? "●  Complete · مکمل" : "●  Draft · نامکمل"); status.getStyleClass().addAll("status",complete ? "complete" : "draft");
        HBox titleRow=new HBox(18,title,status); titleRow.setAlignment(Pos.CENTER_LEFT); HBox.setHgrow(title,Priority.ALWAYS);
        Label guidance=new Label(complete ? "Every selected section has content." : "Add content to each selected section to complete this chapter."); guidance.getStyleClass().add("muted");
        FlowPane actions=new FlowPane(8,8);
        actions.getChildren().addAll(Ui.button("Rename · عنوان",() -> editTitle(chapter)),
            Ui.primaryButton("Structure · ترتیب",() -> Ui.guard(feedback,() -> StructureScreen.open(this,id,studio,this::render))),
            Ui.button("Glossary · فرہنگ",() -> Ui.guard(feedback,() -> GlossaryScreen.open(this,id,studio,this::render))),
            Ui.dangerButton("Delete chapter · باب حذف",() -> {
                if(Ui.confirm(this,"Delete ‘"+chapter.title()+"’? All its components, saved text and glossary entries will also be deleted.\nباب، تمام اجزا، محفوظ متن اور فرہنگ کے اندراجات بھی حذف ہوں گے۔"))
                    Ui.run(feedback,() -> { studio.deleteChapter(id,true); reload(null); });
            }));
        VBox header=new VBox(12,eyebrow,titleRow,guidance,actions); header.getStyleClass().add("chapter-header");
        detail.getChildren().add(header);
        var types=studio.structure(id);
        if(types.isEmpty()) {
            Label hint=new Label("No sections yet. Choose Structure above to plan this chapter.\nابھی کوئی جزو نہیں۔ اوپر ترتیب پر کلک کریں۔"); hint.setWrapText(true); hint.getStyleClass().add("empty-section"); detail.getChildren().add(hint);
        } else {
            Label sections=new Label("LESSON SECTIONS  /  "+types.size()); sections.getStyleClass().add("eyebrow"); detail.getChildren().add(sections);
        }
        int position=0;
        for(ComponentType type:types) {
            Label index=new Label(String.format("%02d",++position)); index.getStyleClass().add("section-index");
            Label sectionTitle=Ui.title(type.toString()); sectionTitle.getStyleClass().add("section-title");
            HBox cardHeading=new HBox(12,index,sectionTitle); cardHeading.setAlignment(Pos.CENTER_LEFT);
            VBox card=Ui.box(cardHeading); card.getStyleClass().add("card");
            if(type==ComponentType.GLOSSARY) {
                Label words=new Label(studio.glossary(id).stream().map(GlossaryEntry::toString).reduce((a,b) -> a+"\n"+b).orElse("No entries · کوئی اندراج نہیں")); words.setWrapText(true); Ui.rtl(words);
                card.getChildren().addAll(words,Ui.button("Manage glossary · فرہنگ کی تدوین",() -> Ui.guard(feedback,() -> GlossaryScreen.open(this,id,studio,this::render))));
            } else {
                String saved=studio.content(id,type);
                Label preview=new Label(saved.isEmpty() ? "No content yet · ابھی متن موجود نہیں" : saved); preview.setWrapText(true); preview.setMaxWidth(Double.MAX_VALUE); Ui.rtl(preview); preview.getStyleClass().add(saved.isEmpty() ? "empty-preview" : "content-preview");
                card.getChildren().addAll(preview,Ui.row(Ui.button("Edit content · متن لکھیں",() -> Ui.guard(feedback,() -> ContentScreen.open(this,id,type,studio,this::render))),
                    Ui.button("Remove content · متن حذف",() -> {
                        if(Ui.confirm(this,"Remove this section's saved text? The chapter will become incomplete.\nاس جزو کا متن حذف کریں؟ باب نامکمل ہو جائے گا۔"))
                            Ui.run(feedback,() -> { studio.removeContent(id,type); render(); });
                    })));
            }
            detail.getChildren().add(card);
        }
    }
}
