package printfactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import controlpanel.ControlPanel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class StartAllControlPanels {

    private static Gson gson = new GsonBuilder().create();
	
	public static void main(String[] args) throws Exception {
        int port = 25565;
        int amountCP = 10;
        boolean udp = false;
        try {
            /**
             * calling rest API to get the IDs for the controlpanels
             */
            URL url = new URL("http://localhost:3000/methods/get-all-ids");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setDoInput( true );
            connection.setDoOutput( true );
            connection.setUseCaches( false );
            connection.setRequestProperty( "Content-Type", "application/json" );
            connection.setRequestProperty( "Content-Length", String.valueOf(0) );

            String resultString = "";
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()) );
            for ( String line; (line = reader.readLine()) != null; )
            {
                resultString += line;
            }

            reader.close();

            List<PanelID> resultList = gson.fromJson(resultString, new TypeToken<List<PanelID>>(){}.getType());

            for(int i = 0; i < amountCP; i++){
                String id = resultList.get(i)._id;
                String name = resultList.get(i).name;
                new ControlPanel(port, udp, id, name).start();
                port++;
            }
        }
        catch(Exception e) {

        }

    }


}
