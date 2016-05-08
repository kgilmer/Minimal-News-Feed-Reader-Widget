package com.abk.mrw.notificationui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;

/**
 * Created by kgilmer on 1/8/16.
 */
public class SettingsActivity  extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }


    @Override
    protected void onResume() {
        super.onResume();

        NewsFeedService.scheduleAlarm(this);

        fireWOTDIntent(this);
    }

    public static void fireWOTDIntent(Context activity) {
        final Intent intent = new Intent(activity, NewsFeedService.class);
        activity.startService(intent);
    }
}