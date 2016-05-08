package com.abk.mrw;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.widget.RemoteViews;
import com.abk.mrw.db.DataSource;
import com.abk.mrw.util.PrefsUtil;
import trikita.log.Log;

import java.util.Arrays;

/**
 * Service that creates widget.
 */
public class NewsFeedLoadService extends IntentService {
    //Extras key for set of feed urls of widget.
    public static final String EXTRA_KEY_URL_ARRAY = "URLS";

    public NewsFeedLoadService() {
        super(NewsFeedLoadService.class.getName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final Context context = this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);

        Log.d("Refreshing widgets " + appWidgetId);

        if (appWidgetId == -1) {
            //No app wiget id specified, refresh all widgets.
            int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(this.getPackageName(), WidgetProvider.class.getName()));

            for (final int id : ids) {
                final String[] urls = DataSource.getSubscribedFeeds(context, id);
                updateWidget(context, id, appWidgetManager, urls);
            }

            //TODO: verify following statement is not needed.
            //return;
        } else {
            final String[] urls = DataSource.getSubscribedFeeds(context, appWidgetId);

            updateWidget(context, appWidgetId, appWidgetManager, urls);
        }

        stopSelf();
    }

    /**
     * (Re)create app widget.
     *
     * @param context Context
     * @param appWidgetId widget id
     * @param appWidgetManager AppWidgetManager
     * @param urls Array of feed sources as String urls.
     */
    private synchronized void updateWidget(Context context, int appWidgetId, AppWidgetManager appWidgetManager, String[] urls) {
        Intent svcIntent = new Intent(context, WidgetService.class);

        SharedPreferences prefs = getSharedPreferences(PrefsUtil.getSharedPrefsRoot(appWidgetId), 0);

        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        svcIntent.putExtra(EXTRA_KEY_URL_ARRAY, urls);
        svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews widget = new RemoteViews(context.getPackageName(),
                R.layout.widget);

        setBackgroundColor(prefs, widget);

        widget.setRemoteAdapter(R.id.feedList,
                svcIntent);

        Intent clickIntent = new Intent(Intent.ACTION_VIEW);

        PendingIntent clickPI = PendingIntent
                .getActivity(context, 0,
                        clickIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        widget.setPendingIntentTemplate(R.id.feedList, clickPI);

        appWidgetManager.updateAppWidget(appWidgetId, widget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.feedList);

        Log.d("Updated widget " + appWidgetId);
    }

    /**
     * Set the widget background gradient based on color selection.
     *
     * @param prefs SharedPreferences
     * @param widget RemoteViews
     */
    private void setBackgroundColor(SharedPreferences prefs, RemoteViews widget) {
        final int bgColor = prefs.getInt("bgcolor", -1);

        if (bgColor != -1) {
            final Resources res = getResources();
            if (bgColor == res.getColor(R.color.md_amber_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_amber);
            } else if (bgColor == res.getColor(R.color.md_blue_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_blue);
            } else if (bgColor == res.getColor(R.color.md_blue_grey_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_blue_gray);
            } else if (bgColor == res.getColor(R.color.md_brown_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_brown);
            } else if (bgColor == res.getColor(R.color.md_cyan_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_cyan);
            } else if (bgColor == res.getColor(R.color.md_deep_orange_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_deep_orange);
            } else if (bgColor == res.getColor(R.color.md_deep_purple_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_deep_purple);
            } else if (bgColor == res.getColor(R.color.md_green_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_green);
            } else if (bgColor == res.getColor(R.color.md_grey_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_grey);
            } else if (bgColor == res.getColor(R.color.md_indigo_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_indigo);
            } else if (bgColor == res.getColor(R.color.md_light_blue_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_light_blue);
            } else if (bgColor == res.getColor(R.color.md_light_green_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_light_green);
            } else if (bgColor == res.getColor(R.color.md_lime_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_lime);
            } else if (bgColor == res.getColor(R.color.md_orange_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_orange);
            } else if (bgColor == res.getColor(R.color.md_pink_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_pink);
            } else if (bgColor == res.getColor(R.color.md_purple_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_purple);
            } else if (bgColor == res.getColor(R.color.md_red_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_red);
            } else if (bgColor == res.getColor(R.color.md_teal_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_teal);
            } else if (bgColor == res.getColor(R.color.md_yellow_500)) {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_yellow);
            } else {
                widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_blue_gray);
            }
        } else {
            widget.setImageViewResource(R.id.widget_background, R.drawable.gradient_background_blue_gray);
        }
    }
}
