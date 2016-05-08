package com.abk.mrw.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.abk.mrw.WidgetViewsFactory;
import trikita.log.Log;

/**
 * Listens to network change events and will fire intent
 * to refresh widget if device is connected via Wifi.
 */
public class NetworkConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isWifiConnected(context)) {
            Log.d("Refreshing feed due to network event.");

            context.startService(WidgetViewsFactory.createRefreshIntent(context));
        }
    }

    /**
     * @param context Context
     * @return true if connected to wifi network
     */
    private static boolean isWifiConnected(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        return (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
