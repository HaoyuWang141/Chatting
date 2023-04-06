package learn.javafx;


import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MainPane extends Application {

    @Override
    public void start(Stage primaryStage) {
        Button btn = new Button("点击跳转");

        // 对按钮事件编程，就是new一个C1的对象，然后调用C1的start方法，就可以打开C1窗口
        btn.setOnAction((ActionEvent event) -> {
            C1 open = new C1();
            open.start(new Stage());
            //stage.hide(); //点开新的界面后，是否关闭此界面
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        Scene scene = new Scene(root, 200, 200);
        primaryStage.setTitle("主界面");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    //点击后，要打开的窗口的主要代码
    private class C1 extends Application {

        @Override
        public void start(Stage primaryStage) {
            StackPane root = new StackPane();
            Scene scene = new Scene(root, 400, 400);
            primaryStage.setTitle("界面2");
            primaryStage.setScene(scene);
            primaryStage.show();
        }
    }
}

