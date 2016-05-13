package controlpanel;

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
    	return (( draw ? "Zeichne bis " : "Fahre bis ") + x + "" + y + "" + z);
    }

}
