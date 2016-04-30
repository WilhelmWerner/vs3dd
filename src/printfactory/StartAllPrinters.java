package printfactory;

import printer.*;

public class StartAllPrinters {
	// Menge der Drucker
	int amountP = 10;
	// Menge der Materialbehaelter pro Drucker
	int amountMC = 3;
	// Array mit allen Druckern
	Printer[] allPrinters = new Printer[amountP];
	// Array mit allen Threads für die Drucker
	Thread[] allPThreads = new Thread[amountP];
	// Array mit allen Materialbehaeltern
	MaterialContainer[][] allMC = new MaterialContainer[amountP][amountMC];
	// Array  mit allen Threads für die Materialbehaelter
	Thread[][] allMCThreads = new Thread[amountP][amountMC];
	
	// startet alle Drucker und deren Materialbehaelter in eigenen Threads
	public StartAllPrinters(){
		for(int i = 0; i < amountP; i++){
			allPrinters[i] = new Printer();
			allPThreads[i] = new Thread(allPrinters[i]);
			allPThreads[i].start();
			for(int j = 0; j < amountMC; j++){
				allMC[i][j] = new MaterialContainer();
				allMCThreads[i][j] = new Thread(allMC[i][j]);
				allMCThreads[i][j].start();
			}	
		}	
	}

	public static void main(String args[]){
		new StartAllPrinters();
	}

	
}
