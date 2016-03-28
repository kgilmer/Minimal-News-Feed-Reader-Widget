package com.abk.mrw.settings;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import com.kenumir.materialsettings.MaterialSettings;
import com.kenumir.materialsettings.items.CheckboxItem;
import com.kenumir.materialsettings.items.HeaderItem;
import com.kenumir.materialsettings.items.TextItem;
import com.kenumir.materialsettings.storage.PreferencesStorageInterface;
import com.kenumir.materialsettings.storage.StorageInterface;

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
public class CustomSourceSettingsActivity extends MaterialSettings {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addItem(new HeaderItem(this).setTitle("Custom Sources"));
        addItem(new TextItem(this, "addUrl").setTitle("Add Source...").setOnclick(new TextItem.OnClickListener() {
            @Override
            public void onClick(TextItem textItem) {
                Toast.makeText(CustomSourceSettingsActivity.this, "clicked", Toast.LENGTH_SHORT).show();
            }
        }));

    }
    @Override
    public StorageInterface initStorageInterface() {
        return new PreferencesStorageInterface(this);
    }
}