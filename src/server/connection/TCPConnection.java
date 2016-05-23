package server.connection;

import java.io.IOException;

public class TCPConnection implements IConnection{
	
	public TCPConnection(int port){
		
	}

	@Override
	public void connect() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void close() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tellServerToClose() throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendMessage(String msg, int recipient) throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String receiveMessage() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int lastMessageFrom() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void waitForAllComponents() throws IOException {
		// TODO Auto-generated method stub
		
	}

}
