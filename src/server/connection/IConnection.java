package server.connection;

import java.io.IOException;


/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */
public interface IConnection {
    public void connect() throws IOException;
    public void close() throws IOException;
    public void tellServerToClose() throws IOException;
    /**
     * Sende eine Nachricht
     * 
     * @param msg nachricht die versendet werden soll
     * @param recipient Empfänger: 0 = drucker 1-3 = container rgb
     * @throws IOException
     */
    public void sendMessage(String msg, int recipient) throws IOException;
    public String receiveMessage() throws IOException;
    public int lastMessageFrom();
    public void waitForAllComponents() throws IOException;
}
