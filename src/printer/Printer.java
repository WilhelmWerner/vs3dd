package printer;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import actions.Action;
import client.connection.Connection;
import controlpanel.ConstructionStep;

public class Printer extends Thread{

    private Connection connection;
    private boolean connected = false;
    private int port = 0;
    private String host = "";
    private ConstructionStep step;
    private boolean udp;
    private String name;
    
    // Materialbehaelter

	// Array mit allen Materialbehaeltern
	private MaterialContainer[] allMC;
	// Array  mit allen Threads für die Materialbehaelter
	private Thread[] allMCThreads;
	// Farben der Behaelter
	private final String colors[] = {"red", "green", "blue"};
	
	public Printer(String host, int port, boolean udp, int amountMC){
		this.host = host;
		this.port = port;
		this.udp = udp;
		this.name = "drucker";

		allMC = new MaterialContainer[amountMC];
		allMCThreads = new Thread[amountMC];
		
		for(int j = 0; j < amountMC; j++){
			allMC[j] = new MaterialContainer(host, port, udp, colors[j]);
			allMCThreads[j] = new Thread(allMC[j]);
			allMCThreads[j].start();
		}	
	}
	
	public void run(){
		try{
            connection = new Connection(host, port, udp, name);
            connection.connect();
            connected = true;
            connection.sendMessage("drucker");
        } catch(Exception e) {
            System.err.println("Connection to Server failed: " + e.getMessage());
        }
		
		while(connected) {
            try {
                receiveMessage();
                if(proceedStep()){
                	System.out.println("Send success");
                	sendMessage("SUCCESS_STEP");
                } else {
                	sendMessage("ERROR");
                }

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
	
	private void sendMessage(String msg) throws IOException{
        Action a = new Action(msg);
    	Gson gson = new GsonBuilder().create();
    	connection.sendMessage(gson.toJson(a));
	}
	
}
