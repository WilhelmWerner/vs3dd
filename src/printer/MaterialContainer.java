package printer;

import java.io.IOException;
import java.util.Date;

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
    private String name = "rot";
    private int cartridge = 1000; // angegeben in Promille wird aber in Prozent versendet
    
    private long timeForPing;
	private String message;
    
	public MaterialContainer(String host, int port, boolean udp, String color){
		this.host = host;
		this.port = port;
		this.udp = udp;
		this.name = color;
	}
	
	public void run(){
		try{
            connection = new Connection(host, port, udp, name);
            connection.connect();
            connected = true;
            connection.sendMessage(name);
        } catch(Exception e) {
            System.err.println("Connection to Server failed: " + e.getMessage());
        }
		
		while(connected) {
            try {
                Thread.sleep(5000);
            	//sendMessage("PING");
            	// TODO: real decreasing misses
            	decreaseCartridge(35);
            	sendCartridge();
            }
            catch (IOException e) {
                System.err.println("Coulnd't send message: " + e.getMessage());
                try {
                    connection.close();
                    connected = false;
                }
                catch (Exception ex) {
                    System.err.println("Coulnd't close client connection: " + ex.getMessage());
                }
            } catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
        try {
            connection.close();
        }
        catch (Exception e) {
            System.err.println("Coulnd't close client connection: " + e.getMessage());
        }
	}
	
	private String receiveMessage() throws IOException {
        String msg = connection.receiveMessage();
        return msg;
    }
	
	private void sendMessage(String msg) throws IOException{
        Action a = new Action(msg);
    	Gson gson = new GsonBuilder().create();
    	connection.sendMessage(gson.toJson(a));
	}
	
	private void sendCartridge() throws IOException{
        Action a = new Action("STATUS_MESSAGE", "" + (cartridge/10)); // Fuellstand wird in Prozent versendet
    	Gson gson = new GsonBuilder().create();
    	connection.sendMessage(gson.toJson(a));
		
	}
	
	private void decreaseCartridge(int dec){
		cartridge -= dec;
		if(cartridge < 0)
		{
			cartridge = 1000;			
		}
	}
	
	
}

