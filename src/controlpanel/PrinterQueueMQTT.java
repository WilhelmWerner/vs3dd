package controlpanel;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import static com.google.common.net.HttpHeaders.USER_AGENT;

/**
 * Created by Wilhelm and Marcel
 */
public class PrinterQueueMQTT {

    private MqttClient client = null;

    private static Vector<String> orderIds = new Vector<>();
    private String topic;
    private String clientId;
    int qos = 2; // 0 = at most once, 1 = at least once, 2 = exactly once
    private String broker = "tcp://188.107.131.18:1883";
    MemoryPersistence persistence = new MemoryPersistence();



    public PrinterQueueMQTT(String topic, String clientId) {
        this.topic = topic;
        this.clientId = clientId;
        try {
            client = new MqttClient(broker, clientId, persistence);
            MqttCallback mqttCallback = new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    String id = (String) new String(mqttMessage.getPayload());
                    orderIds.add(id);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            };
            client.setCallback(mqttCallback);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            System.out.println("Connecting to broker: "+broker);
            client.connect(connOpts);
            System.out.println("Connected");
            client.subscribe(topic);
        } catch(MqttException me) {
            System.out.println("reason "+me.getReasonCode());
            System.out.println("msg "+me.getMessage());
            System.out.println("loc "+me.getLocalizedMessage());
            System.out.println("cause "+me.getCause());
            System.out.println("excep "+me);
            me.printStackTrace();
        }
    }

    public String getNewOrder() {
        if(!orderIds.isEmpty()) {
            try {
                String id = orderIds.firstElement();
                orderIds.removeElementAt(0);
                String nextOrder = getOrderById(id);
                System.out.print("new order: " + nextOrder);
                return nextOrder;
            }
            catch (Exception e) {
                System.err.println("Could not get order by ID: "  + e.getMessage());
            }
        }
        return null;
    }

    private String getOrderById(String id) throws Exception {
        String url = "http://localhost:3000/blueprint/" + id;

        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        // optional default is GET
        con.setRequestMethod("GET");

        //add request header
        con.setRequestProperty("User-Agent", USER_AGENT);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        //print result
        System.out.println(response.toString());


        return response.toString();
    }
}
