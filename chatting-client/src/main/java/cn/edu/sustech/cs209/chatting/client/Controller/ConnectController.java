package cn.edu.sustech.cs209.chatting.client.Controller;

import cn.edu.sustech.cs209.chatting.client.Client;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class ConnectController extends Application {

    private static final ConnectController connectController = new ConnectController();

    public static ConnectController getConnectController() {
        return connectController;
    }

    @FXML
    private TextField host;
    @FXML
    private TextField StringPort;
    @FXML
    private VBox vBox;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Connect.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setTitle("Connect");
        stage.show();
    }

    @FXML
    private void onButtonConnectClick() {
        int port;
        try {
            System.out.println(host.getText() + "/" + StringPort.getText());
            port = Integer.parseInt(StringPort.getText());
            Client.Connect(host.getText(), port);
            LoginController.getLoginController().start(new Stage());
            vBox.getScene().getWindow().hide();
        } catch (NumberFormatException e) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("输入错误");
            alert.setHeaderText("端口号不是数字");
            alert.showAndWait();
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("连接错误");
            alert.setHeaderText("连接主机失败");
            alert.showAndWait();
        }
    }
}
