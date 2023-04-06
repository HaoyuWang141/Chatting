package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.client.Controller.ConnectController;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        new ConnectController().start(new Stage());
    }
}
