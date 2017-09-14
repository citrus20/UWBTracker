package uwb.mnilsen.org.uwbtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by mnilsen on 9/8/17.
 */

public class DashView extends SurfaceView implements SurfaceHolder.Callback,TrackingEventListener {
    public static final int MAX_TAG_INDEX = 1;
    public static final float MAX_ZONE_MILLIMETERS_X = 6000.0f;
    public static final float MAX_ZONE_MILLIMETERS_Y= 6000.0f;

    private Context context;
    private TrackingClient tc;
    private String uri = "";

    private Translator translator = new Translator();
    private Bitmap background;
    private Bitmap[] tagIcons = new Bitmap[MAX_TAG_INDEX + 1];
    private float scaleX;
    private float scaleY;
    private float height;
    private float width;
    private JSONArray current = null;
    private Paint paint = new Paint();
    private AnchorConfig anchorConfig;

    private Paint circumPaint = new Paint();
    private Paint anchorPaint = new Paint();

    public DashView(Context context) {
        super(context);
        this.context = context;
        this.setup();
    }

    public DashView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.setup();
    }
//
//    public DashView(Context context, AttributeSet attrs, int defStyleAttr, Context context1) {
//        super(context, attrs, defStyleAttr);
//        this.context = context1;
//        this.setup();
//    }

    private void setup() {
        this.getHolder().addCallback(this);
        this.setZOrderOnTop(true);
        this.getHolder().setFormat(PixelFormat.TRANSPARENT);
        this.setFocusable(true);
        this.uri = context.getString(R.string.locserver_default);
        background  = BitmapFactory.decodeResource(context.getResources(), R.drawable.times_sq);
        tagIcons[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.t0);
        tagIcons[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.t1);

        this.circumPaint.setColor(Color.GREEN);
        this.circumPaint.setStrokeWidth(1.0f);
        this.circumPaint.setStyle(Paint.Style.STROKE);

        this.anchorPaint.setColor(Color.GREEN);

    }

    private void calculateScale()
    {
        Rect r = this.getHolder().getSurfaceFrame();
        this.width = r.width();
        this.height = r.height();
        this.scaleX = r.width() / MAX_ZONE_MILLIMETERS_X;
        this.scaleY = r.height() / MAX_ZONE_MILLIMETERS_Y;
        Log.i("LocTrackMain",String.format("scaleX=%s, scaleY=%s, width=%s, height=%s",this.scaleX,this.scaleY,this.width,this.height));
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        this.calculateScale();
        this.translator.setScale(this.scaleX);
        Log.i("LocTrackMain",this.translator.toString());
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
        Canvas c = this.getHolder().lockCanvas();
        if(c == null) return;
        c.save();
        c.drawBitmap(background,0.0f,0.0f,paint);
        this.drawAnchors(c);
        if(this.current == null) return;
        for(int i = 0;i < this.current.length();i++)
        {
            StringBuilder sb = new StringBuilder();
            sb.append("Tag " + i + ", ");
            try {
                JSONObject jobj = this.current.getJSONObject(i);
                float x = jobj.getInt("x");
                sb.append("pre-X " + x + ", ");
                x = this.translator.getTranslatedTagX(x);
                sb.append("post-X " + x + ", ");
                float y = jobj.getInt("y");
                sb.append("pre-Y " + y + ", ");
                y = this.translator.getTranslatedTagY(y);
                sb.append("post-Y " + y );
                Log.i("LocTrackMain",sb.toString());

                c.drawBitmap(this.tagIcons[i],x,this.height - y,paint);

            } catch (JSONException e) {
                Log.e("LocTrackMain", String.format("JSON parse error: %s", e.getMessage()), e);
            }
        }
        c.restore();
        this.getHolder().unlockCanvasAndPost(c);
    }

    private void drawAnchors(Canvas c)
    {
        if(this.anchorConfig == null) return;

        //  circumcircle
        c.drawCircle(this.anchorConfig.getCircleCenter()[0],
                this.anchorConfig.getCircleCenter()[1],
                this.anchorConfig.getCircleRadius(),this.circumPaint);

        //  anchor 0
        c.drawCircle(this.anchorConfig.getAnchor0Position()[0],
                this.anchorConfig.getAnchor0Position()[1],15.0f,this.anchorPaint);
        //  anchor 1
        c.drawCircle(this.anchorConfig.getAnchor1Position()[0],
                this.anchorConfig.getAnchor1Position()[1],15.0f,this.anchorPaint);
        //  anchor 2
        c.drawCircle(this.anchorConfig.getAnchor2Position()[0],
                this.anchorConfig.getAnchor2Position()[1],15.0f,this.anchorPaint);
    }

    private void drawBlankScreen()
    {
        Canvas c = this.getHolder().lockCanvas();
        if(c == null) return;
        c.drawBitmap(background,0.0f,0.0f,paint);
        this.getHolder().unlockCanvasAndPost(c);
    }

    public void start(Translator tr) {
        this.translator = tr;

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
        //this.drawBlankScreen();
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

    class AnchorTask extends AsyncTask<String,Integer,AnchorConfig> {

        @Override
        protected AnchorConfig doInBackground(String... params) {
            String json = null;
            InputStream is = null;
            Log.i("LocTrackMain","Retrieving Anchor config...");
            AnchorConfig ac = new AnchorConfig();
            HttpClient cl = new DefaultHttpClient();
            try {
                HttpResponse resp = cl.execute(new HttpGet(params[0]));
                is = resp.getEntity().getContent();
                json = this.streamToString(is);
            } catch (IOException e) {
                Log.e("LocTrackMain", String.format("I/O error while retrieving Anchor info from '%s'", params[0]), e);
            }
            if(json == null || json.length() == 0)
            {
                Log.e("LocTrackMain", String.format("No json found while retrieving Anchor info from '%s'", params[0]));
                return null;
            }

            try {
                JSONObject jobj = new JSONObject(json);
                ac.setA0a1Distance(Math.round(jobj.getDouble("a0a1Distance")) * translator.getScale());
                ac.setA0a2Distance(Math.round(jobj.getDouble("a0a2Distance")) * translator.getScale());
                ac.setA1a2Distance(Math.round(jobj.getDouble("a1a2Distance")) * translator.getScale());

                float[] floats = (float[]) jobj.get("anchor0Position");
                float fx = translator.getTranslatedX(floats[0]);
                float fy = translator.getTranslatedY(floats[1]);
                ac.setAnchor0Position(new float[]{fx,fy});

                floats = (float[]) jobj.get("anchor1Position");
                fx = translator.getTranslatedX(floats[0]);
                fy = translator.getTranslatedY(floats[1]);
                ac.setAnchor1Position(new float[]{fx,fy});

                floats = (float[]) jobj.get("anchor2Position");
                fx = translator.getTranslatedX(floats[0]);
                fy = translator.getTranslatedY(floats[1]);
                ac.setAnchor2Position(new float[]{fx,fy});

                float rad = Math.round(jobj.getDouble("circleRadius"));
                ac.setCircleRadius(rad * translator.getScale());

                floats = (float[]) jobj.get("circleCenter");
                fx = translator.getTranslatedX(floats[0]);
                fy = translator.getTranslatedY(floats[1]);
                ac.setCircleCenter(new float[]{fx,fy});

                Log.i("LocTrackMain","Retrieved Anchor config: " + ac.toString());

            } catch (JSONException e) {
                Log.e("LocTrackMain", String.format("Anchor JSON parse error: %s", json), e);
            }

            return ac;
        }

        private String streamToString(InputStream is) throws IOException {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            String line = "";
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
