package Connection;

import java.io.IOException;

/**
 * Created by Mr. Crapfruit on 20.11.2015.
 */
public class Connection {

    private boolean UDP = false;
    private int port = 25565;
    private String host = "localhost";

    private IConnection Connection;

    public Connection() throws Exception {
        if(UDP) {
            Connection = new UDPConnection(host, port);
        }
        else {
            Connection = new TCPConnection(host, port);
        }
    }

    public void connect() throws IOException {
        Connection.connect();
    }

    public void close() throws IOException{
        Connection.close();
    }

    public void tellServerToClose() throws IOException {
        Connection.tellServerToClose();
    }

    public void sendMessage(String msg) throws IOException {
        Connection.sendMessage(msg);
    }

    public String receiveMessage() throws IOException {
        return Connection.receiveMessage();
    }
}
