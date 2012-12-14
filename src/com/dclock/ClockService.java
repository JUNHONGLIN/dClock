package com.dclock;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.text.format.Time;
import android.util.Log;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class ClockService extends IntentService
{
    private static final String ServiceTag = "dClockService";
    private Time prevTime;
    private Thread clockThread;
    private AtomicBoolean started = new AtomicBoolean(false);

    public ClockService(String name) {
        super(name);
    }

    public ClockService() {
        super(ServiceTag);
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        Log.d(ServiceTag, "onHandleIntent start");
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
                runTimer();
            }
            else
            {
                if (intent.getAction().equalsIgnoreCase(ClockWidget.ClockStop))
                {
                    Log.d(ServiceTag, "stopSelf");
                    cleanup();
                    stopSelf();
                }
            }
        }

        Log.d(ServiceTag, "onHandleIntent end");
    }

   @Override
   public void onDestroy()
   {
        Log.d(ServiceTag, "onDestroy start");
        cleanup();
        super.onDestroy();
        Log.d(ServiceTag, "onDestroy end");
   }

   public void cleanup()
   {
       started.set(false);
       try
       {
           if (clockThread != null)
           {
               clockThread.join();
               clockThread = null;
           }
       }
       catch (InterruptedException e)
       {
           Log.e(ServiceTag, "Waiting thread interrupted", e);
       }
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
            Intent intent = new Intent(this.getApplicationContext(), ClockWidget.class);
            intent.setAction(ClockWidget.ACTION_REDRAW);
            sendBroadcast(intent);
        }

        prevTime = now;
    }

    private void runTimer()
    {
        clockThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                prevTime = new Time(Time.getCurrentTimezone());
                prevTime.setToNow();
                while(started.get())
                {
                    updateTime();
                    try
                    {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException e)
                    {
                        Log.e(ServiceTag, "Interrupted", e);
                    }
                }
            }
        });

        clockThread.run();
    }
}