
package controlpanel;

import java.util.ArrayList;

/*
 * PrinterQueue get's new Orders from the Central Dashboard
 *
 */

public class PrinterQueue {

    private ArrayList<Order> orders;
    private String displayName;

    private int currentOrderIndex = 0;

    public PrinterQueue(String body) {
        // TODO: 04.05.16 create constructionSteps with the body  
    }

    public String getNextStep() {
        Order currentOrder = this.orders.get(this.currentOrderIndex);
        return currentOrder.getNextStep();
    }

    public boolean hasNextStep() {
        if(this.currentOrderIndex < this.orders.size()) {
            return true;
        }
        return false;
    }

    public void addOrder(Order order) {
        this.orders.add(order);
    }


}