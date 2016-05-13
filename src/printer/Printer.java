package printer;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import actions.Action;
import connection.Connection;
import controlpanel.ConstructionStep;

public class Printer extends Thread{

    private Connection Connection;
    private boolean connected = false;
    private int port = 0;
    private String host = "";
    private ConstructionStep step;
	
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
                if(proceedStep()){
                	sendMessage(new Action("SUCCESS_STEP").toString());
                } else {
                	sendMessage(new Action("ERROR").toString());
                }
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
        step = gson.fromJson(msg, ConstructionStep.class);
    }
	
	private boolean proceedStep(){
        System.out.println("Message: " + step.toString());
        
        try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
        return true;
	}
	
	private void sendMessage(String msg){
		
	}
	
}
