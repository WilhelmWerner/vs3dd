package printer;

import java.io.IOException;

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
        PrinterStep step = Connection.receiveMessage();
        System.out.println("Message: " + step.toString());
    }
	
}
