package client.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import actions.Action;

public class TCPReceiver extends Thread{

	private BufferedReader receiver;
	private DataOutputStream sender;
	private SynchronousQueue<String> queue;
	private String message;
	private String name;
	
	private long timeForPing = new Date().getTime();

	private Gson gson = new GsonBuilder().create();
	
	public TCPReceiver(String name, BufferedReader receiver, DataOutputStream sender, SynchronousQueue<String> queue){
		this.name = name;
		this.receiver = receiver;
		this.sender = sender;
		this.queue = queue;
	}
	
	public void run() {
		
		while(true){
			
			try {
				message = receiver.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			switch (message) {
			case "PING":
				// fast answer to ping
				setTimeForPing(new Date().getTime() - getTimeForPing()); 
				System.out.println("Der Ping von " + name + " hat " + ((timeForPing - 5000) < 1 ? "weniger als 1" : (timeForPing - 5000)) + " ms gebraucht");
				setTimeForPing(new Date().getTime());
				break;
			default:
				try {
					// regular message handling
					queue.put(message);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			
		}
		
	}

	public long getTimeForPing() {
		return timeForPing;
	}

	public void setTimeForPing(long timeForPing) {
		this.timeForPing = timeForPing;
	}
}
