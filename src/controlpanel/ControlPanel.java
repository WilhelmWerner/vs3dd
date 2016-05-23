package controlpanel;

import com.google.gson.*;
import actions.*;
import server.connection.Connection;

import java.net.*;
import java.io.*;

public class ControlPanel extends Thread {

	private String displayName = "";
	private Socket client;
	private Gson gson = new GsonBuilder().create();
    private PrinterQueue printerQQ = new PrinterQueue();

    boolean connected;
	boolean hasNextSteps = true;
	
	Connection connection;

	public ControlPanel(int serverPort, boolean udp) throws Exception {
		connection = new Connection(serverPort, udp);
	}

	@Override
	public void run() {
		String message;
        this.connected = true;
		System.out.println("Thread started: " + this);	// Display Thread-ID
		try {
			connection.waitForAllComponents();
			
			readTestFile();
			successStep();

			while(connected){
				if (hasNextSteps) {
					message = ".";
					message = connection.receiveMessage();
					System.out.println("Received: " + message);

					if (message.equals(".")) {
						connected = false;
					} else if (message.equals("ping")){
						//ignore for the moment
					}
					else {
						dispatchAction(message);
					}
				} else {
					// TODO: 13.05.16 waiting for new Orders, set hasNextSteps to false

				}
			}

			connection.close();
			System.out.println("Thread ended: " + this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readTestFile() {
		Reader reader = null;
		try {
			reader = new FileReader("jsonTestFiles/order.json");
			Order order = gson.fromJson(reader, Order.class);
			this.printerQQ.addOrder(order);
		}
		catch(Exception e) {
			System.err.print("could not read the json file");
		}
		this.hasNextSteps = true;
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
		Action action = this.gson.fromJson(message, Action.class);

		switch (action.getType()) {

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

    private void disconnectPrinter() throws Exception {
		if(this.connected) {
			this.connected = false;
			this.connection.close();
		}
    }

	/**
	 * 
	 * @param message should be an json Object
	 * @throws Exception
	 */
	private void sendMessage(String message, int recipient) throws Exception {
		connection.sendMessage(message + "\n", recipient);
	}

	private void successStep() {

		if(printerQQ.hasNextStep()) {
			String nextStep = printerQQ.getNextStep();
			try {
				sendMessage(nextStep, 0);
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
