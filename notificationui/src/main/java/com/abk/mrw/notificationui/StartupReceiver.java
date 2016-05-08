package com.abk.mrw.notificationui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by kgilmer on 1/16/16.
 */
public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NewsFeedService.scheduleAlarm(context);
    }
}
