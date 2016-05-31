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
	
	int port;
	int portOfLastClient;
	int[] portOf = new int[4];
	SynchronousQueue<String> queue = new SynchronousQueue<String>();
	SynchronousQueue<String> senderQueue = new SynchronousQueue<String>();
	String message;
	
	ServerSocket listenSocket;
	
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
		String message;
		Socket client;
		String[] name = new String[4];
		for(int components = 0; components < 4;){
            client = listenSocket.accept();
            System.out.println("Connection with: " +     // Output connection
                    client.getRemoteSocketAddress());   // (Client) address
            
			message = identifyClient(client);
            
			switch(message){
			case "drucker":
				if(component[0] == null){
					components++;
					System.out.println(message + " verbunden");
					name[0] = message;
				}
	            component[0] = client;
				break;
			case "container rot":
				if(component[1] == null){
					components++;
					System.out.println(message + " verbunden");
					name[1] = message;
				}
				component[1] = client;
				break;
			case "container gruen":
				if(component[2] == null){
					components++;
					System.out.println(message + " verbunden");
					name[2] = message;
				}
				component[2] = client;
				break;
			case "container blau":
				if(component[3] == null){
					components++;
					System.out.println(message + " verbunden");
					name[3] = message;
				}
				component[3] = client;
				break;
			default:
				System.err.println("not a component");
			}	
		}
		
		for(int i = 0; i < 4; i++)
		{
			fromClient[i] = new BufferedReader(new InputStreamReader(component[i].getInputStream())); // Datastream FROM Client
			toClient[i] = new DataOutputStream(component[i].getOutputStream());
			new TCPReceiver(name[i], fromClient[i], queue, senderQueue).start();
		}
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
