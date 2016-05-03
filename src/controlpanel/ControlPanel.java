package controlpanel;

import com.google.gson.Gson;
import action.Action;

import java.net.*;
import java.io.*;

public class ControlPanel extends Thread {

	private String displayName = '';
	private Socket client;

	public ControlPanel(String displayName, Socket client) {
		this.displayName = displayName;
		this.client = client;
	}

	@Override
	public void run() {
		String message;
		BufferedReader fromClient;
		DataOutputStream toClient;
		boolean connected = true;
		System.out.println("Thread started: " + this);                  // Display Thread-ID
		try{
			fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // Datastream FROM Client
			toClient = new DataOutputStream(client.getOutputStream());

			while(connected){
				message = fromClient.readLine();

				System.out.println("Received: "+ message);

				toClient.writeBytes("Ping-" + '\n');

				if (message.equals(".")) {
					connected = false;
				}
				else {
					dispatchAction(message);
				}

			}

			fromClient.close();
			toClient.close();
			client.close();
			System.out.println("Thread ended: " + this);

		}catch (IOException e){
			System.err.println(e);
		}


	}

	public void main (String args[]){
		
	}

	/*
	@message should be an json Object
	{
		"type": "error",
		"body": {
			"msg": "Printer is empty!"
		}
	}
	*/
	private void dispatchAction(String message) {

		String jsonTestObj = "{\"type\": \"error\", \"body\": \"some blabla. \"}";

		Gson gson = new Gson();
		Action action = gson.fromJson(jsonTestObj, Action.class);

		switch (action.type) {
			case "error":

				break;
			default:
				System.out.println("Error: Invalid action was dispatched!");
				break;
		}

	}
	
}
