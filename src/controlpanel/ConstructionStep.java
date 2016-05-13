package controlpanel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by trafy on 06.05.16.
 */
public class ConstructionStep {

    private boolean draw;
    private int x;
    private int y;
    private int z;

    public ConstructionStep() {

    }
    
    public String toString(){
        Gson gson = new GsonBuilder().create();
    	return gson.toJson(this);
    }

}
