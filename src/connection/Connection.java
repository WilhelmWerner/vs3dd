package connection;

import java.io.IOException;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */
public class Connection {

    private boolean UDP = false;

    private IConnection Connection;

    public Connection(String host, int port) throws Exception {
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
