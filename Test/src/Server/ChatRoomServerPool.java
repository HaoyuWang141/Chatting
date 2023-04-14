package Server;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChatRoomServerPool {

    public static void main(String[] args) {
        new ChatRoomServerPool(8080).start();
    }
    private final int port;
    private final List<ClientHandler> clients = new ArrayList<>();
    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public ChatRoomServerPool(int port) {
        this.port = port;
    }

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("服务器已启动，监听端口：" + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("客户端已连接：" + socket.getRemoteSocketAddress());
                ClientHandler clientHandler = new ClientHandler(socket, clients);
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
        private BufferedReader in;
        private PrintWriter out;

        public ClientHandler(Socket socket, List<ClientHandler> clients) {
            this.clientSocket = socket;
            this.clients = clients;
        }

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
                    in.close();
                    out.close();
                    clientSocket.close();
                    clients.remove(this);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
