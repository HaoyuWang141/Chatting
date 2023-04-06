package cn.edu.sustech.cs209.chatting.server;

import cn.edu.sustech.cs209.chatting.common.ChatGroup;
import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;
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

public class Server {

    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    // 维护所有存在的群聊，包括双人和多人的
    private Map<Integer, ChatGroup> groupsMap = new HashMap<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private static int ID = 0;

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器已启动，监听端口：" + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接：" + socket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(socket, clients, groupsMap);
                clients.add(clientHandler);
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler implements Runnable {

        private final Socket clientSocket;
        private final List<ClientHandler> clients;
        private final Map<Integer, ChatGroup> groupsMap;
        private ObjectInputStream in;
        private ObjectOutputStream out;
        private String username;

        public ClientHandler(Socket socket, List<ClientHandler> clients,
            Map<Integer, ChatGroup> groupsMap) {
            this.clientSocket = socket;
            this.clients = clients;
            this.groupsMap = groupsMap;
        }

        @Override
        public void run() {
            try {
                in = new ObjectInputStream(new BufferedInputStream(clientSocket.getInputStream()));
                out = new ObjectOutputStream(clientSocket.getOutputStream());
                Message message;
                while (true) {
                    message = (Message) in.readObject();
                    System.out.println("收到客户端消息：" + message.getData());
                    switch (message.getType()) {
                        case RequestLogin:
                            try {
                                String[] data = message.getData().split(":");
                                if (!checkUsername(data[0])) {
                                    throw new Exception("User not exists");
                                }
                                if (!checkPassword(data[0], data[1])) {
                                    throw new Exception("Password is wrong");
                                }
                                username = data[0];
                            } catch (Exception e) {
                                e.printStackTrace();
                                this.out.writeObject(
                                    new Message(System.currentTimeMillis(), "Server", -1,
                                        e.getMessage(),
                                        MessageType.LoginFail));
                                break;
                            }
                            clients.forEach((client) -> {
                                if (client != this) {
                                    ChatGroup group = new ChatGroup(++ID,
                                        username + " " + client.username);
                                    groupsMap.put(ID, group);
                                }
                            });
                            this.out.writeObject(
                                new Message(System.currentTimeMillis(), "Server", -1,
                                    "Login Successfully", MessageType.LoginSuccess));
                            break;

                        case Message:
                            for (Integer id : groupsMap.keySet()) {
                                if (id == message.getSendTo()) {
                                    groupsMap.get(id).addMessage(message);
                                }
                            }
                            break;

                        case RequestChatGroup:
                            // id:name,id:name,...
                            StringBuilder data = new StringBuilder();
                            for (ChatGroup g : groupsMap.values()) {
                                if (g.containUser(username)) {
                                    data.append(g.getId()).append(":").append(g.getName())
                                        .append(",");
                                }
                            }
                            this.out.writeObject(
                                new Message(System.currentTimeMillis(), "Server", -1,
                                    data.toString(), MessageType.RequestSuccess));
                            break;

                        case RequestMessage:
                            int groupId;
                            try {
                                groupId = Integer.parseInt(message.getData());
                                List<Message> messages = groupsMap.get(groupId).getRecord();
                                for (Message m : messages) {
                                    m.setType(MessageType.RequestSuccess);
                                    this.out.writeObject(m);
                                }
                            } catch (Exception e) {
                                this.out.writeObject(
                                    new Message(System.currentTimeMillis(), "Server", -1,
                                        "Request Message Fail", MessageType.RequestFail));
                            }
                            break;

                        case Disconnect:
                            break;
                        default:
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
                    clients.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private static boolean checkUsername(String username) {
            return true;
        }

        private static boolean checkPassword(String username, String pwd) {
            return true;
        }
    }
}
