package com.abk.mrw;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by kgilmer on 3/10/16.
 */
public class NetworkConnectivityReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (isWifiConnected(context)) {
            Log.i(NetworkConnectivityReceiver.class.getCanonicalName(), "Refreshing feed due to network event.");

            context.startService(WidgetViewsFactory.createRefreshIntent(context));
        }
    }

    public static boolean isWifiConnected(final Context context) {
        ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMan.getActiveNetworkInfo();
        return (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }
}
