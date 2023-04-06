package cn.edu.sustech.cs209.chatting.client.Controller;

import cn.edu.sustech.cs209.chatting.client.Client;
import cn.edu.sustech.cs209.chatting.common.MessageType;
import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Login.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setTitle("Chatting Client");
        stage.show();
    }

    @FXML
    private TextField username;
    @FXML
    private TextField password;

    @FXML
    protected void onButtonLoginClick() {
        Client client = Client.getClient();
        try {
            client.sendMessage(username.getText(), 0, username.getText() + password.getText(),
                MessageType.RequestLogin);
            while (true){
                if(true){
                    break;
                }
            }
        } catch (NullPointerException e) {

        } catch (IOException e) {

        }
    }

    @FXML
    protected void onButtonSignupClick() {

    }


}

