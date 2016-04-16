package com.abk.mrw.settings;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.Toast;
import com.abk.mrw.NewsFeedLoadService;
import com.abk.mrw.R;
import com.abk.mrw.util.PrefsUtil;
import com.google.common.base.Optional;
import trikita.log.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

/**
 * Created by kgilmer on 3/29/16.
 */
public class SettingsActivity extends PreferenceActivity {

    private SettingsFragment settingsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        settingsFragment = new SettingsFragment();
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, settingsFragment)
                .commit();

        Optional<Integer> widgetIdOpt = getSourceWidgetId();
        if (widgetIdOpt.isPresent()) {
            PrefsUtil.setWidgetId(widgetIdOpt.get());
        } else if (!PrefsUtil.hasWidgetId()) {
            Log.w("No widget id.");
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        SharedPreferences prefs = settingsFragment.getPreferences();
        Set<String> feeds = prefs.getStringSet("pref_feeds", new HashSet<String>());

        if (feeds.isEmpty()) {
            Toast.makeText(getApplicationContext(), R.string.no_feed_message, Toast.LENGTH_LONG).show();
        } else {
            Intent startService = new Intent(this,
                    NewsFeedLoadService.class);
            startService.putExtra(EXTRA_APPWIDGET_ID, PrefsUtil.getWidgetId());
            setResult(RESULT_OK, startService);
            startService(startService);
        }

        super.onBackPressed();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        setContentView(R.layout.settings_page);
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
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