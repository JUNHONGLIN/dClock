package com.dclock;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.*;
import android.text.format.Time;
import android.util.Log;
import android.widget.RemoteViews;

public class ClockWidget extends AppWidgetProvider
{
    public static final String ClockStart = "DigitalClock.ClockStart";
    public static final String ClockStop = "DigitalClock.ClockStop";
    public static final String ACTION_REDRAW = "com.dClock.action.CLOCK_REDRAW";

    @Override
    public void onReceive(Context context, Intent intent)
    {
        if (intent != null &&
            intent.getAction() != null &&
            intent.getAction().equalsIgnoreCase(ACTION_REDRAW))
        {
            updateTimeImage(context);
        }

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
    {
        updateTimeImage(context);

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onEnabled(Context context)
    {
        if (context != null)
        {
            super.onEnabled(context);
            Intent i = new Intent(context, ClockService.class);
            i.setAction(ClockStart);
            context.startService(i);
        }

        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context)
    {
        if (context != null)
        {
            Intent i = new Intent(context, ClockService.class);
            i.setAction(ClockStop);
            context.startService(i);
        }

        super.onDisabled(context);
    }

    public static void updateTimeImage(Context context)
    {
        if (context == null)
        {
            return;
        }

        Time now = new Time(Time.getCurrentTimezone());
        now.setToNow();

        ComponentName thisWidget = new ComponentName(context, ClockWidget.class);
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setImageViewBitmap(R.id.CurrentTimeImage, BuildTime(context, now.format("%A, %d %B %Y"), now.format("%H:%M")));
        manager.updateAppWidget(thisWidget, views);
    }

    private static Bitmap BuildTime(Context context, String date, String time)
    {
        Bitmap result = Bitmap.createBitmap(400, 120, Bitmap.Config.ARGB_4444);
        Canvas myCanvas = new Canvas(result);
        Paint paint = new Paint();
        Typeface clock = Typeface.createFromAsset(context.getAssets(), "fonts/AndroidClock.ttf");
        paint.setColor(Color.TRANSPARENT);
        myCanvas.drawPaint(paint);
        paint.setAntiAlias(true);
        paint.setSubpixelText(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
        paint.setTextSize(14);
        paint.setTextAlign(Paint.Align.RIGHT);
        myCanvas.drawText(date, 400, 116, paint);

        paint.setTypeface(clock);
        paint.setTextSize(100);
        myCanvas.drawText(time, 400, 100, paint);

        return result;
    }
}
