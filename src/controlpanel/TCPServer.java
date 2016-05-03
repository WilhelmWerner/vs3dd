package controlpanel;

import java.net.*;

public class MultithreadedTCPServer {
    public static void main(String[] args) throws Exception {
        int port = 25565;
        ServerSocket listenSocket = new ServerSocket(port);
        System.out.println("Multithreaded Server starts on Port " + port);
        while(true) {
            Socket client = listenSocket.accept();
            System.out.println("Connection with: " +     // Output connection
                    client.getRemoteSocketAddress());   // (Client) address
            new MonitoringService(client).start();
        }
    }
}
