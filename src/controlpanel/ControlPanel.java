package controlpanel;

import com.google.gson.*;
import actions.*;
import server.connection.Connection;

import java.io.*;

public class ControlPanel extends Thread {

	private String displayName = "";
	private Gson gson = new GsonBuilder().create();
    private String lastSender;

	private Order currentOrder = null;
	private PrinterQueue printerQQ = new PrinterQueue();

    boolean connected;
	boolean isWorking = false;
	
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

			while(connected){
				if (isWorking) {
					message = ".";
					message = connection.receiveMessage();
					lastSender = connection.lastSender();
					System.out.println("Received: " + message);
					if(message.equals(".")){
						if (isWorking) {
							// TODO: 29.05.16 detailed error reporting
							System.out.println("Something went wrong. The printer couldn't finish this order. ");
						}
						disconnect();
					} else {
						dispatchAction(message);						
					}
				} else {
					// TODO: 29.05.16 consume next Order from activemq /consume/order
					String nextOrder = printerQQ.consumeOrder();
					System.out.print("Control panel received: " + nextOrder);

					currentOrder = null;
					currentOrder = gson.fromJson(nextOrder, Order.class);

					successStep();
					this.isWorking = true;
				}
			}

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
		}
		catch(Exception e) {
			System.err.print("could not read the json file");
		}
		this.isWorking = true;
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
			case "PING":
				System.out.println("got Ping from " + lastSender);
				break;
			default:
				System.out.println("Error: Invalid action was dispatched!");
				break;
		}

	}

    private void disconnect() {
		if(this.connected) {
			this.connected = false;
			
			try {
				this.connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
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
		if(currentOrder != null && currentOrder.hasNextStep()) {
			String nextStep = currentOrder.getNextStep();
			try {
				sendMessage(nextStep, 0);
			}
			catch(Exception e) {
				System.err.print("The server could not send the message to the client.");
			}
		}
		else {
			this.isWorking = false;
		}
	}

}
