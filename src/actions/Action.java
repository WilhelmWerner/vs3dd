package actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Action {

    public String type;
    public String body;
    private Gson gson;
    
    // for the lazy boy
    public Action(String typeAndBody){
    	this.type = typeAndBody;
    	this.body = typeAndBody;
    }
    
    public Action(String type, String body){
    	this.type = type;
    	this.body = body;
    }

    public String toString(){
    	gson = new GsonBuilder().create();
    	
		return gson.toJson(this);
    }
}
