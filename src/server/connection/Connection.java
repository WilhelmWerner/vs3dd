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

    /**
     * Sende eine Nachricht
     * 
     * @param msg nachricht die versendet werden soll
     * @param recipient Empfänger: 0 = drucker 1-3 = container rgb
     * @throws IOException
     */
    public void sendMessage(String msg, int recipient) throws IOException {
        connection.sendMessage(msg, recipient);
    }

    public String receiveMessage() throws IOException {
        return connection.receiveMessage();
    }
    
    public void waitForAllComponents() throws IOException {
    	connection.waitForAllComponents();
    }
    
    public String lastSender(){
    	return connection.lastSender();
    }
}
