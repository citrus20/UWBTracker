package uwb.mnilsen.org.uwbtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mnilsen on 9/8/17.
 */

public class DashView extends SurfaceView implements SurfaceHolder.Callback,TrackingEventListener {
    public static final int MAX_TAG_INDEX = 1;
    public static final int MAX_ZONE_MILLIMETERS_X= 30000;
    public static final int MAX_ZONE_MILLIMETERS_Y= 30000;

    private Context context;
    private TrackingClient tc;
    private String uri = "";

    private SurfaceHolder holder;
    private Translator translator = new Translator();
    private Bitmap background;
    private Bitmap[] tagIcons = new Bitmap[MAX_TAG_INDEX + 1];
    private float scaleX;
    private float scaleY;
    private JSONArray current = null;

    public DashView(Context context) {
        super(context);
        this.context = context;
        this.uri = context.getString(R.string.locserver_default);
        background  = BitmapFactory.decodeResource(context.getResources(), R.drawable.times_sq);
        tagIcons[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.t0);
        tagIcons[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.t1);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        holder = surfaceHolder;
        Rect r = surfaceHolder.getSurfaceFrame();
        this.scaleX = MAX_ZONE_MILLIMETERS_X / r.width();
        this.scaleY = MAX_ZONE_MILLIMETERS_Y / r.height();
        this.drawBlankScreen();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        this.drawScreen();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if(this.tc != null) this.tc.stop();
    }

    @Override
    public void handleTrackingEvent(String msg) {
        try {
            this.current = new JSONArray(msg);
            this.drawScreen();
        } catch (JSONException e) {
            Log.e("LocTrackMain", String.format("JSON parse error: %s", msg), e);
        }
    }

    private void drawScreen()
    {
        Canvas c = this.holder.lockCanvas();
        c.drawBitmap(background,0.0f,0.0f,null);
        if(this.current == null) return;
        for(int i = 0;i < this.current.length();i++)
        {
            try {
                JSONObject jobj = this.current.getJSONObject(i);
                int x = jobj.getInt("x");
                x = this.translator.getTranslatedX(x);
                int y = jobj.getInt("y");
                y = this.translator.getTranslatedY(y);

                c.drawBitmap(this.tagIcons[i],(float)x,(float)y,null);

            } catch (JSONException e) {
                Log.e("LocTrackMain", String.format("JSON parse error: %s", e.getMessage()), e);
            }
        }
        this.holder.unlockCanvasAndPost(c);
    }

    private void drawBlankScreen()
    {
        Canvas c = this.holder.lockCanvas();
        c.drawBitmap(background,0.0f,0.0f,null);

        this.holder.unlockCanvasAndPost(c);
    }

    public void start(Translator tr)
    {
        this.translator = tr;
        tr.setxScale(this.scaleX);
        tr.setyScale(this.scaleY);
        try {
            this.tc = new TrackingClient(new URI(this.uri));
            this.tc.addTrackingEventListener(this);
            this.tc.start();
        } catch (URISyntaxException e) {
            Log.e("LocTrackMain", String.format("JSON parse error: %s", this.uri), e);
        }
    }

    public void stop() {
        if(this.tc != null) {
            this.tc.stop();
        }
        this.drawBlankScreen();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public TrackingClient getTrackingClient() {
        return tc;
    }


}
