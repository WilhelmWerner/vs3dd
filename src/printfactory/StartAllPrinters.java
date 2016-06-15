package printfactory;

import printer.*;

public class StartAllPrinters {
	// Menge der Drucker
	private int amountP = 5;
	// Menge der Materialbehaelter pro Drucker
	private int amountMC = 3;
	// Array mit allen Druckern
	private Printer[] allPrinters = new Printer[amountP];
	// Array mit allen Threads für die Drucker
	private Thread[] allPThreads = new Thread[amountP];
	
	private String host = "localhost";
	private int port = 25565;
	private boolean udp = false;
	
	// startet alle Drucker und deren Materialbehaelter in eigenen Threads
	public StartAllPrinters(){
		for(int i = 0; i < amountP; i++){
			allPrinters[i] = new Printer(host, port, udp, amountMC);
			allPThreads[i] = new Thread(allPrinters[i]);
			allPThreads[i].start();
			port++;
		}	
	}

	public static void main(String args[]){
		new StartAllPrinters();
	}

	
}
