package com.abk.mrw;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.widget.RemoteViews;
import com.abk.mrw.db.DataSource;
import com.abk.mrw.util.PrefsUtil;
import trikita.log.Log;

import java.util.Arrays;

/**
 * Created by kgilmer on 2/14/16.
 */
public class NewsFeedLoadService extends IntentService {
    public static final String EXTRA_KEY_URL_ARRAY = "URLS";

    public NewsFeedLoadService() {
        super(NewsFeedLoadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Context ctxt = this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);


        Log.i("Refreshing widgets " + appWidgetId);

        if (appWidgetId == -1) {
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this.getPackageName(), WidgetProvider.class.getName()));

            for (final int id : ids) {
                final String [] urls = DataSource.getSubscribedFeeds(ctxt, id);
                Log.d(appWidgetId + " Subscribed: " + Arrays.asList(urls));

                updateWidget(ctxt, id, appWidgetManager, urls);
            }

            return;
        } else {
            final String [] urls = DataSource.getSubscribedFeeds(ctxt, appWidgetId);
            Log.d(appWidgetId + " Subscribed: " + Arrays.asList(urls));

            updateWidget(ctxt, appWidgetId, appWidgetManager, urls);
        }

        stopSelf();
    }

    private synchronized void updateWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager, String [] urls) {
        Intent svcIntent = new Intent(context, WidgetService.class);

        SharedPreferences prefs = getSharedPreferences(PrefsUtil.getSharedPrefsRoot(appWidgetId), 0);

        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.putExtra(EXTRA_KEY_URL_ARRAY, urls);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews widget = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        final int bgColor = prefs.getInt("bgcolor", -1);
        if (bgColor != -1) {
            widget.setInt(R.id.widget_background, "setBackgroundColor", bgColor);
        }

        widget.setRemoteAdapter(R.id.words,
                svcIntent);

        Intent clickIntent = new Intent(Intent.ACTION_VIEW);

        PendingIntent clickPI = PendingIntent
                .getActivity(context, 0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.words, clickPI);

        appWidgetManager.updateAppWidget(appWidgetId, widget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.words);

        Log.d("Updated widget " + appWidgetId);
    }
}
