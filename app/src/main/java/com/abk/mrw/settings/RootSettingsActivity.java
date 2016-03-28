package com.abk.mrw.settings;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import com.abk.mrw.RSSLoadService;
import com.abk.mrw.WidgetService;
import com.google.common.base.Optional;
import com.kenumir.materialsettings.MaterialSettings;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.PreferencesStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;

public class RootSettingsActivity extends MaterialSettings {

    private int widgetId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        Optional<Integer> widgetIdOpt = getSourceWidgetId();
        if (!widgetIdOpt.isPresent()) {
            Log.w(RootSettingsActivity.class.getCanonicalName(), "No widget id.");
            finish();
            return;
        }
        widgetId = widgetIdOpt.get();

        addItem(new TextItem(this, "curatedSources")
                .setTitle("Curated Sources")
                .setSubtitle("Select from pre-defined feeds.")
                .setOnclick(new TextItem.OnClickListener() {
                    @Override
                    public void onClick(TextItem textItem) {
                        Intent intent = new Intent(RootSettingsActivity.this, CuratedSourceSettingsActivity.class);
                        intent.putExtra(EXTRA_APPWIDGET_ID, widgetId);
                        startActivity(intent);
                    }
                }));

        addItem(new TextItem(this, "customSources")
                .setTitle("Custom Sources")
                .setSubtitle("Add your own URLs.")
                .setOnclick(new TextItem.OnClickListener() {
                    @Override
                    public void onClick(TextItem textItem) {
                        Intent intent = new Intent(RootSettingsActivity.this, CuratedSourceSettingsActivity.class);
                        intent.putExtra(EXTRA_APPWIDGET_ID, widgetId);
                        startActivity(new Intent(RootSettingsActivity.this, CuratedSourceSettingsActivity.class));
                    }
                }));

    }

    @Override
    public void onBackPressed() {
        Intent startService = new Intent(this,
                RSSLoadService.class);
        startService.putExtra(EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, startService);
        startService(startService);

        super.onBackPressed();
    }

    @Override
    public StorageInterface initStorageInterface() {
        return new PreferencesStorageInterface(this);
    }

    private Optional<Integer> getSourceWidgetId() {

        int widgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            widgetId = extras.getInt(EXTRA_APPWIDGET_ID,
                    INVALID_APPWIDGET_ID);
        }
        if (widgetId != INVALID_APPWIDGET_ID) {
            return Optional.of(widgetId);
        }

        return Optional.absent();
    }
}