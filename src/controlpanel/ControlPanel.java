package controlpanel;

import com.google.gson.Gson;
import actions.*;
import com.google.gson.GsonBuilder;

import java.net.*;
import java.io.*;

public class ControlPanel extends Thread {

	private String displayName = "";
	private Socket client;
	private Gson gson = new GsonBuilder().create();
    private PrinterQueue printerQQ;

    boolean connected;
	boolean hasNextSteps = false;
    private BufferedReader fromClient;
    private DataOutputStream toClient;

	public ControlPanel(Socket client) {
		this.client = client;
	}

	@Override
	public void run() {
		String message;
        this.connected = true;
		System.out.println("Thread started: " + this);	// Display Thread-ID
		try{
			this.fromClient = new BufferedReader(new InputStreamReader(client.getInputStream())); // Datastream FROM Client
			this.toClient = new DataOutputStream(client.getOutputStream());

			while(connected){
				if (hasNextSteps) {
					message = fromClient.readLine();

					System.out.println("Received: " + message);

					if (message.equals(".")) {
						connected = false;
					} else {
						dispatchAction(message);
					}
				}else{
					// TODO: 13.05.16 waiting for new Orders, set hasNextSteps to false
					Reader reader = null;
					try {
						reader = new FileReader("jsonTestFiles/order.json");
						Order order = gson.fromJson(reader, Order.class);
						this.printerQQ.addOrder(order);
					}
					catch(Exception e) {
						System.err.print("could not read the json file");
					}
					message = new Action("SUCCESS_STEP").toString();
					this.hasNextSteps = true;
				}
			}

			fromClient.close();
			toClient.close();
			client.close();
			System.out.println("Thread ended: " + this);

		} catch (IOException e) {
			System.err.println("could not connect");
		}


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
				successStep();
                break;

			case "STATUS_MESSAGE":

				break;

			case "ERROR":

				break;
			default:
				System.out.println("Error: Invalid action was dispatched!");
				break;
		}

	}

    private void createPrinterQueue(String body) {
        this.printerQQ = new PrinterQueue(body);
    }

    private void disconnectPrinter() throws Exception {
		if(this.connected) {
			this.connected = false;
			this.fromClient.close();
			this.toClient.close();
			this.client.close();
		}
    }

	/*
	 @message should be an json Object
	 */
	private void sendMessage(String message) throws Exception {
		String jsonMsg = this.gson.toJson(message);
		toClient.writeBytes(jsonMsg);
	}

	private void successStep() {

		if(printerQQ.hasNextStep()) {
			String nextStep = printerQQ.getNextStep();
			try {
				sendMessage(nextStep);
			}
			catch(Exception e) {
				System.err.print("The server could not send the message to the client.");
			}
		}
		else {
			this.hasNextSteps = false;
		}


	}
	
}
