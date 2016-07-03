package controlpanel;

import com.google.gson.*;
import actions.*;
import server.connection.Connection;

import java.io.*;
import java.util.HashMap;
import java.util.concurrent.SynchronousQueue;

public class ControlPanel extends Thread {

	private String ctrlId;
	private String displayName;
	private Gson gson = new GsonBuilder().create();
    private String lastSender;

	private Order currentOrder = null;

    private boolean connected;
	private boolean isWorking = false;
	private int orderCounter = 0;
	private int orderCounterMax = 10;
	
	private Connection connection;

	private int updateCounterMax = 1;
	private int updateCounter = updateCounterMax;
	private HashMap<String, String> cartridge = new HashMap<String, String>();
	private final String container[] = {"red", "green", "blue"};
	
	private SynchronousQueue<String> startQueue;
	private SynchronousQueue<String> endQueue;
	private long startTime;
	private long endTime;


	public ControlPanel(int serverPort, boolean udp, String id, String name, SynchronousQueue<String> startQueue, SynchronousQueue<String> endQueue) throws Exception {
		connection = new Connection(serverPort, udp);
		this.displayName = name;
		this.ctrlId = id;
		this.startQueue = startQueue;
		this.endQueue = endQueue;
	}

	@Override
	public void run() {
		startTime = System.currentTimeMillis();
		String message;
		System.out.println("Thread started: " + this);	// Display Thread-ID
		
		initializeCartridge();
		
		try {

			connection.waitForAllComponents();
			this.connected = true;

			while(connected){
				if (isWorking) {
					message = ".";
					
						message = connection.receiveMessage();
					
					lastSender = connection.lastSender();
					//System.out.println("Received: " + message);
					if(message.equals(".")) {
						disconnect();
					} else {
						dispatchAction(message);						
					}
				} else {
					orderCounter++;
					if(orderCounter > orderCounterMax)
						disconnect();
					//String nextOrder = printerQQ.consumeOrder();
					String nextOrder = findWaitingOrder();

					currentOrder = null;
					currentOrder = gson.fromJson(nextOrder, Order.class);

					if(currentOrder != null) {
						//System.out.println("Consuming new Order | ID: " + currentOrder.getOrderId());

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

	private String findWaitingOrder() {
		String result = "{  \"orderId\": \"yc2DRgbq3GyZq3LXe\",  \"constructionSteps\": [    {      \"x\": 0,      \"y\": 0,      \"z\": 0,      \"draw\": false,      \"color\": \"#000\"    },    {      \"x\": 115,      \"y\": 37,      \"z\": 180,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 55,      \"y\": 27,      \"z\": 0,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 119,      \"y\": 133,      \"z\": 101,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 30,      \"y\": 14,      \"z\": 70,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 161,      \"y\": 14,      \"z\": 36,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 98,      \"y\": 162,      \"z\": 110,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 44,      \"y\": 4,      \"z\": 59,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 38,      \"y\": 57,      \"z\": 128,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 104,      \"y\": 46,      \"z\": 29,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 2,      \"y\": 46,      \"z\": 91,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 137,      \"y\": 104,      \"z\": 136,      \"draw\": true,      \"color\": \"#000\"    },    {      \"x\": 185,      \"y\": 162,      \"z\": 148,      \"draw\": true,      \"color\": \"#000\"    }  ]}";
		//System.out.print("Find waiting order ...");
		return result;
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
				// TODO: 14.06.16 send tank-status to the rest-api
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
			endTime = System.currentTimeMillis();
			
			try {
				startQueue.put("" + startTime);
				endQueue.put("" + endTime);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
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
		if(currentOrder.hasNextStep()) {
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
				//System.err.print("The server could not send the message to the client.");
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
    	String cartdrige =  gson.toJson(cartridge); // TODO: use the string for updating the cartridge in the dashboard
		//System.out.println("-----------------> " + cartdrige );
	}

}
