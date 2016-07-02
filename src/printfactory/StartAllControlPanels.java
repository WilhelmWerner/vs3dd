package printfactory;

import controlpanel.ControlPanel;

public class StartAllControlPanels {
	
	public static void main(String[] args) throws Exception {
        int port = 25565;
        int amountCP = 1;
        boolean udp = true;
        int controlpanelID = 0;
        try {

            for(int i = 0; i < amountCP; i++){
                String id = "" + (++controlpanelID);
                String name = "controlpanel" + (controlpanelID);
                System.out.println(name + " " + id);
                new ControlPanel(port, udp, id, name).start();
                port++;
            }
        }
        catch(Exception e) {

        }

    }


}
