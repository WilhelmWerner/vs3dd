package connection;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */

import java.io.*;
import java.net.*;

public class TCPConnection implements IConnection {

    private String host;
    private int port;

    private Socket socket;
    private BufferedReader fromServer;
    private DataOutputStream toServer;

    public TCPConnection(String host, int port) throws Exception {
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        toServer = new DataOutputStream(socket.getOutputStream());
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void close() throws IOException {
        socket.close();
        toServer.close();
        fromServer.close();
    }

    public void sendMessage(String msg) throws IOException {
        toServer.writeBytes(msg + '\n');
    }

    public void tellServerToClose() throws IOException {
        toServer.writeBytes("." + '\n');
    }

    public String receiveMessage() throws IOException {
        String msg = fromServer.readLine();
        String[] param = msg.split("-");
        if (param[0].equals("Ping")) {

        }
        return param[0];
    }

}
