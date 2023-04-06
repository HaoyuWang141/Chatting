package cn.edu.sustech.cs209.chatting.client;

import cn.edu.sustech.cs209.chatting.common.Message;
import cn.edu.sustech.cs209.chatting.common.MessageType;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Client {

    private static Client client;
    private final String host;
    private final int port;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Scanner scanner;
    private String name;
    private List<Map<Integer, String>> groups; // key:id,value:group name
    private List<Message> messages;
    private Socket socket;

    private Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public static void Connect(String host, int port) throws IOException {
        if (client != null) {
            client.close();
        }
        client = new Client(host, port);
        client.start();
    }

    public static Client getClient() {
        return client;
    }

    private void start() throws IOException {
        try {
            socket = new Socket(host, port);
            System.out.println("Connect to Server: " + socket.getRemoteSocketAddress());
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Thread receiveThread = new Thread(() -> {
                Message message;
                while (true) {
                    try {
                        message = (Message) in.readObject();
                        System.out.println("Message from Server: " + message.getData());
                        switch (message.getType()) {
                            case LoginSuccess:
                                System.out.println(message.getData());

                                break;
                            case LoginFail:
                                System.out.println(message.getData());

                                break;
                            case RequestSuccess:
                                break;
                            case RequestFail:
                                System.out.println(message.getData());

                                break;
                        }
                    } catch (ClassNotFoundException | IOException e) {
                        e.printStackTrace();
                        System.exit(1);
                    }
                }
            });
            receiveThread.start();
        } catch (ConnectException e) {
//            e.printStackTrace();
            throw e;
        } catch (SocketException e) {
            e.printStackTrace();
            System.out.println(
                socket.getInetAddress().getHostAddress() + " disconnected from the Server");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String sendBy, int sendTo, String data, MessageType type)
        throws IOException {
        Message message = new Message(System.currentTimeMillis(), sendBy, sendTo, data, type);
        out.writeObject(message);
        out.flush();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void close() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
