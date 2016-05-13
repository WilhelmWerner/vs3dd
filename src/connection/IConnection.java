package connection;

import java.io.IOException;

import printer.PrinterStep;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */
public interface IConnection {
    public void connect() throws IOException;
    public void close() throws IOException;
    public void tellServerToClose() throws IOException;
    public void sendMessage(String msg) throws IOException;
    public String receiveMessage() throws IOException;
}
