package learn.javafx;

import java.util.TreeMap;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application{

    public static void main(String[] args) throws IOException {
        launch();
//        new HelloController().start(new Stage());
    }

    @Override
    public void start(Stage stage) throws Exception {
        MainPane mainPane = new MainPane();
        mainPane.start(new Stage());
    }
}