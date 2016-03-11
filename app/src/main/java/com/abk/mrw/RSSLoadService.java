package com.abk.mrw;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.security.PublicKey;

/**
 * Created by kgilmer on 2/14/16.
 */
public class RSSLoadService extends IntentService {
    private static final String [] EXAMPLE_URLS = {
            "https://news.ycombinator.com/rss",
            "https://news.google.com/news?pz=1&cf=all&ned=us&hl=en&output=rss",
            "http://feeds.bbci.co.uk/news/world/us_and_canada/rss.xml?edition=int",
            "http://www.npr.org/rss/rss.php?id=1001"
    };
    public static final String EXTRA_KEY_URL_ARRAY = "URLS";

    public RSSLoadService() {
        super(RSSLoadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Context ctxt = this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        final String [] urls =
                intent.getStringExtra("url") != null ? new String[] {intent.getStringExtra("url")} : EXAMPLE_URLS;

        Log.i(this.getClass().getCanonicalName(), "Refreshing widgets");

        if (appWidgetId == -1) {
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this.getPackageName(), WidgetProvider.class.getName()));

            for (final int id : ids) {
                updateWidget(ctxt, id, appWidgetManager, urls);
            }

            return;
        } else {
            updateWidget(ctxt, appWidgetId, appWidgetManager, urls);
        }

        stopSelf();
    }

    private synchronized void updateWidget(Context ctxt, int appWidgetId, AppWidgetManager appWidgetManager, String [] urls) {
        Intent svcIntent = new Intent(ctxt, WidgetService.class);

        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        //svcIntent.putExtra("RSS_URL", url);
        svcIntent.putExtra(EXTRA_KEY_URL_ARRAY, urls);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews widget = new RemoteViews(ctxt.getPackageName(),
                R.layout.widget);

        widget.setRemoteAdapter(R.id.words,
                svcIntent);

        Intent clickIntent = new Intent(Intent.ACTION_VIEW);

        //Intent clickIntent = new Intent(ctxt, LoremActivity.class);
        PendingIntent clickPI = PendingIntent
                .getActivity(ctxt, 0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.words, clickPI);

        appWidgetManager.updateAppWidget(appWidgetId, widget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.words);

        Log.i(RSSLoadService.class.getCanonicalName(), "Updated widget " + appWidgetId);
    }
}
