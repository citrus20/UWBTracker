package uwb.mnilsen.org.uwbtracker;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michaeln on 8/30/17.
 */

public class TrackingClient extends WebSocketClient {
    private URI uri;
    private List<TrackingEventListener> listeners = new ArrayList<>();

    public TrackingClient(URI serverURI) {
        super(serverURI, new Draft_17());
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

    public void addTrackingEventListener(TrackingEventListener l)
    {
        this.listeners.add(l);
    }

    public void removeTrackingEventListener(TrackingEventListener l)
    {
        this.listeners.remove(l);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log(String.format("Connection opened: %s",handshakedata.getHttpStatusMessage()));
    }

    @Override
    public void onMessage(String message) {
        log(String.format("Message: %s",message));
        for(TrackingEventListener l:this.listeners)
        {
            l.handleTrackingEvent(message);
        }
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log(String.format("Connection closed: %s",reason));
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        log(String.format("Websocket error: %s",ex.getMessage(),ex));
    }

    private void log(String msg)
    {
        //System.out.println(msg);
        Log.i("TrackingClient",msg);
    }

    private void log(String msg,Throwable e)
    {
        //System.out.println(msg);
        Log.w("TrackingClient",msg,e);
    }
}
