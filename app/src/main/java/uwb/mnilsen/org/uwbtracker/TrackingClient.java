package uwb.mnilsen.org.uwbtracker;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by michaeln on 8/30/17.
 */

public class TrackingClient extends WebSocketClient {
    private URI uri;

    public TrackingClient(URI serverURI) {
        super(serverURI);
        this.uri = uri;
    }

    public void start()
    {
        super.connect();
    }

    public void stop()
    {
        super.close();
    }

    public static void main(String[] args ) {
        URI uri;
        try {
            uri = new URI("ws://10.0.0.157:8080/ws/tracking");

        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        TrackingClient tc = new TrackingClient(uri);
        tc.start();

    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println(String.format("Connection opened: %s",handshakedata.getHttpStatusMessage()));
    }

    @Override
    public void onMessage(String message) {
        System.out.println(String.format("Message: %s",message));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println(String.format("Connection closed: %s",reason));
    }

    @Override
    public void onError(Exception ex) {
        System.out.println(String.format("Websocket error: %s",ex.getMessage()));
    }
}
