package com.abk.mrw.settings;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.widget.Toast;
import com.abk.mrw.db.DataSource;
import com.abk.mrw.model.RSSBookmarkItem;
import com.kenumir.materialsettings.MaterialSettings;
import com.kenumir.materialsettings.items.CheckboxItem;
import com.kenumir.materialsettings.items.HeaderItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.PreferencesStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;

import java.io.IOException;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class CuratedSourceSettingsActivity extends MaterialSettings {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final int widgetId = getIntent().getIntExtra(EXTRA_APPWIDGET_ID, -1);
        if (widgetId == -1) {
            Log.e(CuratedSourceSettingsActivity.class.getCanonicalName(), "Invalid widget id.");
            finish();
        }

        addItem(new HeaderItem(this).setTitle("Curated Sources"));

        try {
            for (final RSSBookmarkItem bookmark : DataSource.getCuratedFeedSources(this)) {
                addItem(new CheckboxItem(this, bookmark.getXmlUrl())
                        .setTitle(bookmark.getTitle())
                        .setSubtitle(bookmark.getHtmlUrl())
                        .setOnCheckedChangeListener(new CheckboxItem.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChange(CheckboxItem checkboxItem, boolean b) {
                                if (b) {
                                    DataSource.addSubscription(CuratedSourceSettingsActivity.this, widgetId, bookmark);
                                } else {
                                    DataSource.removeSubscription(CuratedSourceSettingsActivity.this, widgetId, bookmark);
                                }
                            }
                        }));
            }
        } catch (IOException e) {
            Log.e(CuratedSourceSettingsActivity.class.getCanonicalName(), "Cannot load curated sources.", e);
        }
    }
    @Override
    public StorageInterface initStorageInterface() {
        return new PreferencesStorageInterface(this);
    }
}