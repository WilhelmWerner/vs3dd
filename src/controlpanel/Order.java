package controlpanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by trafy on 06.05.16.
 */
public class Order {

    private String orderId;
    private ConstructionStep[] constructionSteps;

    private int currentStepIndex = 0;

    public Order() {

    }

    public boolean hasNextStep() {
        if(this.currentStepIndex < constructionSteps.length) {
            return true;
        }
        return false;
    }

    /**
     *
     * @return constructionStep as json
     */
    public String getNextStep() {
        return this.constructionSteps[currentStepIndex].toString();
    }

}
