package printfactory;

import java.io.PrintWriter;
import java.util.Date;
import java.util.concurrent.SynchronousQueue;

import controlpanel.ControlPanel;

public class StartAllControlPanels {
	
	public static void main(String[] args) throws Exception {
		SynchronousQueue<String> startQueue = new SynchronousQueue<String>();
		SynchronousQueue<String> endQueue = new SynchronousQueue<String>();
        int port = 25565;
        int amountCP = 1000;
        boolean udp = false;
        int controlpanelID = 0;
        try {

            for(int i = 0; i < amountCP; i++){
                String id = "" + (++controlpanelID);
                String name = "controlpanel" + (controlpanelID);
                System.out.println(name + " " + id);
                new ControlPanel(port, udp, id, name, startQueue, endQueue).start();
                port++;
            }
            
            long startTime = System.currentTimeMillis();
            long endTime = System.currentTimeMillis();;
            String bufferStartTime;
            String bufferEndTime;
            
            for(int i = 1; i < (amountCP+1); i++){
            	bufferStartTime = startQueue.take();
            	bufferEndTime = endQueue.take();
            	System.out.println("Bisher haben " + i + " Controlpanel ihre Aufträge bearbeitet.");
            	if(Long.parseLong(bufferStartTime) < startTime)
            		startTime = Long.parseLong(bufferStartTime);
            	if(Long.parseLong(bufferEndTime) > endTime)
            		endTime = Long.parseLong(bufferEndTime);
            }
            System.out.println("Es hat " + ((endTime - startTime)/1000) + " sekunden gedauert, bis alle Aufträge abgearbeitet waren" );
            
            PrintWriter writer;
            
            if(udp)
            {
            	writer = new PrintWriter("stats_UDP.txt", "UTF-8");
            } else
            {
            	writer = new PrintWriter("stats_TCP.txt", "UTF-8");
            }
            
            writer.println("Es hat " + ((endTime - startTime)/1000) + " sekunden gedauert, bis alle Aufträge abgearbeitet waren");
            writer.println("Startzeit: " + new Date(startTime));
            writer.println("Endzeit: " + new Date(endTime));
            writer.close();
            
        }
        catch(Exception e) {

        }

    }


}
