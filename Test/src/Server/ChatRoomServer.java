package Server;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatRoomServer {

    // 定义用于存储客户端连接的List
    private static List<ClientHandler> clients = new ArrayList<ClientHandler>();

    public static void main(String[] args) throws Exception {

        // 创建一个ServerSocket来监听客户端连接
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("聊天室服务器已启动，等待客户端连接...");

        // 循环等待客户端连接
        while (true) {
            // 服务器等待客户端连接
            Socket clientSocket = serverSocket.accept();
            System.out.println("客户端连接成功，地址：" + clientSocket.getRemoteSocketAddress());

            // 为每个客户端创建一个ClientHandler线程
            ClientHandler clientHandler = new ClientHandler(clientSocket, clients);
            clients.add(clientHandler);
            clientHandler.start();
        }
    }

    // 定义一个静态内部类，用于处理客户端的连接请求和消息
    private static class ClientHandler extends Thread {

        private Socket clientSocket; // 客户端socket
        private List<ClientHandler> clients; // 所有客户端列表
        private BufferedReader in; // 用于读取客户端发来的消息的BufferedReader
        private PrintWriter out; // 用于发送消息给客户端的PrintWriter

        // 构造函数，初始化客户端
        public ClientHandler(Socket socket, List<ClientHandler> clients) {
            this.clientSocket = socket;
            this.clients = clients;
        }

        // 处理客户端连接和消息
        public void run() {
            try {
                // 初始化输入和输出流
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                out = new PrintWriter(clientSocket.getOutputStream(), true);

                // 读取客户端发来的消息，并将其广播给其他客户端
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println("收到客户端消息：" + inputLine);
                    for (ClientHandler client : clients) {
                        if (client != this) {
                            client.out.println(inputLine);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("客户端异常断开：" + clientSocket.getRemoteSocketAddress());
            } finally {
                try {
                    clientSocket.close();
                    clients.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}