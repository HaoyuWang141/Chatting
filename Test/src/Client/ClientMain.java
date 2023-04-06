package Client;

public class ClientMain {

    public static void main(String[] args) {
        Client client1 = new Client("localhost",8080);
        client1.start();
    }
}
