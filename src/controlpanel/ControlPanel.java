package controlpanel;

import com.google.gson.Gson;
import actions.*;

import java.net.*;
import java.io.*;

public class ControlPanel extends Thread {

	private String displayName = '';
	private Socket client;
	private GSON gson = new Gson();
    private PrinterQueue printerQQ;

    boolean connected;
    private BufferedReader fromClient;
    private DataOutputStream toClient;

	public ControlPanel(String displayName, Socket client) {
		this.displayName = displayName;
		this.client = client;
	}

	@Override
	public void run() {
		String message;
        this.connected = true;
		System.out.println("Thread started: " + this);                  // Display Thread-ID
		try{
			this.fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // Datastream FROM Client
			this.toClient = new DataOutputStream(client.getOutputStream());

			while(connected){
				message = fromClient.readLine();

				System.out.println("Received: "+ message);

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

		} catch (IOException e) {
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

		Action action = this.gson.fromJson(jsonTestObj, Action.class);

		switch (action.type) {

            // send next step, if there are no steps left tell the printer to finish +
            // send next order step by step
            case "SUCCESS_STEP":

                break;

            case "CONNECT_PRINTER":

                break;

            case "DISCONNECT_PRINTER":
                this.disconnectPrinter();
                break;

			case "ERROR":

				break;
			default:
				System.out.println("Error: Invalid action was dispatched!");
				break;
		}

	}

    private void connectPrinter(String body) {
        ConnectPrinter action = this.gson.fromJson(body, ConnectPrinter.class);
        this.printerQQ = new PrinterQueue(action.displayName);
    }

    private void disconnectPrinter() {
        this.connected = false;
        this.fromClient.close();
        this.toClient.close();
        this.client.close();
    }

	/*
	 @message should be an json Object
	 */
	private void toClient(String message) {
		String jsonMsg = this.gson.toJson(message);
		this.toClient.writeBytes(jsonMsg + "\n");
	}
	
}
