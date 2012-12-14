package com.dclock;


import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.TextView;

public class ClockSettingActivity extends PreferenceActivity
{
    public void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
