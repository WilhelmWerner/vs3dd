package printer;

public class MaterialContainer extends Thread {

	public MaterialContainer(){
		
	}
	
	public void run(){
		//System.out.println("Material Container laeuft endlos");
		while(true){
			try{
				Thread.sleep(5000);
			} catch (Exception e){
				
			}
		}
	}
}
