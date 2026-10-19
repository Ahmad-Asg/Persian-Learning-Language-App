package edu.persian;

import edu.persian.model.ComponentType;
import edu.persian.repository.*;
import edu.persian.service.*;
import edu.persian.ui.ChapterScreen;
import javafx.application.Platform;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.stage.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.nio.file.*;
import java.sql.DriverManager;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/** Optional real-window smoke exercise. Not part of the headless JUnit suite. */
public final class UiSmoke {
    private static Throwable failure;
    private static Path output;
    public static void main(String[] args) throws Exception {
        output=Path.of("target","ui-smoke"); Files.createDirectories(output);
        String schema="persian_smoke_"+UUID.randomUUID().toString().replace("-","");
        String baseUrl=System.getProperty("persian.test.url","jdbc:mariadb://127.0.0.1:3306/");
        String user=System.getProperty("persian.test.user","root"), password=System.getProperty("persian.test.password","");
        try(var c=DriverManager.getConnection(baseUrl,user,password);var s=c.createStatement()) { s.executeUpdate("CREATE DATABASE `"+schema+"` CHARACTER SET utf8mb4 COLLATE utf8mb4_bin"); }
        Path config=Files.createTempFile(output,"smoke-",".properties");
        Properties properties=new Properties(); properties.setProperty("db.url",baseUrl+schema); properties.setProperty("db.user",user); properties.setProperty("db.password",password);
        try(var writer=Files.newOutputStream(config)) { properties.store(writer,"Isolated UI smoke database"); }
        System.setProperty("persian.config",config.toString());
        CountDownLatch finished=new CountDownLatch(1);
        Platform.startup(() -> { }); Platform.setImplicitExit(false);
        Platform.runLater(() -> {
            try { exercise(baseUrl+schema,user,password); }
            catch(Throwable e) { failure=e; e.printStackTrace(); }
            finally { for(Window w:List.copyOf(Window.getWindows())) w.hide(); finished.countDown(); }
        });
        if(!finished.await(45,TimeUnit.SECONDS)) { System.err.println("UI smoke timed out"); System.exit(1); }
        Platform.exit();
        try(var c=DriverManager.getConnection(baseUrl,user,password);var s=c.createStatement()) { s.executeUpdate("DROP DATABASE `"+schema+"`"); }
        Files.deleteIfExists(config);
        if(failure!=null) System.exit(1);
        System.out.println("UI SMOKE PASSED: chapter creation, structure, content, glossary, cancellation, deletion and App startup.");
    }
    private static void exercise(String url,String user,String password) throws Exception {
        Database db=new Database(url,user,password); ChapterService chapters=new ChapterService(new ChapterRepository(db));
        ComponentRepository repo=new ComponentRepository(db); StructureService structure=new StructureService(chapters,repo);
        GlossaryService glossary=new GlossaryService(chapters,new GlossaryRepository(db)); ContentService content=new ContentService(repo,structure,glossary);
        ChapterScreen root=new ChapterScreen(chapters,structure,content,glossary); Scene scene=new Scene(root,1120,760);
        scene.getStylesheets().add(App.class.getResource("/styles.css").toExternalForm()); Stage stage=new Stage(); stage.setScene(scene); stage.show();
        nextDialog(form -> {
            click(form,"Save"); require(form.getScene().getWindow().isShowing(),"Invalid title closed dialog");
            ((TextField)find(form,TextField.class)).setText("درس ۱ — سلام اور تعارف"); click(form,"Save");
        }); click(root,"+ New chapter"); require(chapters.list().size()==1,"Chapter not created"); long id=chapters.list().getFirst().id();
        nextDialog(form -> {
            @SuppressWarnings("unchecked") ComboBox<ComponentType> combo=(ComboBox<ComponentType>)find(form,ComboBox.class);
            combo.setValue(ComponentType.DIALOGUE); click(form,"Add section"); combo.setValue(ComponentType.GLOSSARY); click(form,"Add section");
            snapshot(form,"structure.png"); click(form,"Save");
        }); click(root,"Structure"); require(structure.get(id).size()==2,"Structure not saved");
        nextDialog(form -> {
            ((TextArea)find(form,TextArea.class)).setText("سلام! حال شما چطور است؟\nآپ کا کیا حال ہے؟\nLesson 2: ۱۲۳ (45) — می‌روم۔"); snapshot(form,"content.png"); click(form,"Save");
        }); click(root,"Edit content"); require(content.get(id,ComponentType.DIALOGUE).contains("۱۲۳"),"Content not saved");
        nextDialog(form -> {
            nextDialog(entry -> {
                ((TextField)find(entry,TextField.class)).setText("دوست"); ((TextArea)find(entry,TextArea.class)).setText("دوست، ساتھی"); click(entry,"Save");
            }); click(form,"Add word"); snapshot(form,"glossary.png"); click(form,"Close");
        }); click(root,"Glossary"); require(glossary.list(id).size()==1,"Glossary not saved"); require(content.complete(id),"Completeness not updated");
        snapshot(root,"chapter.png"); stage.setWidth(900); stage.setHeight(620); root.resize(900,620); snapshot(root,"compact.png");
        nextDialog(form -> click(form,"Cancel")); click(root,"Delete chapter"); require(chapters.list().size()==1,"Cancelled deletion removed chapter");
        nextDialog(form -> click(form,"Confirm")); click(root,"Delete chapter"); require(chapters.list().isEmpty(),"Confirmed deletion failed");
        stage.close(); Stage actual=new Stage(); new App().start(actual); require(actual.isShowing(),"App did not start"); actual.close();
    }
    private static void nextDialog(Consumer<Parent> action) {
        Platform.runLater(() -> {
            try {
                Window window=Window.getWindows().stream().filter(Window::isShowing).reduce((a,b) -> b).orElseThrow();
                Parent root=window.getScene().getRoot(); root.applyCss(); root.layout(); action.accept(root);
            } catch(Throwable e) { failure=e; e.printStackTrace(); for(Window w:List.copyOf(Window.getWindows())) w.hide(); }
        });
    }
    private static List<Node> nodes(Node root) {
        List<Node> result=new ArrayList<>(); result.add(root);
        if(root instanceof Parent parent) for(Node child:parent.getChildrenUnmodifiable()) result.addAll(nodes(child));
        return result;
    }
    private static Node find(Parent root,Class<?> type) { root.applyCss(); root.layout(); return nodes(root).stream().filter(type::isInstance).findFirst().orElseThrow(); }
    private static void click(Parent root,String prefix) {
        root.applyCss(); root.layout();
        Button button=(Button)nodes(root).stream().filter(n -> n instanceof Button b && b.getText().startsWith(prefix)).findFirst().orElseThrow(() -> new AssertionError("Missing button: "+prefix)); button.fire();
        if(failure!=null) throw new AssertionError(failure);
    }
    private static void require(boolean condition,String message) { if(!condition) throw new AssertionError(message); }
    private static void snapshot(Parent root,String name) {
        try {
            root.applyCss(); root.layout(); var image=root.snapshot(null,null); int w=(int)image.getWidth(),h=(int)image.getHeight();
            BufferedImage bitmap=new BufferedImage(w,h,BufferedImage.TYPE_INT_ARGB);
            for(int y=0;y<h;y++) for(int x=0;x<w;x++) bitmap.setRGB(x,y,image.getPixelReader().getArgb(x,y));
            ImageIO.write(bitmap,"png",output.resolve(name).toFile());
        } catch(Exception e) { throw new RuntimeException(e); }
    }
}
