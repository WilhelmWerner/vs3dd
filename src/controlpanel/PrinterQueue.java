
package controlpanel;

/*
 * gets new Orders from Activemq /consume/order
 *
 */

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class PrinterQueue {

    private ConnectionFactory factory = null;
    private Connection connection;
    private Session session;
    private Destination destination;
    private MessageConsumer consumer = null;

    public PrinterQueue() {
        try {
            factory = new ActiveMQConnectionFactory(ActiveMQConnection.DEFAULT_BROKER_URL);
            connection = factory.createConnection();
            connection.start();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            destination = session.createQueue("order");
            consumer = session.createConsumer(destination);
        }
        catch (JMSException e) {
            System.err.print("Could not consume a new order: " + e.getMessage());
        }
    }

    public String consumeOrder() {
        try {
            System.out.println("Trying to consume new order ... ");

            Message message = consumer.receive();

            // TODO: 29.05.16 use Json instance if possible
            if(message instanceof TextMessage) {
                TextMessage text = (TextMessage) message;
                System.out.print("nextOrder: " + text.getText());
                return text.getText();
            }
        }
        catch (JMSException e) {
            System.err.print("Could not consume a new order: " + e.getMessage());
        }

        return "nothing to consume";
    }


}