package com.abk.mrw;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import trikita.log.Log;

/**
 * Android Widget API boilerplate to call the service that creates the widgets.
 */
public class WidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
                         int[] appWidgetIds) {

        for (int appWidgetId : appWidgetIds) {
            Intent si = new Intent(context, NewsFeedLoadService.class);
            si.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            context.startService(si);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}