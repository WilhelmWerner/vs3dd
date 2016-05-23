package server.connection;

import java.io.IOException;
import java.net.Socket;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */
public class Connection {

    private IConnection connection;

    /**
     * 
     * @param client
     * @param udp
     * @throws Exception
     */
    public Connection(int port, boolean udp) throws Exception {
    	if(udp) {
            connection = new UDPConnection(port);
        }
        else {
            connection = new TCPConnection(port);
        }
    }

    public void connect() throws IOException {
        connection.connect();
    }

    public void close() throws IOException{
        connection.close();
    }

    public void tellServerToClose() throws IOException {
        connection.tellServerToClose();
    }

    public void sendMessage(String msg, int recipient) throws IOException {
        connection.sendMessage(msg, recipient);
    }

    public String receiveMessage() throws IOException {
        return connection.receiveMessage();
    }
    
    public void waitForAllComponents() throws IOException {
    	connection.waitForAllComponents();
    }
}
