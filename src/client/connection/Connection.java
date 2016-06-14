package client.connection;

import java.io.IOException;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */
public class Connection {

    private IConnection Connection;

    /**
     * 
     * @param host ip address of the host
     * @param port port for the socket
     * @param udp udp yes? = true otherwise false
     * @throws Exception
     */
    public Connection(String host, int port, boolean udp, String name) throws Exception {
    	if(udp) {
            Connection = new UDPConnection(host, port, name);
        }
        else {
            Connection = new TCPConnection(host, port, name);
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
