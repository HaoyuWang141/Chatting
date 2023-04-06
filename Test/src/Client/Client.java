package Client;

import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {

    private final String host;
    private final int port;
    private BufferedReader in;
    private PrintWriter out;
    private Scanner scanner;

    public Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void start() {
        try (Socket socket = new Socket(host, port);) {
            System.out.println("连接至服务器：" + socket.getRemoteSocketAddress());
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            System.out.println("1111");
            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            System.out.println("22222");
            out = new PrintWriter(socket.getOutputStream(), true);
            System.out.println("akldfjalkdfjaslkdfja");
            // 从Server接受消息
            Thread receiveThread = new Thread(() -> {
                String message;
                try {
                    while ((message = in.readLine()) != null) {
                        System.out.println(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            receiveThread.start();

            // 向Server发送消息
//            scanner = new Scanner(System.in);
//            Thread sendThread = new Thread(() -> {
//                String message;
//                while (true) {
//                    message = scanner.nextLine();
//                    out.println(message);
//                }
//            });
//            sendThread.start();
            // 发送消息
            scanner = new Scanner(System.in);
            while (true) {
                String message = scanner.nextLine();
                out.println(message);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            try {
                in.close();
                out.close();
                scanner.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
