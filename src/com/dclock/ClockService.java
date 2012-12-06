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
import com.dclock.utils.LocalBinder;

public class ClockService extends Service
{
    private Time prevTime;
    private IBinder binder = new LocalBinder<ClockService>(this);
    private boolean started = false;

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
        return binder;
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
            if (intent.getAction().equalsIgnoreCase(ClockWidget.ClockStart) && !started)
            {
                started = true;
                prevTime = new Time(Time.getCurrentTimezone());
                prevTime.setToNow();
                runTimer();
            }
            else
            {
                if (intent.getAction().equalsIgnoreCase(ClockWidget.ClockStop))
                {
                    started = false;
                    stopSelf();
                }
            }
        }

       return START_STICKY;
   }

   @Override
   public void onDestroy()
   {
        started = false;
        cleanup();
        super.onDestroy();
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
        if (started)
        {
            handler.postDelayed(callback, 1000);
        }
    }
}