package controlpanel;

import com.google.gson.*;
import actions.*;
import server.connection.Connection;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

public class ControlPanel extends Thread {

	private String ctrlId;
	private String displayName;
	private Gson gson = new GsonBuilder().create();
    private String lastSender;

	private Order currentOrder = null;
	//private PrinterQueue printerQQ = new PrinterQueue();
	private PrinterQueueMQTT mqttQQ = null;

    private boolean connected;
	private boolean isWorking = false;
	
	private Connection connection;

	public class RestMsg {
		public String id;
		public String data;
	}


	private int updateCounterMax = 1;
	private int updateCounter = updateCounterMax;
	private HashMap<String, String> cartridge = new HashMap<String, String>();
	private final String container[] = {"red", "green", "blue"};


	public ControlPanel(int serverPort, boolean udp, String id, String name) throws Exception {
		connection = new Connection(serverPort, udp);
		this.displayName = name;
		this.ctrlId = id;
		this.mqttQQ = new PrinterQueueMQTT(id);
	}

	@Override
	public void run() {
		String message;
		System.out.println("Thread started: " + this);	// Display Thread-ID
		
		initializeCartridge();
		
		try {

			connection.waitForAllComponents();
			this.connected = true;
			sendRestMsg("update-printer-status", this.ctrlId, "online");

			while(connected){
				if (isWorking) {
					message = ".";
					message = connection.receiveMessage();
					lastSender = connection.lastSender();
					System.out.println("Received: " + message);
					if(message.equals(".")) {
						disconnect();
					} else {
						dispatchAction(message);						
					}
				} else {
					//String nextOrder = printerQQ.consumeOrder();
					//String nextOrder = findWaitingOrder();
					String nextOrder = mqttQQ.getNewOrder();

					if( nextOrder != null) {
						currentOrder = null;
						currentOrder = gson.fromJson(nextOrder, Order.class);

						if(currentOrder != null) {
							System.out.println("Consuming new Order | ID: " + currentOrder.getOrderId());

							sendRestMsg("update-order-status", currentOrder.getOrderId(), "in progress");
							sendRestMsg("update-current-order", this.ctrlId, currentOrder.getOrderId());

							proceedStep();
							this.isWorking = true;
						}
					}

				}
			}

			System.out.println("Thread ended: " + this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String findWaitingOrder() {
		String result;
		System.out.print("Find waiting order ...");
		while(true) {
			result = sendRestMsg("find-waiting-order", "", "");
			if(!result.contains("nothing")) {
				return result;
			}
			try {
				sleep(10000);
			} catch(Exception e) {

			}
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

	private String sendRestMsg(String path, String id, String data)  {
		try {
			RestMsg restMsg = new RestMsg();
			restMsg.id = id;
			restMsg.data = data;
			String body = gson.toJson(restMsg);
			URL url = new URL("http://localhost:3000/methods/" + path);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Content-Length", String.valueOf(body.length()));

			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			writer.write(body);
			writer.flush();

			String result = "";
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			for (String line; (line = reader.readLine()) != null; ) {
				System.out.println(line);
				result += line;
			}

			writer.close();
			reader.close();
			return result;
		}
		catch(Exception e) {
			System.err.print("Could not send message to the rest-server: " + e.getMessage());
		}
		return null;
	}

    private void disconnect() {
		if(this.connected) {
			this.connected = false;
			
			try {
				this.connection.close();
				sendRestMsg("update-printer-status", this.ctrlId, "offline");
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
				sendRestMsg( ("update-pending-steps"), this.ctrlId, String.valueOf(currentOrder.getWorkingProgress()));
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
			sendRestMsg("update-order-status", currentOrder.getOrderId(), "finished");
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
		System.out.println("-----------------> " + cartdrige );
		sendRestMsg("update-printer-cartdrige", this.ctrlId, cartdrige);
	}

}
