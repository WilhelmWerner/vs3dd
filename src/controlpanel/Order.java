package controlpanel;

import java.util.ArrayList;

/**
 * Created by trafy on 06.05.16.
 */
public class Order {

    private String orderId;
    private ArrayList<ConstructionStep> constructionSteps = new ArrayList<>();

    private int currentStepIndex = 0;

    public Order() {

    }

    public boolean hasNextStep() {
        if(this.currentStepIndex < constructionSteps.size()) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return constructionStep as json
     */
    public String getNextStep() {
        return this.constructionSteps.get(this.currentStepIndex).toString();
    }

    public String getOrderId() {
        return this.orderId;
    }

    public void incrementStepIndex() {
        ++this.currentStepIndex;
    }

    public String getWorkingProgress() {
        int total = this.constructionSteps.size();
        String pre = "Order [" + this.orderId + "] progress ";
        String progress = pre + this.currentStepIndex + "/" + total;
        return progress;
    }

    public String toString() {
        String steps = "";

        for (int i = 0; i < constructionSteps.size(); ++i) {
            steps += constructionSteps.get(this.currentStepIndex).toString();
            steps += ", ";
        }

        return this.orderId + ", " + steps;
    }

}
