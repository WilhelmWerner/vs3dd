package Connection;

import java.io.IOException;

/**
 * Created by Mr. Crapfruit on 21.11.2015.
 */
public interface IConnection {
    public void connect() throws IOException;
    public void close() throws IOException;
    public void tellServerToClose() throws IOException;
    public void sendMessage(String msg) throws IOException;
    public String receiveMessage() throws IOException;
}
