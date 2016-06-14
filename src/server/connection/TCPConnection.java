package server.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.SynchronousQueue;

import com.google.gson.Gson;


public class TCPConnection implements IConnection{
	
	private int port;
	private int portOfLastClient;
	private int[] portOf = new int[4];
	private SynchronousQueue<String> queue = new SynchronousQueue<String>();
	private SynchronousQueue<String> senderQueue = new SynchronousQueue<String>();
	private String message;
	private Socket client;
	private String[] name = new String[4];
	
	private ServerSocket listenSocket;
	
	private Socket component[] = new Socket[4];

    private BufferedReader[] fromClient = new BufferedReader[4];
    private DataOutputStream[] toClient = new DataOutputStream[4];
    
    private String lastSender;
	
	public TCPConnection(int port) throws IOException{
		this.port = port;
		
		connect();
	}

	@Override
	public void connect() throws IOException {
        listenSocket = new ServerSocket(port);
        System.out.println("TCP Server startet auf Port " + port);
	}

	@Override
	public void close() throws IOException {
		for(int i = 0; i < 4; i++){
			fromClient[i].close();
			toClient[i].close();
			component[i].close();
		}
	}

	@Override
	public void sendMessage(String msg, int recipient) throws IOException {
		toClient[recipient].writeBytes(msg);
	}

	@Override
	public String receiveMessage() throws IOException {
		try {
			message = queue.take();
			lastSender = senderQueue.take();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return message;
		
	}

	@Override
	public void waitForAllComponents() throws IOException {
		for(int components = 0; components < 4;){
            client = listenSocket.accept();
            System.out.println("Connection with: " +     // Output connection
                    client.getRemoteSocketAddress());   // (Client) address
            
			message = identifyClient(client);
            
			switch(message){
			case "drucker":
				if(connectComponent(0)) components++;
				break;
			case "red":
				if(connectComponent(1)) components++;
				break;
			case "green":
				if(connectComponent(2)) components++;
				break;
			case "blue":
				if(connectComponent(3)) components++;
				break;
			default:
				System.err.println("not a component");
			}	
		}
		
		for(int i = 0; i < 4; i++)
		{
			fromClient[i] = new BufferedReader(new InputStreamReader(component[i].getInputStream())); // Datastream FROM Client
			toClient[i] = new DataOutputStream(component[i].getOutputStream());
			new TCPReceiver(name[i], fromClient[i], toClient[i], queue, senderQueue).start();
		}
	}
	
	private boolean connectComponent(int i){
		// first added component
		if(component[i] == null){
			System.out.println(message + " verbunden");
			name[i] = message;
	        component[i] = client;
	        return true;
		}
		// if the same or another client with the same name tries to connect
		// just assign the client but do not increment the counter of components
        component[i] = client;
        return false;
	}
		
	private String identifyClient(Socket client) throws IOException{
		BufferedReader fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // Datastream FROM Client

		String message = fromClient.readLine();
		return message;
	}

	@Override
	public String lastSender() {
		return lastSender;
	}

}
