package learn.javafx;

import java.net.URL;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.control.Label;

public class Login extends Application {

    Stage windows;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        windows = primaryStage;
        windows.setTitle("Login");

        VBox layout = new VBox();
        layout.setPadding(new Insets(50, 50, 50, 50));
        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);

        HBox layout1 = new HBox();
        layout1.setPadding(new Insets(50, 50, 50, 50));
        layout1.setSpacing(20);
        layout1.setAlignment(Pos.CENTER);

        Label lb = new Label("登录");
        lb.setId("bold-label");

        TextField tf = new TextField();
        tf.setPromptText("请输入用户名");
        tf.setMinWidth(90);
        PasswordField pf = new PasswordField();
        pf.setPromptText("请输入密码");
        pf.setMinWidth(90);

        Button bt = new Button("Login");
        Button bt1 = new Button("Sign up");
        bt1.getStyleClass().add("button-blue");

        layout1.getChildren().addAll(bt1, bt);
//        bt.setMinWidth(120);
        layout.getChildren().addAll(lb, tf, pf, layout1);
        Scene sne = new Scene(layout, 500, 300);
        windows.setScene(sne);
//        sne.getStylesheets().add(Login.class.getClassLoader().getResource("Viper.css").toExternalForm());

        windows.show();
    }
}

