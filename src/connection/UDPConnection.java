package connection;

/**
 * Created by Marcel Oehlenschlaeger und Wilhelm Werner
 */
import java.io.IOException;
import java.net.*;

public class UDPConnection implements IConnection {

    private String host;
    private int port;

    private DatagramSocket socket;
    private byte dataSend[] = new byte[1024];
    private byte dataReceive[] = new byte[1024];
    private InetAddress address;
    private DatagramPacket packetSend;
    private DatagramPacket packetReceive;

    public UDPConnection(String host, int port) throws Exception{
        this.host = host;
        this.port = port;
    }

    public void connect() throws IOException {
        socket = new DatagramSocket();
    }

    public void close() throws IOException {
        socket.close();
    }

    public void tellServerToClose() throws IOException {
        return;
    }

    public void sendMessage(String msg) throws IOException {
        address = InetAddress.getByName(host);
        dataSend = (msg).getBytes();
        packetSend = new DatagramPacket(dataSend, dataSend.length, address, port);
        socket.send(packetSend);
    }

    public String receiveMessage() throws IOException {
        packetReceive = new DatagramPacket(dataReceive, dataReceive.length);
        socket.receive(packetReceive);
        return new String(packetReceive.getData());
    }
}
