package com.dclock;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.atomic.AtomicBoolean;

public class ClockService extends IntentService implements SharedPreferences.OnSharedPreferenceChangeListener
{
    private static final String ServiceTag = "dClockService";
    private Calendar prevTime;
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                preferences.registerOnSharedPreferenceChangeListener(this);

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

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        preferences.unregisterOnSharedPreferenceChangeListener(this);
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
        Calendar now = Calendar.getInstance();

        if (now.get(Calendar.YEAR) != prevTime.get(Calendar.YEAR) ||
            now.get(Calendar.MONTH) != prevTime.get(Calendar.MONTH) ||
            now.get(Calendar.DAY_OF_MONTH) != prevTime.get(Calendar.DAY_OF_MONTH) ||
            now.get(Calendar.HOUR_OF_DAY) != prevTime.get(Calendar.HOUR_OF_DAY) ||
            now.get(Calendar.MINUTE) != prevTime.get(Calendar.MINUTE))
        {
            SendUpdateIntent();
        }

        prevTime = now;
    }

    private void SendUpdateIntent()
    {
        Intent intent = new Intent(this.getApplicationContext(), ClockWidget.class);
        intent.setAction(ClockWidget.ACTION_REDRAW);
        sendBroadcast(intent);
    }

    private void runTimer()
    {
        clockThread = new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                prevTime = Calendar.getInstance();
                prevTime.set(1900, 1, 1, 0, 0);
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

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s)
    {
        Log.d(ServiceTag, "SendUpdateIntent");
        SendUpdateIntent();
    }
}