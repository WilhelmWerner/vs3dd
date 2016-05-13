package printer;

public class PrinterStep {

	String STEP = "";
	int X = 0;
	int Y = 0;
	int Z = 0;
	
	public String toString(){
		return (STEP + " - " + X + " " + Y + " " + Z);
	}
}
