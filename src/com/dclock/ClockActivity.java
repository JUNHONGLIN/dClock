package com.dclock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class ClockActivity extends Activity
{

    private TextView timeDisplay;
    private TextView dateDisplay;
    private final Handler handler = new Handler();
    private final BroadcastReceiver intentReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {
            handler.post(new Runnable()
            {
                public void run()
                {
                    updateTime();
                }
            });
        }
    };

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // Set our view from the "main" layout resource
        setContentView(R.layout.main);

        // Getting text views

        timeDisplay = (TextView) findViewById(R.id.CurrentTime);
        dateDisplay = (TextView) findViewById(R.id.CurrentDate);
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(intentReceiver, filter);
        updateTime();
    }

    @Override
    public void onDestroy()
    {
        unregisterReceiver(intentReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    private void updateTime()
    {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();
        timeDisplay.setText(now.format("%H:%M"));
        dateDisplay.setText(now.format("%A, %d %B %Y"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item != null)
        {
            switch (item.getItemId())
            {
                case R.id.Settings:
                    Intent intent = new Intent(ClockActivity.this, ClockSettingActivity.class);
                    startActivity(intent);
                    return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
