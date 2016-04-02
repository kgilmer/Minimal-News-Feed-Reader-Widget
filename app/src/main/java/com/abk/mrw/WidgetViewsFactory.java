/***
 * Copyright (c) 2008-2012 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 * <p/>
 * From _The Busy Coder's Guide to Advanced Android Development_
 * http://commonsware.com/AdvAndroid
 */


package com.abk.mrw;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.abk.mrw.db.DataSource;
import com.abk.mrw.model.RSSItem;
import com.abk.mrw.settings.SettingsActivity;
import com.abk.mrw.util.PrefsUtil;
import com.google.common.collect.Iterables;

import java.util.*;

public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private final Set<String> urls;
    private final List<RSSItem> feed = new ArrayList<>();
    private final Context ctxt;
    private final int appWidgetId;
    private final SharedPreferences prefs;

    public WidgetViewsFactory(Context ctxt, Intent intent) {
        this.ctxt = ctxt;
        //this.urls = intent.getStringArrayExtra(RSSLoadService.EXTRA_KEY_URL_ARRAY);
        this.appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
        this.prefs = ctxt.getSharedPreferences(PrefsUtil.getSharedPrefsRoot(appWidgetId), 0);
        this.urls = prefs.getStringSet("pref_popularSources", Collections.singleton("http://news.ycombinator.com/rss"));
    }

    @Override
    public void onCreate() {
        scheduleAlarm(ctxt);
        Log.d(this.getClass().getCanonicalName(), "onCreate()");
    }

    @Override
    public void onDestroy() {
        clearAlarm(ctxt);
        Log.d(this.getClass().getCanonicalName(), "onDestroy()");
    }

    @Override
    public int getCount() {
        Log.i(WidgetViewsFactory.class.getCanonicalName(), "getCount() " + feed.size());

        return feed.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews row = new RemoteViews(ctxt.getPackageName(),
                R.layout.row);

        final Intent i = new Intent();
        row.setTextViewText(android.R.id.title, feed.get(position).getTitle());
        i.setData(Uri.parse(feed.get(position).getUrl()));
        row.setOnClickFillInIntent(android.R.id.title, i);

        final int textColor = prefs.getInt("textcolor", -1);
        if (textColor != -1) {
            row.setTextColor(android.R.id.title, textColor);
        }
        //Log.i(WidgetViewsFactory.class.getCanonicalName(), "getViewAt()");

        return row;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
        Log.d(this.getClass().getCanonicalName(), "onDataSetChanged()");

        List<RSSItem> tmpItems = new ArrayList<>();
        Iterables.addAll(tmpItems, DataSource.getRSSItems(urls));
        Log.d(this.getClass().getCanonicalName(), "new items: " + tmpItems.size());
        feed.clear();
        feed.addAll(tmpItems);
    }

    protected static void scheduleAlarm(final Context context) {
        Log.d(WidgetProvider.class.getCanonicalName(), "Scheduling alarm.");

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);

        final PendingIntent alarmIntent =
                getAlarmIntent(context);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.cancel(alarmIntent);
        alarmMgr.setInexactRepeating(AlarmManager.ELAPSED_REALTIME,
                AlarmManager.INTERVAL_HALF_HOUR,
                AlarmManager.INTERVAL_HALF_HOUR, alarmIntent);

        /*
        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, alarmIntent);
                */
        Log.i(WidgetViewsFactory.class.getCanonicalName(), "Setting alarm for refresh: " + calendar.toString());
    }

    private void clearAlarm(Context context) {
        final PendingIntent alarmIntent =
                getAlarmIntent(context);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
    }


    private static PendingIntent getAlarmIntent(Context context) {
        return PendingIntent.getService(context, 0, createRefreshIntent(context), 0);
    }

    public static Intent createRefreshIntent(Context context) {
        return new Intent(context, RSSLoadService.class);
    }
}