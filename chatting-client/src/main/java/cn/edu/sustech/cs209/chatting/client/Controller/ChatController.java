package cn.edu.sustech.cs209.chatting.client.Controller;

import cn.edu.sustech.cs209.chatting.client.Client;
import cn.edu.sustech.cs209.chatting.common.ChatGroup;
import cn.edu.sustech.cs209.chatting.common.ChatGroupType;
import cn.edu.sustech.cs209.chatting.common.LocalGroup;
import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.RequestType;
import cn.edu.sustech.cs209.chatting.common.User;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class ChatController extends Application implements Initializable {

    @FXML
    Label currentUsername;
    @FXML
    Label onlineUserCnt;
    @FXML
    public ListView<User> userListView;
    @FXML
    public ListView<Message> chatContentListView;
    @FXML
    public ListView<LocalGroup> chatGroupListView;
    @FXML
    private TextArea inputArea;
    public List<User> userList;
    public List<LocalGroup> chatGroupList;
    public List<Message> chatContentList;


    private int currentChatId;
    boolean updateUserListFinished, updateChatGroupListFinished, updateChatContentListFinished;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Chat.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        stage.setTitle("Chatting Client");
        stage.setOnCloseRequest(e -> {
            Client.getClient().close();
        });
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Client.getClient().setChatController(this);
        currentUsername.setText(Client.getClient().getName());
        userListView.setCellFactory(new UserCellFactory());
        chatContentListView.setCellFactory(new MessageCellFactory());
        chatGroupListView.setCellFactory(new GroupCellFactory());
        userList = new ArrayList<>();
        chatGroupList = new ArrayList<>();
        chatContentList = new ArrayList<>();
        currentChatId = -1;
        updateUserListFinished = false;
        updateChatGroupListFinished = false;
        updateChatContentListFinished = false;

        Thread requestThread = new Thread(() -> {
            while (true) {
                try {
                    Client.getClient().sendRequest(RequestType.UserList, null, null);
                    Client.getClient().sendRequest(RequestType.ChatGroupList, null, null);
                    Client.getClient()
                        .sendRequest(RequestType.ChatGroupMessages, null, currentChatId);
                    while (true) {
                        Thread.sleep(10);
                        if (updateUserListFinished
                            & updateChatGroupListFinished
                            & updateChatContentListFinished) {
                            updateUserListFinished = false;
                            updateChatGroupListFinished = false;
                            updateChatContentListFinished = false;
                            break;
                        }
                    }
                    Thread.sleep(500);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        requestThread.start();
    }

    boolean createNewChat = false;

    @FXML
    public void createPrivateChat() {
        if (userList.size() == 0) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("无需创建聊天");
            alert.setHeaderText("因为您处于离线状态，或仅有您一人在线，故没必要创建聊天");
            alert.showAndWait();
            return;
        }
        AtomicReference<String> user = new AtomicReference<>();
        Stage stage = new Stage();
        ComboBox<String> userSel = new ComboBox<>();
        userSel.setPrefWidth(60);
        if (userList != null) {
            userSel.getItems().addAll(userList.stream().map(User::name).toList());
        } else {
            userSel.getItems().clear();
        }
        Button okBtn = new Button("OK");
        okBtn.setOnAction(e -> {
            user.set(userSel.getSelectionModel().getSelectedItem());
            AtomicBoolean chatIsExisted = new AtomicBoolean(false);
            chatGroupList.forEach(e1 -> {
                if (e1.type().equals(ChatGroupType.OneToOneChat) && e1.name()
                    .equals(user.get())) {
                    currentChatId = e1.id();
                    chatIsExisted.set(true);
                }
            });
            if (!chatIsExisted.get()) {
                List<User> list = new ArrayList<>();
                list.add(new User(currentUsername.getText(), ""));
                list.add(new User(user.get(), ""));
                ChatGroup newChatGroup = new ChatGroup(-1,
                    new User(currentUsername.getText(), ""),
                    list, ChatGroupType.OneToOneChat, "");
                try {
                    Client.getClient()
                        .sendRequest(RequestType.CreateChatGroup, "create one-to-one chat",
                            newChatGroup);
                } catch (IOException ex) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("开聊天失败");
                    alert.setHeaderText("因为网络问题,创建群聊失败");
                    alert.showAndWait();
                }
            }
            stage.close();
            createNewChat = true;
        });

        VBox box = new VBox(20);
        box.setPrefSize(300,200);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 20, 20, 20));
        Label label = new Label("创建个人聊天");
        label.setPadding(new Insets(0,20,0,20));
        box.getChildren().addAll(label, userSel, okBtn);
        stage.setScene(new Scene(box));
        stage.showAndWait();
    }

    /**
     * A new dialog should contain a multi-select list, showing all user's name. You can select
     * several users that will be joined in the group chat, including yourself.
     * <p>
     * The naming rule for group chats is similar to WeChat: If there are > 3 users: display the
     * first three usernames, sorted in lexicographic order, then use ellipsis with the number of
     * users, for example: UserA, UserB, UserC... (10) If there are <= 3 users: do not display the
     * ellipsis, for example: UserA, UserB (2)
     */
    @FXML
    public void createGroupChat() {
        if (userList.size() == 0) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("无需创建聊天");
            alert.setHeaderText("因为您处于离线状态，或仅有您一人在线，故没必要创建聊天");
            alert.showAndWait();
            return;
        }
        Stage stage = new Stage();
        TextField groupName = new TextField();
        groupName.setPromptText("请输入群聊名称");
        List<CheckBox> userSel = new ArrayList<>();
        if (userList != null) {
            userList.forEach(e -> {
                userSel.add(new CheckBox(e.name()));
            });
        }
        Button okBtn = new Button("OK");
        okBtn.setOnAction(e -> {
            List<User> list = new ArrayList<>();
            userSel.forEach(e1 -> {
                if (e1.isSelected()) {
                    list.add(new User(e1.getText(), ""));
                }
            });
            if (groupName.getText().equals("")) {
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("开聊天失败");
                alert.setHeaderText("没输入群聊名称");
                alert.showAndWait();
                return;
            }
            if (list.size() > 0) {
                list.add(new User(currentUsername.getText(), ""));
                ChatGroup newChatGroup = new ChatGroup(-1,
                    new User(currentUsername.getText(), ""),
                    list, ChatGroupType.GroupChat, groupName.getText());
                try {
                    Client.getClient()
                        .sendRequest(RequestType.CreateChatGroup, "create group chat",
                            newChatGroup);
                } catch (IOException ex) {
                    Alert alert = new Alert(AlertType.WARNING);
                    alert.setTitle("开聊天失败");
                    alert.setHeaderText("因为网络问题,创建群聊失败");
                    alert.showAndWait();
                }
            }
            stage.close();
            createNewChat = true;
        });

        VBox box = new VBox(20);
        box.setPrefSize(300,200);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20, 20, 20, 20));
        HBox userSelBox = new HBox(5);
        userSelBox.setMaxWidth(100);
        userSel.forEach(e -> userSelBox.getChildren().add(e));
        Label label = new Label("创建群聊");
        box.getChildren().addAll(label, groupName, userSelBox, okBtn);
        stage.setScene(new Scene(box));
        stage.showAndWait();
    }

    /**
     * Sends the message to the <b>currently selected</b> chat.
     * <p>
     * Blank messages are not allowed. After sending the message, you should clear the text input
     * field.
     * </p>
     */

    @FXML
    public void doSendMessage() {
        try {
            if (inputArea.getText() != null || inputArea.getText().equals("")) {
                Client.getClient()
                    .sendMessage(Client.getClient().getName(), currentChatId,
                        inputArea.getText());
            }
            inputArea.setText("");
        } catch (IOException e) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("发送失败");
            alert.setHeaderText("因为网络问题,消息发送失败");
            alert.showAndWait();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void setChatGroupId() {
        // 获取选择模型
        MultipleSelectionModel<LocalGroup> selectionModel = chatGroupListView.getSelectionModel();
        // 获取选定的列表项
        LocalGroup selectedItem = selectionModel.getSelectedItem();
        // 执行操作
        System.out.println("选定的列表项：" + selectedItem);
        if (selectedItem != null) {
            currentChatId = selectedItem.id();
        }
    }

    public void setUserList(List<User> list) {
        userList = new ArrayList<>(list);
        userList.removeIf(user -> user.name().equals(currentUsername.getText()));
        updateUserListFinished = true;
//        System.out.println("chage updateUserListFinished true");
        Platform.runLater(() -> {
            onlineUserCnt.setText("Online User: " + list.size());
            ObservableList<User> userObservableList = FXCollections.observableArrayList();
            userObservableList.addAll(userList);
            userListView.setItems(userObservableList);
        });
    }

    public void setChatGroupList(List<LocalGroup> groupList) {
        if (groupList == null) {
            chatGroupList = new ArrayList<>();
        } else {
            chatGroupList = new ArrayList<>(groupList);
        }
        updateChatGroupListFinished = true;
        Platform.runLater(() -> {
            ObservableList<LocalGroup> localGroupObservableList = FXCollections.observableArrayList();
            localGroupObservableList.addAll(chatGroupList);
            chatGroupListView.setItems(localGroupObservableList);
        });
    }

    public void setChatContentList(List<Message> messageList) {
        if (messageList == null) {
            chatContentList = new ArrayList<>();
        } else {
            chatContentList = new ArrayList<>(messageList);
        }
        updateChatContentListFinished = true;
        Platform.runLater(() -> {
            if (createNewChat) {
                createNewChat = false;
                MultipleSelectionModel<LocalGroup> selectionModel = chatGroupListView.getSelectionModel();
                for (int i = 0; i < chatGroupList.size(); i++) {
                    if (chatGroupList.get(i).id() == currentChatId) {
                        selectionModel.select(i);
                        break;
                    }
                }
            }
            ObservableList<Message> messageObservableList = FXCollections.observableArrayList();
            messageObservableList.addAll(chatContentList);
            chatContentListView.setItems(messageObservableList);
        });
    }

    public void setCurrentChatId(int currentChatId) {
        this.currentChatId = currentChatId;
    }

    private class UserCellFactory implements
        Callback<ListView<User>, ListCell<User>> {

        @Override
        public ListCell<User> call(ListView<User> param) { // 回调函数
            return new ListCell<User>() {

                @Override
                public void updateItem(User user, boolean empty) { // 更新列表项
                    super.updateItem(user, empty);
                    if (empty || Objects.isNull(user)) { // 列表或消息为空
                        setGraphic(null); // 设置图形为空
                        setText(null); // 设置文字为空
                        return;
                    }

                    HBox hBox = new HBox();
                    Label label = new Label(user.name());
                    label.setPrefSize(100, 20);
                    label.setWrapText(true);
                    label.setAlignment(Pos.CENTER);
//                    label.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().addAll(label);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(hBox);
                }
            };
        }
    }

    private class GroupCellFactory implements
        Callback<ListView<LocalGroup>, ListCell<LocalGroup>> {

        @Override
        public ListCell<LocalGroup> call(ListView<LocalGroup> param) { // 回调函数
            return new ListCell<LocalGroup>() {

                @Override
                public void updateItem(LocalGroup group, boolean empty) { // 更新列表项
                    super.updateItem(group, empty);
                    if (empty || Objects.isNull(group)) { // 列表或消息为空
                        setGraphic(null); // 设置图形为空
                        setText(null); // 设置文字为空
                        return;
                    }

                    HBox hBox = new HBox();
                    Label label = new Label(group.name());

                    label.setPrefSize(100, 20);
                    label.setWrapText(true);
//                    label.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
                    hBox.setAlignment(Pos.CENTER);
                    hBox.getChildren().addAll(label);
                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(hBox);
                }
            };
        }
    }

    /**
     * You may change the cell factory if you changed the design of {@code Message} model. Hint: you
     * may also define a cell factory for the chats displayed in the left panel, or simply override
     * the toString method.
     */
    private class MessageCellFactory implements Callback<ListView<Message>, ListCell<Message>> {

        @Override
        public ListCell<Message> call(ListView<Message> param) { // 回调函数
            return new ListCell<Message>() {

                @Override
                public void updateItem(Message msg, boolean empty) { // 更新列表项
                    super.updateItem(msg, empty);
                    if (empty || Objects.isNull(msg)) { // 列表或消息为空
                        setGraphic(null); // 设置图形为空
                        setText(null); // 设置列表为空
                        return;
                    }

                    HBox wrapper = new HBox();
                    Label nameLabel = new Label(msg.getSentBy());
                    Label msgLabel = new Label(msg.getData());

                    nameLabel.setPrefSize(50, 20);
                    nameLabel.setWrapText(true);
                    nameLabel.setStyle("-fx-border-color: black; -fx-border-width: 1px;");
                    nameLabel.setAlignment(Pos.CENTER);

                    if (msg.getSentBy().equals("Server")) {
                        wrapper.setAlignment(Pos.CENTER);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    } else if (Client.getClient().getName().equals(msg.getSentBy())) {
                        wrapper.setAlignment(Pos.CENTER_RIGHT);
                        wrapper.getChildren().addAll(msgLabel, nameLabel);
                        msgLabel.setPadding(new Insets(0, 20, 0, 0));
                    } else {
                        wrapper.setAlignment(Pos.CENTER_LEFT);
                        wrapper.getChildren().addAll(nameLabel, msgLabel);
                        msgLabel.setPadding(new Insets(0, 0, 0, 20));
                    }

                    setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    setGraphic(wrapper);
                }
            };
        }
    }

    public void serverDown() {
        Platform.runLater(() -> {
            onlineUserCnt.setText("Online User: 0");
            userList = new ArrayList<>();
            Alert alert = new Alert(AlertType.ERROR);
            alert.setTitle("服务器断开连接");
            alert.setHeaderText(
                "您已与服务器断开连接, 您只能查看最后一个聊天的历史消息，但是不能查看其他聊天信息、创建聊天、接受新消息或发送消息");
            alert.showAndWait();
        });
    }
}

