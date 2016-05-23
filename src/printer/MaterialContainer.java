package printer;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import actions.Action;
import client.connection.Connection;
import controlpanel.ConstructionStep;

public class MaterialContainer extends Thread{

    private Connection connection;
    private boolean connected = false;
    private int port = 0;
    private String host = "";
    private ConstructionStep step;
    private boolean udp;
    private String color = "rot";
	
	public MaterialContainer(String host, int port, boolean udp, String color){
		this.host = host;
		this.port = port;
		this.udp = udp;
		this.color = color;
	}
	
	public void run(){
		try{
            connection = new Connection(host, port, udp);
            connection.connect();
            connected = true;
            connection.sendMessage("container " + color);
        } catch(Exception e) {
            System.err.println("Connection to Server failed: " + e.getMessage());
        }
		
		while(connected) {
            try {
                receiveMessage();
                Action a = new Action();
                if(proceedStep()){
                	System.out.println("Send success");

                	a = new Action("SUCCESS_STEP");
                } else {
                	a = new Action("ERROR");
                }

            	Gson gson = new GsonBuilder().create();
            	

            	connection.sendMessage(gson.toJson(a));
            }
            catch (IOException e) {
                System.err.println("Coulnd't receive message: " + e.getMessage());
            }
        }
        try {
            connection.close();
        }
        catch (Exception e) {
            System.err.println("Coulnd't close client connection: " + e.getMessage());
        }
	}
	
	private void receiveMessage() throws IOException {
        String msg = connection.receiveMessage();
        
        Gson gson = new GsonBuilder().create();
        step = gson.fromJson(msg, ConstructionStep.class);
    }
	
	private boolean proceedStep(){
        System.out.println("Message: " + step.toString());
        
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
        return true;
	}
	
	private void sendMessage(String msg){
		
	}
	
}

