package com.abk.mrw;

import android.content.Intent;
import android.widget.RemoteViewsService;
import trikita.log.Log;

public class WidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        Log.d("onGetViewFactory()");

        return (new WidgetViewsFactory(this.getApplicationContext(),
                intent));
    }
}