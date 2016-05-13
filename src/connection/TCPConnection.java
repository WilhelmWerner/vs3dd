package connection;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */

import java.io.*;
import java.net.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import printer.PrinterStep;

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

    public PrinterStep receiveMessage() throws IOException {
        String msg = fromServer.readLine();
        
        Gson gson = new GsonBuilder().create();
        PrinterStep step = gson.fromJson(msg, PrinterStep.class);
        
        return step;
    }

}
