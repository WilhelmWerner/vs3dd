package printfactory;

import controlpanel.ControlPanel;

public class StartAllControlPanels {
	
	public static void main(String[] args) throws Exception {
        int port = 25565;
        int amountCP = 3;
        for(int i = 0; i < amountCP; i++){
            new ControlPanel(port, true).start();
        	port++;
        }
    }

}
