package com.dclock;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClockService extends Service
{
    private final String ServiceTag = "dClockService";
    private Time prevTime;
    private AtomicBoolean started = new AtomicBoolean(false);

    private Runnable callback = new Runnable()
    {
        @Override
        public void run()
        {
            updateTime();
            runTimer();
        }
    };

    private Handler handler = new Handler();

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
   {
        Log.d(ServiceTag, "onStartCommand start");
        String action = null;
        if (intent != null)
        {
            action = intent.getAction();
        }

        if (action != null && !action.isEmpty())
        {
            Log.d(ServiceTag, String.format("Intent action is \'%1s\'", action));
            if (action.equalsIgnoreCase(ClockWidget.ClockStart) && !started.getAndSet(true))
            {
                Log.d(ServiceTag, "runTimer");
                prevTime = new Time(Time.getCurrentTimezone());
                prevTime.setToNow();
                runTimer();
            }
            else
            {
                if (intent.getAction().equalsIgnoreCase(ClockWidget.ClockStop))
                {
                    Log.d(ServiceTag, "stopSelf");
                    started.set(false);
                    stopSelf();
                }
            }
        }

       Log.d(ServiceTag, "onStartCommand end");
       return START_STICKY;
   }

   @Override
   public void onDestroy()
   {
        Log.d(ServiceTag, "onDestroy start");
        started.set(false);
        cleanup();
        super.onDestroy();
        Log.d(ServiceTag, "onDestroy end");
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
        if (started.get())
        {
            try
            {
                handler.postDelayed(callback, 1000);
            }
            catch (Exception ex)
            {
                Log.e(ServiceTag, "Run timer error", ex);
                handler.postDelayed(callback, 1000);
            }
        }
    }
}