package server.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import com.google.gson.Gson;


public class TCPConnection implements IConnection{
	
	int port;
	int portOfLastClient;
	int[] portOf = new int[4];
	
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
		fromClient[0].close();
		toClient[0].close();
		for(int i = 0; i < 4; i++){
			component[i].close();
		}
	}

	@Override
	public void sendMessage(String msg, int recipient) throws IOException {
		toClient[recipient].writeBytes(msg);
	}

	@Override
	public String receiveMessage() throws IOException {
		lastSender = "drucker";
		return fromClient[0].readLine();
	}

	@Override
	public void waitForAllComponents() throws IOException {
		String message;
		Socket client;
		
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
				}
	            component[0] = client;
				break;
			case "container rot":
				if(component[1] == null){
					components++;
					System.out.println(message + " verbunden");
				}
				component[1] = client;
				break;
			case "container gruen":
				if(component[2] == null){
					components++;
					System.out.println(message + " verbunden");
				}
				component[2] = client;
				break;
			case "container blau":
				if(component[3] == null){
					components++;
					System.out.println(message + " verbunden");
				}
				component[3] = client;
				break;
			default:
				System.err.println("not a component");
			}	
		}
		

		fromClient[0] = new BufferedReader(new InputStreamReader(component[0].getInputStream())); // Datastream FROM Client
		toClient[0] = new DataOutputStream(component[0].getOutputStream());
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
