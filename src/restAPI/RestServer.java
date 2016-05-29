package restAPI;

import com.sun.net.httpserver.HttpServer;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;

/**
 * Created by Wilhelm Werner und Marcel Öhlenschläger
 */
public class RestServer {

    private final static int port = 8090;
    private final static String host = "http://localhost/";

    public static void main(String[] args) throws Exception {
        URI baseURI = UriBuilder.fromUri(host).port(port).build();
        ResourceConfig config = new ResourceConfig(PrinterQQ.class);
        HttpServer server = JdkHttpServerFactory.createHttpServer(baseURI, config);
    }
}
