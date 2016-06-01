package client.connection;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import actions.Action;



public class TCPConnection implements IConnection {

    private String host;
    private int port;
    private String name;

    private Socket socket;
    private BufferedReader fromServer;
    private DataOutputStream toServer;
	private SynchronousQueue<String> queue = new SynchronousQueue<String>();
	
	private TCPReceiver receiver;

    public TCPConnection(String host, int port, String name) throws Exception {
        this.host = host;
        this.port = port;
        this.name = name;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        toServer = new DataOutputStream(socket.getOutputStream());
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        receiver = new TCPReceiver(name, fromServer, toServer, queue);
        receiver.start();
    }

    public void close() throws IOException {
        socket.close();
        toServer.close();
        fromServer.close();
    }

    public void sendMessage(String msg) throws IOException {
    	if(msg.equals("PING"))
    	{
            Action a = new Action(msg);
        	Gson gson = new GsonBuilder().create();
        	toServer.writeBytes(gson.toJson(a) + '\n');
    		//receiver.setTimeForPing(new Date().getTime());
    	} else {
    		toServer.writeBytes(msg + '\n');
    	}
    }

    public void tellServerToClose() throws IOException {
        toServer.writeBytes("." + '\n');
    }

    public String receiveMessage() throws IOException {
        String msg = "";
		try {
			msg = queue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        return msg;
    }

}
