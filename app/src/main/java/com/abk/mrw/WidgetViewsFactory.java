package com.abk.mrw;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;
import com.abk.mrw.db.DataSource;
import com.abk.mrw.model.FeedEntry;
import com.abk.mrw.util.PrefsUtil;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import trikita.log.Log;

import java.util.*;

/**
 * Responsible for loading the widget content based on preference data
 * associated with the widget.
 */
public class WidgetViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    //URLs for the widget.
    private final Set<String> urls;
    //Widget Model.
    private final List<FeedEntry> feed = new ArrayList<>();
    private final Context context;
    private final SharedPreferences prefs;

    public WidgetViewsFactory(Context context, Intent intent) {
        int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);

        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            throw new IllegalArgumentException("Invalid widget id.");
        }

        this.context = context;
        this.prefs = context.getSharedPreferences(PrefsUtil.getSharedPrefsRoot(appWidgetId), 0);
        this.urls = getURLsFromPrefs(prefs);
    }

    /**
     * Load all URLs for widget from prefs.
     *
     * @param prefs SharedPreferences
     * @return set of URLs as strings
     */
    private Set<String> getURLsFromPrefs(SharedPreferences prefs) {
        return prefs.getStringSet("pref_feeds", Collections.<String>emptySet());
    }

    @Override
    public void onCreate() {
        scheduleAlarm(context);
    }

    @Override
    public void onDestroy() {
        clearAlarm(context);
    }

    @Override
    public int getCount() {
        return feed.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final String textSize = prefs.getString("pref_textSize", "Medium");
        final int rowLayout;
        switch (textSize) {
            case "Large":
                rowLayout = R.layout.row_large;
                break;
            case "Medium":
                rowLayout = R.layout.row_medium;
                break;
            case "Small":
                rowLayout = R.layout.row_small;
                break;
            default:
                throw new IllegalArgumentException("Undefined size: " + textSize);
        }

        RemoteViews widgetRow = new RemoteViews(context.getPackageName(),
                rowLayout);

        widgetRow.setTextViewText(android.R.id.title, feed.get(position).getTitle());
        final String urlStr = feed.get(position).getUrl();
        if (urlStr != null) {
            //URL will be null if a 'dummy' list view item with the error is displayed.
            final Intent intent = new Intent();
            intent.setData(Uri.parse(urlStr));
            widgetRow.setOnClickFillInIntent(android.R.id.title, intent);
        }

        return widgetRow;
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
        synchronized (feed) {
            feed.clear();
            Iterables.addAll(feed, DataSource.getRSSItems(urls));
        }
    }

    protected static void scheduleAlarm(final Context context) {
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

        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_HOUR, alarmIntent);

        Log.d("Setting alarm for refresh: " + calendar.toString());
    }

    /**
     * Create any existing alarm.
     * @param context Context
     */
    private static void clearAlarm(Context context) {
        final PendingIntent alarmIntent =
                getAlarmIntent(context);

        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmMgr.cancel(alarmIntent);
    }

    /**
     * Create alarm intent
     * @param context Context
     * @return alarm intent
     */
    private static PendingIntent getAlarmIntent(Context context) {
        return PendingIntent.getService(context, 0, createRefreshIntent(context), 0);
    }

    /**
     * @param context Context
     * @return Intent to refresh widget
     */
    public static Intent createRefreshIntent(Context context) {
        return new Intent(context, NewsFeedLoadService.class);
    }
}