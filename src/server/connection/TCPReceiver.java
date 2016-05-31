package server.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.SynchronousQueue;

public class TCPReceiver extends Thread{

	BufferedReader receiver;
	SynchronousQueue<String> queue;
	SynchronousQueue<String> senderQueue;
	String message;
	String name;
	
	public TCPReceiver(String name, BufferedReader receiver, SynchronousQueue<String> queue, SynchronousQueue<String> senderQueue){
		this.name = name;
		this.receiver = receiver;
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
			
			try {
				queue.put(message);
				senderQueue.put(name);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		
	}
}
