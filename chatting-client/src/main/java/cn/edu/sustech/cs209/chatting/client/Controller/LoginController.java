package cn.edu.sustech.cs209.chatting.client.Controller;

import cn.edu.sustech.cs209.chatting.client.Client;
import cn.edu.sustech.cs209.chatting.common.Request;
import cn.edu.sustech.cs209.chatting.common.RequestType;
import cn.edu.sustech.cs209.chatting.common.User;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LoginController extends Application {

    private static final LoginController loginController = new LoginController();

    public static LoginController getLoginController() {
        return loginController;
    }

    private PipedInputStream pis = new PipedInputStream();
    private PipedOutputStream pos = new PipedOutputStream();
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ObjectOutputStream getOos() {
        return oos;
    }

    @Override
    public void start(Stage stage) throws IOException {
        pis.connect(pos);
        oos = new ObjectOutputStream(pos);
        ois = new ObjectInputStream(pis);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setTitle("Login");
        stage.setOnCloseRequest(e -> {
            Client.getClient().close();
        });
        stage.show();
    }

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private VBox vBox;

    @FXML
    protected void onButtonLoginClick() {
        try {
            if (username.getText().equals("") || password.getText().equals("")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("输入错误");
                alert.setHeaderText("用户名或密码为空");
                alert.showAndWait();
                return;
            }
            Client.getClient().sendRequest(RequestType.Login, "Login Request",
                new User(username.getText(), password.getText()));
            Request request = (Request) loginController.ois.readObject();
            if (request.isSuccess()) {
                Client.getClient().setName(username.getText());
                new ChatController().start(new Stage());
                vBox.getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("登录失败");
                alert.setHeaderText("登录失败: " + request.getInfo());
                alert.showAndWait();
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Stage SignUpStage = null;

    @FXML
    void onButtonSignupClick() {
        try {
            if (SignUpStage == null) {
                SignUpStage = new Stage();
                SignUpController.getSignUpController().start(SignUpStage);
            } else {
                SignUpStage.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

