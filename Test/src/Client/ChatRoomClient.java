package Client;
import java.io.*;
import java.net.*;
import java.util.Scanner;

public class ChatRoomClient {

    private final String host;
    private final int port;
    private BufferedReader in;
    private PrintWriter out;

    public ChatRoomClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket(host, port)) {
            System.out.println("连接到服务器：" + socket.getRemoteSocketAddress());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // 接收其他客户端发送的消息
            Thread receiveThread = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    System.out.println("与服务器断开连接");
                }
            });
            receiveThread.start();

            // 发送消息
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                out.println(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ChatRoomClient client = new ChatRoomClient("localhost", 8080);
        client.start();
    }
}

