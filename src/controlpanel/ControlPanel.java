package controlpanel;

import com.google.gson.*;
import actions.*;
import server.connection.Connection;

import java.io.*;
import java.util.HashMap;

public class ControlPanel extends Thread {

	private String displayName = "";
	private Gson gson = new GsonBuilder().create();
    private String lastSender;

	private Order currentOrder = null;
	private PrinterQueue printerQQ = new PrinterQueue();

    private boolean connected;
	private boolean isWorking = false;
	
	private Connection connection;

	private int updateCounterMax = 10;
	private int updateCounter = updateCounterMax;
	private HashMap<String, String> cartridge = new HashMap<String, String>();
	private final String container[] = {"red", "green", "blue"};
	
	public ControlPanel(int serverPort, boolean udp) throws Exception {
		connection = new Connection(serverPort, udp);
	}

	@Override
	public void run() {
		String message;
        this.connected = true;
		System.out.println("Thread started: " + this);	// Display Thread-ID
		
		initializeCartridge();
		
		try {
			connection.waitForAllComponents();

			while(connected){
				if (isWorking) {
					message = ".";
					message = connection.receiveMessage();
					lastSender = connection.lastSender();
					if(message.equals(".")){
						// TODO: 29.05.16 detailed error reporting
						System.out.println("Something went wrong. The printer couldn't finish this order. ");
						disconnect();
					} else {
						dispatchAction(message);						
					}
				} else {
					String nextOrder = printerQQ.consumeOrder();

					currentOrder = null;
					currentOrder = gson.fromJson(nextOrder, Order.class);

					if(currentOrder != null) {
						System.out.println("Consuming new Order | ID: " + currentOrder.getOrderId());
						proceedStep();
						this.isWorking = true;
					}

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
				proceedStep();
                break;

			case "STATUS_MESSAGE":
				// Wenn eine Statusnachricht von den Materialbehaeltern kommt
				if(!lastSender.equals("drucker"))
				{
					cartridge.put(lastSender, action.getBody());
				}
				break;

			case "ERROR":

				break;
			case "PING":
				//System.out.println("got Ping from " + lastSender);
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

	private void proceedStep() {
		if(currentOrder != null && currentOrder.hasNextStep()) {
			System.out.println(currentOrder.getWorkingProgress());
			String nextStep = currentOrder.getNextStep();
			try {
				sendMessage(nextStep, 0);
				
				// update cycle for cartridge
				updateCounter--;
				if(updateCounter < 0){
					updateCounter = updateCounterMax;
					updateCartridge();
				}
				
				currentOrder.incrementStepIndex();
			}
			catch(Exception e) {
				System.err.print("The server could not send the message to the client.");
			}
		}
		else {
			this.isWorking = false;
		}
	}
	
	private void initializeCartridge(){
		cartridge.put(container[0], ""+100);
		cartridge.put(container[1], ""+100);
		cartridge.put(container[2], ""+100);
	}
	
	private void updateCartridge(){
    	Gson gson = new GsonBuilder().create();
    	gson.toJson(cartridge); // TODO: use the string for updating the cartridge in the dashboard

	}

}
