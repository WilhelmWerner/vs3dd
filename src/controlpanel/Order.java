package controlpanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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

    public String toString() {
        String steps = "";

        for (int i = 0; i < constructionSteps.size(); ++i) {
            steps += constructionSteps.get(this.currentStepIndex).toString();
            steps += ", ";
        }

        return this.orderId + ", " + steps;
    }

}
