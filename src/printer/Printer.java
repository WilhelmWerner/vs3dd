package printer;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import connection.Connection;

public class Printer extends Thread{

    private Connection Connection;
    private boolean connected = false;
    private int port = 0;
    private String host = "";
	
	public Printer(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public void run(){
		try{

            Connection = new Connection(host, port);
            Connection.connect();
            connected = true;
        } catch(Exception e) {
            System.err.println("Connection to Server failed: " + e.getMessage());
        }
		
		while(connected) {
            try {
                receiveMessage();
            }
            catch (IOException e) {
                System.err.println("Coulnd't receive message: " + e.getMessage());
            }
        }
        try {
            Connection.close();
        }
        catch (Exception e) {
            System.err.println("Coulnd't close client connection: " + e.getMessage());
        }
	}
	
	private void receiveMessage() throws IOException {
        String msg = Connection.receiveMessage();
        
        Gson gson = new GsonBuilder().create();
        PrinterStep step = gson.fromJson(msg, PrinterStep.class);        
        
        System.out.println("Message: " + step.toString());
    }
	
}
