package com.abk.mrw.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.abk.mrw.R;
import com.abk.mrw.util.PrefsUtil;
import trikita.log.Log;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Single fragment for widget settings.  This class contains the UI interaction
 * for the settings activity.
 */
public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add the widget ID to the preference name.
        getPreferenceManager().setSharedPreferencesName(PrefsUtil.getSharedPrefsRoot());
        // When feeds are added, add them to the root settings so they can be viewed and removed.
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

        addPreferencesFromResource(R.xml.settings);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settings_page, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (getView() != null) {
            View frame = (View) getView().getParent();
            if (frame != null)
                frame.setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.contains("pref_popularSources") || key.contains("pref_manualSource")) {
            updateFeedList(sharedPreferences);
        } else if (key.startsWith("custPref_")) {
            removeFeedFromList(sharedPreferences, key);
        }
    }

    /**
     * Remove an existing feed from the list of feeds.
     *
     * @param prefs SharedPreferences
     * @param key key of feed
     */
    private void removeFeedFromList(SharedPreferences prefs, String key) {
        Set<String> feeds = prefs.getStringSet("pref_feeds", new HashSet<String>());
        int beforeHash = feeds.hashCode();
        String removedUrl = key.substring(9);
        Log.d("removed " + removedUrl);
        feeds.remove(removedUrl);

        if (beforeHash != feeds.hashCode()) {
            prefs.edit().remove(key);
            prefs.edit().putStringSet("pref_feeds", feeds).apply();

            refreshFeedList(feeds);
        }
    }

    /**
     * (Re)create the existing feed list from preference data.
     *
     * @param prefs SharedPreferences
     */
    private void updateFeedList(SharedPreferences prefs) {
        Set<String> feeds = prefs.getStringSet("pref_feeds", new HashSet<String>());
        int beforeHash = feeds.hashCode();
        feeds.addAll(prefs.getStringSet("pref_popularSources", Collections.<String>emptySet()));
        prefs.edit().remove("pref_popularSources").apply();
        String manualFeed = prefs.getString("pref_manualSource", null);

        if (manualFeed != null) {
            //User specified custom URL
            feeds.add(manualFeed);
            prefs.edit().remove("pref_manualSource").apply();
        }

        if (beforeHash != feeds.hashCode()) {
            prefs.edit().putStringSet("pref_feeds", feeds).apply();

            refreshFeedList(feeds);
        }
    }

    /**
     * Create preference views for each feed defined for the widget.
     *
     * @param feeds Set of feeds.
     */
    private void refreshFeedList(Set<String> feeds) {
        Log.d("Feeds: " + feeds);

        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("feeds");

        targetCategory.removeAll();

        for (final String url : feeds) {
            //create one check box for each setting you need
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this.getActivity());
            checkBoxPreference.setKey("custPref_" + url);
            checkBoxPreference.setTitle(url);
            checkBoxPreference.setChecked(true);
            checkBoxPreference.setIcon(R.drawable.ic_rss_feed_black_24dp);

            targetCategory.addPreference(checkBoxPreference);
        }
    }

    /**
     * @return SharedPreferences instance associated with this widget.
     */
    public SharedPreferences getPreferences() {
        return getPreferenceManager().getSharedPreferences();
    }
}