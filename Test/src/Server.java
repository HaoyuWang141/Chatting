import java.net.*;
import java.io.*;

public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8080);
        System.out.println("等待客户端连接...");

        Socket clientSocket = serverSocket.accept();
        System.out.println("客户端连接成功！");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            System.out.println("客户端发送的消息: " + inputLine);
            out.println("服务器收到消息：" + inputLine);
        }

        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
