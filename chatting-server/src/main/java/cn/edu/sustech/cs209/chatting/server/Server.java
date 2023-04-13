package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.ChatGroup;
import cn.edu.sustech.cs209.chatting.common.ChatGroupType;
import cn.edu.sustech.cs209.chatting.common.LocalGroup;
import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.Request;
import cn.edu.sustech.cs209.chatting.common.RequestType;
import cn.edu.sustech.cs209.chatting.common.User;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class Server {

    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final Map<Integer, ChatGroup> groupsMap = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static int ID = 0;
    private final List<User> registerUser = new ArrayList<>();

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器已启动，监听端口：" + port);
            registerUser.add(new User("a", "1"));
            registerUser.add(new User("b", "2"));
            registerUser.add(new User("c", "3"));

            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接：" + socket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(socket, clients, groupsMap,
                    registerUser);
                clients.add(clientHandler);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private final List<ClientHandler> clients;
        private final Map<Integer, ChatGroup> groupsMap;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private User user = new User("", "");
        private List<User> registerUser;

        public ClientHandler(Socket socket, List<ClientHandler> clients,
            Map<Integer, ChatGroup> groupsMap, List<User> registerUser) {
            this.clientSocket = socket;
            this.clients = clients;
            this.groupsMap = groupsMap;
            this.registerUser = registerUser;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                boolean connect = true;
                while (true) {
                    Object obj = in.readObject();
                    if (obj.getClass().equals(Message.class)) {
                        Message message = (Message) obj;
//                        System.out.println(
//                            "客户端" + this.clientSocket.getRemoteSocketAddress() + "发来消息: "
//                                + message.getData() + " sendTo: " + message.getSendTo());
                        if (groupsMap.containsKey(message.getSendTo())) {
                            groupsMap.get(message.getSendTo()).addMessage(message);
                            this.out.writeObject(
                                new Request<>(RequestType.SendMessage, true, "成功发送消息", null));
                        } else {
                            this.out.writeObject(
                                new Request<>(RequestType.SendMessage, false,
                                    "发送失败,未找到指定的group:id=" + message.getSendTo(), null));
                        }
                    } else if (obj.getClass().equals(Request.class)) {
//                        System.out.println(
//                            "客户端" + this.clientSocket.getRemoteSocketAddress()
//                                + "发来Request, 类型为: " + ((Request) obj).getType()
//                                + ", 信息为: " + ((Request) obj).getInfo());
                        switch (((Request<?>) obj).getType()) {
                            case Signup -> {
                                System.out.println("Sign up request");
                                Request<User> signRequest = (Request<User>) obj;
                                System.out.println(signRequest.getObj().toString());
                                User newUser = signRequest.getObj();
                                if (checkUsernameExists(newUser.name())) {
                                    this.out.writeObject(
                                        new Request<>(RequestType.Signup, false, "用户已存在",
                                            null));
                                } else {
                                    registerUser.add(newUser);
                                    this.out.writeObject(
                                        new Request<>(RequestType.Signup, true, "注册成功",
                                            null));
                                }
                            }
                            case Login -> {
                                Request<User> loginRequest = (Request<User>) obj;
                                User loginUser = loginRequest.getObj();
                                System.out.println(
                                    loginRequest.getInfo() + ": " + loginUser.toString());
                                if (!checkUsernameExists(loginUser.name())) {
                                    this.out.writeObject(
                                        new Request<>(RequestType.Login, false,
                                            "用户不存在，请先注册",
                                            null));
                                    break;
                                }
                                if (!checkPassword(loginUser.name(), loginUser.pwd())) {
                                    this.out.writeObject(
                                        new Request<>(RequestType.Login, false, "密码错误",
                                            null));
                                    break;
                                }
                                if (checkUserOnline(loginUser.name())) {
                                    this.out.writeObject(
                                        new Request<>(RequestType.Login, false, "该用户已上线",
                                            null));
                                    break;
                                }
                                this.user = new User(loginUser.name(), loginUser.pwd());
                                this.out.writeObject(
                                    new Request<>(RequestType.Login, true, "登录成功",
                                        null));
                                for (ChatGroup g : groupsMap.values()) {
                                    if (g.containUser(user)) {
                                        g.addMessage(new Message("Server", g.getId(),
                                            "User " + user.name() + " is online"));
                                    }
                                }
                            }
                            case UserList -> {
                                List<User> onlineUserList = clients.stream()
                                    .map(e -> new User(e.user.name(), null)).toList();
                                this.out.writeObject(
                                    new Request<>(RequestType.UserList, true,
                                        "get online user list",
                                        onlineUserList));
                            }
                            case ChatGroupList -> {
                                List<LocalGroup> groupList = new ArrayList<>();
                                List<ChatGroup> chatGroupList = new ArrayList<>();
                                for (ChatGroup g : groupsMap.values()) {
                                    chatGroupList.add(g);
                                }
                                chatGroupList.stream().sorted((e1, e2) -> {
                                    if (e1 == null || e2 == null) {
                                        return 0;
                                    }
                                    if (e1.getLastActiveTime() > e2.getLastActiveTime()) {
                                        return -1;
                                    } else if (e1.getLastActiveTime() < e2.getLastActiveTime()) {
                                        return 1;
                                    }
                                    return 0;
                                }).forEach(g -> {
                                    if (g.containUser(user)) {
                                        if (g.getType().equals(ChatGroupType.OneToOneChat)) {
                                            String str = null;
                                            for (User u : g.getUsers()) {
                                                if (!u.equals(user)) {
                                                    str = u.name();
                                                    break;
                                                }
                                            }
                                            groupList.add(
                                                new LocalGroup(g.getId(), str,
                                                    g.getType()));
                                        } else if (g.getType().equals(ChatGroupType.GroupChat)) {
                                            groupList.add(
                                                new LocalGroup(g.getId(), g.getName(),
                                                    g.getType()));
                                        }
                                    }
                                });
                                this.out.writeObject(
                                    new Request<>(RequestType.ChatGroupList, true,
                                        "get chat group list Successfully",
                                        groupList));
                            }
                            case MessageList -> {
                                int groupId;
                                try {
                                    groupId = ((Request<Integer>) obj).getObj();
//                                    System.out.println(
//                                        "request group messages, groupId: " + groupId);
                                    List<Message> messages = new ArrayList<>(
                                        groupsMap.get(groupId).getRecord());
//                                    messages.stream().map(Message::getData)
//                                        .forEach(System.out::println);
                                    this.out.writeObject(
                                        new Request<>(RequestType.MessageList, true,
                                            "Request Message Successfully",
                                            messages));
                                } catch (Exception e) {
                                    this.out.writeObject(
                                        new Request<>(RequestType.MessageList, false,
                                            "Request Message Fail",
                                            null));
                                }
                            }
                            case CreateChatGroup -> {
                                Request<ChatGroup> createChatGroupRequest = (Request<ChatGroup>) obj;
                                ChatGroup newChatGroup = createChatGroupRequest.getObj();
                                if (newChatGroup.getType().equals(ChatGroupType.OneToOneChat)) {
                                    for (Map.Entry<Integer, ChatGroup> g : groupsMap.entrySet()) {
                                        ChatGroup c = g.getValue();
                                        if (c.oneToOneChatGroupEquals(newChatGroup)) {
                                            this.out.writeObject(
                                                new Request<>(RequestType.CreateChatGroup, false,
                                                    "this one-to-one group exists", c.getId())
                                            );
                                            break;
                                        }
                                    }
                                }
                                newChatGroup.setId(++ID);
                                groupsMap.put(newChatGroup.getId(), newChatGroup);
                                this.out.writeObject(
                                    new Request<>(RequestType.CreateChatGroup, true,
                                        "create chat successfully", newChatGroup.getId())
                                );
                            }
                            case Disconnect -> connect = false;
                        }
                    }
                    if (!connect) {
                        System.out.println(
                            "客户端正常断开：" + clientSocket.getRemoteSocketAddress());
                        break;
                    }
                }
            } catch (IOException e) {
                System.out.println("客户端异常断开：" + clientSocket.getRemoteSocketAddress());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for (ChatGroup g : groupsMap.values()) {
                    if (g.containUser(user)) {
                        g.addMessage(new Message("Server", g.getId(),
                            "User " + user.name() + " is offline"));
                    }
                }
                clients.remove(this);
                /*
                 * TODO: 持久化储存*/
            }
        }
    }

    private boolean checkUsernameExists(String username) {
        if (username == null || username.equals("")) {
            return true;
        }
        return registerUser.stream().map(User::name).collect(Collectors.toSet()).contains(username);
    }

    private boolean checkPassword(String username, String pwd) {
        if (username == null || username.equals("") || pwd == null || pwd.equals("")) {
            return false;
        }
        for (User u : registerUser) {
            if (u.name().equals(username) && u.pwd().equals(pwd)) {
                return true;
            }
        }
        return false;
    }

    private boolean checkUserOnline(String username) {
        return clients.stream().map(e -> e.user.name()).collect(Collectors.toSet())
            .contains(username);
    }
}
