package uwb.mnilsen.org.uwbtracker;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity {

    private TextView mTextMessage;
    private TextView urlText;
    private TextView jsonText;
    private GridLayout settingsGrid;
    private GridLayout debugGrid;
    private DashView dashPanel;
    private ToggleButton runButton;
    private Button zeroButton;
    private TextView x_offsetText;
    private TextView y_offsetText;
    private TextView x_scaleText;
    private TextView y_scaleText;

    private TrackingClient tracking;
    private Translator trans = new Translator();
    private boolean zeroing = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    settingsGrid.setVisibility(View.VISIBLE);
                    dashPanel.setVisibility(View.GONE);
                    debugGrid.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_dashboard:
                    mTextMessage.setText(R.string.title_dashboard);
                    settingsGrid.setVisibility(View.GONE);
                    dashPanel.setVisibility(View.VISIBLE);
                    debugGrid.setVisibility(View.GONE);
                    return true;
                case R.id.navigation_notifications:
                    mTextMessage.setText(R.string.title_notifications);
                    settingsGrid.setVisibility(View.GONE);
                    dashPanel.setVisibility(View.GONE);
                    debugGrid.setVisibility(View.VISIBLE);
                    return true;
            }
            return false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTextMessage = (TextView) findViewById(R.id.message);
        this.urlText = (TextView) findViewById(R.id.urlText);
        this.jsonText = (TextView) findViewById(R.id.jsonText);
        this.settingsGrid = (GridLayout) findViewById(R.id.setings_pnl);
        this.debugGrid = (GridLayout) findViewById(R.id.debug_pnl);
        this.dashPanel = (DashView) findViewById(R.id.img_pnl);
        this.runButton = (ToggleButton) findViewById(R.id.runButton);
        this.zeroButton = (Button) findViewById(R.id.zeroButton);
        this.x_offsetText = (TextView) findViewById(R.id.x_offset);
        this.y_offsetText = (TextView) findViewById(R.id.y_offset);
        this.x_scaleText = (TextView) findViewById(R.id.x_scale);
        this.y_scaleText = (TextView) findViewById(R.id.y_scale);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.zeroButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startZeroingProcess();
            }
        });
        this.runButton.setOnCheckedChangeListener(new ToggleListener());
        loadTranslationText();
    }

    private void loadTranslationText() {
        this.x_offsetText.setText(Integer.toString(this.trans.getxOffset()));
        this.y_offsetText.setText(Integer.toString(this.trans.getyOffset()));
        this.x_scaleText.setText(Float.toString(this.trans.getxScale()));
        this.y_scaleText.setText(Float.toString(this.trans.getyScale()));
    }

    private void retrieveTranslationText() {
        this.trans.setxOffset(Integer.parseInt(this.x_offsetText.getText().toString()));
        this.trans.setyOffset(Integer.parseInt(this.y_offsetText.getText().toString()));
        this.trans.setxScale(Float.parseFloat(this.x_scaleText.getText().toString()));
        this.trans.setyScale(Float.parseFloat(this.y_scaleText.getText().toString()));
    }

    private void startZeroingProcess() {
        this.zeroing = true;
        AsyncTask task = new ZeroTask();
        task.execute(new Integer[0]);
    }

    private class ToggleListener implements CompoundButton.OnCheckedChangeListener {

        public ToggleListener() {

        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                String url = urlText.getText().toString();
                try {
                    dashPanel.setUri(urlText.getText().toString());
                    retrieveTranslationText();
                    dashPanel.start(trans);
                } catch (Exception e) {
                    Log.w("LocTrackMain", String.format("Startup: %s", e.getMessage()), e);
                }
            } else {
                dashPanel.stop();
            }
        }
    }

    class EventUpdateTask  extends AsyncTask<String, String, Integer> {
        String message;

        @Override
        protected Integer doInBackground(String... strings) {
            this.message = strings[0];
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            jsonText.setText(this.message);
        }
    }

    class ZeroTask extends AsyncTask<Integer, Integer, Integer> {
        public static final int SAMPLE_COUNT = 50;
        private String uri;

        @Override
        protected void onPreExecute() {
            this.uri = urlText.getText().toString();
        }

        @Override
        protected Integer doInBackground(Integer... integers) {
            ZeroTrackingEventListener lstnr = new ZeroTrackingEventListener();
            try {
                TrackingClient tc = new TrackingClient(new URI(uri));
                tc.addTrackingEventListener(lstnr);
                tc.start();
                while(true)
                {
                    if(lstnr.isComplete()) break;
                }
                tc.stop();
                zeroing = false;
                Toast.makeText(getApplicationContext(), "Zero calibration completed", Toast.LENGTH_LONG);
            }
            catch (URISyntaxException e) {
                Log.e("LocTrackMain", String.format("Invalid URI: %s", uri), e);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            x_offsetText.setText(Integer.toString(trans.getxOffset()));
            y_offsetText.setText(Integer.toString(trans.getyOffset()));
        }

        private class ZeroTrackingEventListener implements TrackingEventListener {
            int xSum = 0;
            int ySum = 0;
            int sampleCount = 0;
            boolean complete = false;

            public void handleTrackingEvent(String msg) {
                JSONArray jarr;
                try {
                    jarr = new JSONArray(msg);
                } catch (JSONException e) {
                    Log.e("LocTrackMain", String.format("JSON parse error: %s", msg), e);
                    return;
                }
                try {
                    JSONObject obj = jarr.getJSONObject(0);
                    int x = obj.getInt("x");
                    int y = obj.getInt("y");
                    this.xSum += x;
                    this.ySum += y;
                    this.sampleCount++;
                    if (this.sampleCount == SAMPLE_COUNT) {
                        complete = true;
                        x = -this.xSum / this.sampleCount;
                        y = -this.ySum / this.sampleCount;
                        trans.setxOffset(x);
                        trans.setyOffset(y);
                    }
                } catch (JSONException e) {
                    Log.e("LocTrackMain", String.format("JSON parse error: %s", msg), e);
                    return;
                }
            }

            public boolean isComplete() {
                return complete;
            }
        }
    }
}
