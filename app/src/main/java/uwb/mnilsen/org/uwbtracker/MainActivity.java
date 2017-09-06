package uwb.mnilsen.org.uwbtracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.net.URI;
import java.net.URISyntaxException;

public class MainActivity extends AppCompatActivity implements TrackingEventListener {

    private TextView mTextMessage;
    private TextView urlText;
    private TextView jsonText;
    private GridLayout settingsGrid;
    private GridLayout debugGrid;
    private ImageView dashPanel;
    private ToggleButton runButton;

    private TrackingClient tracking;

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
        this.dashPanel = (ImageView) findViewById(R.id.img_pnl);
        this.runButton = (ToggleButton) findViewById(R.id.runButton);

        runButton.setOnCheckedChangeListener(new ToggleListener(this));

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    private class ToggleListener implements CompoundButton.OnCheckedChangeListener {
        TrackingEventListener listener;
        public ToggleListener(TrackingEventListener listener) {
            this.listener = listener;
        }

        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                String url = urlText.getText().toString();
                try {
                    tracking = new TrackingClient(new URI(url));
                    tracking.addTrackingEventListener(this.listener);
                    tracking.start();
                } catch (URISyntaxException e) {
                    Log.w("LocTrackMain",String.format("Invalid URI: %s",url),e);
                }
            } else {
                if(tracking != null) tracking.stop();
                tracking = null;
            }
        }
    }

    @Override
    public void handleTrackingEvent(String msg) {
        this.jsonText.setText(msg);
    }
}
