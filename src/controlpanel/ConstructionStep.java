package controlpanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by Wilhelm Werner und Marcel Öhlenschläger
 */
public class ConstructionStep {

    private boolean draw;
    private int x;
    private int y;
    private int z;
    private String color;

    public ConstructionStep() {

    }
    
    public String toString(){
        Gson gson = new GsonBuilder().create();
    	return gson.toJson(this);
    }

}
