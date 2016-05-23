package server.connection;

import java.io.IOException;

/**
 * Created by Mr. Crapfruit on 20.11.2015.
 */

import java.net.*;

public class UDPConnection implements IConnection{
	
	int port;
	int portOfLastClient;
	int[] portOf = new int[4];
	
    DatagramPacket packetSend;
    DatagramPacket packetReceive;
    DatagramSocket socket;

    InetAddress addressOfLastClient;
    InetAddress[] addressOf = new InetAddress[4];
    int location;
    
	public UDPConnection(int port) throws Exception{
		this.port = port;
		
		connect();
	}

	@Override
	public void connect() throws IOException {
		socket = new DatagramSocket(port);
        System.out.println("UDPServer startet auf Port " + port);
	}

	@Override
	public void close() throws IOException {
        socket.close();
		
	}

	@Override
	public void tellServerToClose() throws IOException {
		return;
	}

	@Override
	public void sendMessage(String msg, int recipient) throws IOException {
        // Paket für Empfänger zusammenbauen
        String s = msg;
    	byte dataSend[] = new byte[1024]; // 128 chars in java
        dataSend = s.getBytes();
        System.out.println(addressOf[recipient] + " and port "+ portOf[recipient]);
        packetSend = new DatagramPacket(dataSend, dataSend.length, addressOf[recipient], portOf[recipient]);
        socket.send(packetSend);
        System.out.println("Send to Port: "+ portOf[recipient] + " sent message: " + s);
	}

	@Override
	public String receiveMessage() throws IOException {
		String message;
	    byte dataReceive[] = new byte[1024]; // 128 chars in java
		// Auf Anfrage warten
        packetReceive = new DatagramPacket(dataReceive, dataReceive.length);
        socket.receive(packetReceive);

        // Sender auslesen
        addressOfLastClient = packetReceive.getAddress();
        portOfLastClient = packetReceive.getPort();

        message = new String(packetReceive.getData());
        message = message.trim();
		return message;
	}
	
	@Override
	public int lastMessageFrom() {
		return portOfLastClient;
	}
	
	@Override
	public void waitForAllComponents() throws IOException{
		String message;
		
		for(int components = 0; components < 4;){
			message = receiveMessage();
			switch(message){
			case "drucker":
				if(addressOf[0] == null){
					components++;
					System.out.println(message + " verbunden");
				}
				addressOf[0] = addressOfLastClient;
				portOf[0] = portOfLastClient;
				break;
			case "container rot":
				if(addressOf[1] == null){
					components++;
					System.out.println(message + " verbunden");
				}
				addressOf[1] = addressOfLastClient;
				portOf[1] = portOfLastClient;
				break;
			case "container gruen":
				if(addressOf[2] == null){
					components++;
					System.out.println(message + " verbunden");
				}
				addressOf[2] = addressOfLastClient;
				portOf[2] = portOfLastClient;
				break;
			case "container blau":
				if(addressOf[3] == null){
					components++;
					System.out.println(message + " verbunden");
				}
				addressOf[3] = addressOfLastClient;
				portOf[3] = portOfLastClient;
				break;
			default:
				System.err.println("not a component");
			}			
		}
	}
}
