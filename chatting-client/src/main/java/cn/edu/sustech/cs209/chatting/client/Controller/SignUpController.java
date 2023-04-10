package cn.edu.sustech.cs209.chatting.client.Controller;

import cn.edu.sustech.cs209.chatting.client.Client;
import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.Request;
import cn.edu.sustech.cs209.chatting.common.RequestType;
import cn.edu.sustech.cs209.chatting.common.User;
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

public class SignUpController extends Application {

    private static final SignUpController signUpController = new SignUpController();

    public static SignUpController getSignUpController() {
        return signUpController;
    }

    private PipedInputStream pis = new PipedInputStream();
    private PipedOutputStream pos = new PipedOutputStream();
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    public ObjectOutputStream getOos() {
        return oos;
    }

    @Override
    public void start(Stage stage) throws Exception {
        pis.connect(pos);
        oos = new ObjectOutputStream(pos);
        ois = new ObjectInputStream(pis);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SignUp.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setTitle("Sign up");
        stage.show();
    }

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private TextField confirm;
    @FXML
    protected VBox vBox;

    @FXML
    protected void onClickSignupButton() {
        try {
            if (username.getText().equals("") || password.getText().equals("") || confirm.getText()
                .equals("")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("输入错误");
                alert.setHeaderText("输入存在空值");
                alert.showAndWait();
                return;
            }
            if (!password.getText().equals(confirm.getText())) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("输入错误");
                alert.setHeaderText("两次输入的密码不同");
                alert.showAndWait();
                return;
            }
            Client.getClient().sendRequest(RequestType.Signup, null,
                new User(username.getText(), password.getText()));
            Request request = (Request) signUpController.ois.readObject();
            if (request.isSuccess()) {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("注册成功");
                alert.setHeaderText("注册成功");
                alert.showAndWait();
                vBox.getScene().getWindow().hide();
            } else {
                Alert alert = new Alert(AlertType.INFORMATION);
                alert.setTitle("注册失败");
                alert.setHeaderText("注册失败：" + request.getInfo());
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
