package actions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Action {

    private String type;
    private String body;
    private Gson gson;
    
    public Action() {
    	
    }
    
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

    public String getType(){
    	return type;
    }
    

    public String getBody(){
    	return body;
    }
}
