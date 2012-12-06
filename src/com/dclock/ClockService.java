package com.dclock;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

public class ClockService extends Service
{
    private Time prevTime;

    private Runnable callback = new Runnable()
    {
        @Override
        public void run() {
            updateTime();
            runTimer();
        }
    };

    private Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
   {
        String action = null;
        if (intent != null)
        {
            action = intent.getAction();
        }

        if (action != null && !action.isEmpty())
        {
            if (intent.getAction().equalsIgnoreCase(ClockWidget.ClockStart))
            {
                prevTime = new Time(Time.getCurrentTimezone());
                prevTime.setToNow();
                runTimer();
            }
            else
            {
                if (intent.getAction().equalsIgnoreCase(ClockWidget.ClockStop))
                {
                    cleanup();
                    stopSelf();
                }
            }
        }

       return START_STICKY;
   }

    public void cleanup()
    {
        handler.removeCallbacks(callback);
    }

    public void updateTime()
    {
        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();

        if (now.year != prevTime.year ||
            now.month != prevTime.month ||
            now.monthDay != prevTime.monthDay ||
            now.hour != prevTime.hour ||
            now.minute != prevTime.minute)
        {
            Intent intent = new Intent(this, ClockWidget.class);
            intent.setAction(ClockWidget.ACTION_REDRAW);
            sendBroadcast(intent);
        }

        prevTime = now;
    }

    private void runTimer()
    {
        handler.postDelayed(callback, 1000);
    }
}