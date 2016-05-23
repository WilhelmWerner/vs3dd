package server.connection;

import java.io.IOException;


/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */
public interface IConnection {
    public void connect() throws IOException;
    public void close() throws IOException;
    public void sendMessage(String msg, int recipient) throws IOException;
    public String receiveMessage() throws IOException;
    public void waitForAllComponents() throws IOException;
    public String lastSender();
}
