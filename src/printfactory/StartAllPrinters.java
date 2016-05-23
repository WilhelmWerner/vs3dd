package printfactory;

import printer.*;

public class StartAllPrinters {
	// Menge der Drucker
	private int amountP = 1;
	// Menge der Materialbehaelter pro Drucker
	private int amountMC = 3;
	// Array mit allen Druckern
	private Printer[] allPrinters = new Printer[amountP];
	// Array mit allen Threads für die Drucker
	private Thread[] allPThreads = new Thread[amountP];
	// Array mit allen Materialbehaeltern
	private MaterialContainer[][] allMC = new MaterialContainer[amountP][amountMC];
	// Array  mit allen Threads für die Materialbehaelter
	private Thread[][] allMCThreads = new Thread[amountP][amountMC];
	
	private String host = "localhost";
	private int port = 25565;
	private boolean udp = false;
	private final String colors[] = {"rot", "gruen", "blau"};
	
	// startet alle Drucker und deren Materialbehaelter in eigenen Threads
	public StartAllPrinters(){
		for(int i = 0; i < amountP; i++){
			allPrinters[i] = new Printer(host, port, udp);
			allPThreads[i] = new Thread(allPrinters[i]);
			allPThreads[i].start();
			for(int j = 0; j < amountMC; j++){
				allMC[i][j] = new MaterialContainer(host, port, udp, colors[j]);
				allMCThreads[i][j] = new Thread(allMC[i][j]);
				allMCThreads[i][j].start();
			}	
			port++;
		}	
	}

	public static void main(String args[]){
		new StartAllPrinters();
	}

	
}
