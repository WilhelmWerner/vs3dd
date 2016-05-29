package restAPI;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * Created by Wilhelm Werner und Marcel Öhlenschläger
 */

@Path("QQ")
public class PrinterQQ {

    private ConnectionFactory factory = null;
    private Connection connection = null;
    private Session session = null;
    private Destination destination = null;
    private MessageProducer producer = null;

    @POST
    @Path("/order")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public String postOrder(String data) {
        System.out.print(data);

        try {
            factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("order");
            producer = session.createProducer(destination);
            TextMessage message = session.createTextMessage();
            message.setText(data);
            System.out.print("Sending ... " + message.getText());
        }
        catch (JMSException e) {
            System.err.print("Could not produce a new order: " + e.getMessage());
        }

        return "success order";
    }
}
