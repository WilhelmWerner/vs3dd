package server.connection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import actions.Action;

public class TCPReceiver extends Thread{

	private BufferedReader receiver;
	private DataOutputStream sender;
	private SynchronousQueue<String> queue;
	private SynchronousQueue<String> senderQueue;
	private String message;
	private String name;

	private Gson gson = new GsonBuilder().create();
	
	public TCPReceiver(String name, BufferedReader receiver, DataOutputStream sender, SynchronousQueue<String> queue, SynchronousQueue<String> senderQueue){
		this.name = name;
		this.receiver = receiver;
		this.sender = sender;
		this.queue = queue;
		this.senderQueue = senderQueue;
	}
	
	public void run() {
		
		while(true){
			
			try {
				message = receiver.readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			Action action = this.gson.fromJson(message, Action.class);

			switch (action.getType()) {
			case "PING":
				try {
					// fast answer to ping
					// System.out.println("ping erhalten von " + name);
					sender.writeBytes("PING" + '\n');
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				break;
			default:
				try {
					// regular message handling
					queue.put(message);
					senderQueue.put(name);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
			
		}
		
	}
}
