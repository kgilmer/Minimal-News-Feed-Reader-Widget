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

public class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getActivity().setTheme(R.style...);

        getPreferenceManager().setSharedPreferencesName(PrefsUtil.getSharedPrefsRoot());
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        addPreferencesFromResource(R.xml.settings);

        /*
        if (getArguments() != null) {
            String page = getArguments().getString("page");
            if (page != null) {
                PrefsUtil.setCurrentFragment(page);
                switch (page) {
                    case "feeds":
                        addPreferencesFromResource(R.xml.feeds);
                        break;
                    case "style":
                        addPreferencesFromResource(R.xml.style);
                        break;
                }
            }
        } else {
            addPreferencesFromResource(R.xml.feeds);
        }
        */
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.settings_page, container, false);
        if (layout != null) {
            AppCompatPreferenceActivity activity = (AppCompatPreferenceActivity) getActivity();

            /*
            Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolbar);
            activity.setSupportActionBar(toolbar);

            ActionBar bar = activity.getSupportActionBar();
            bar.setHomeButtonEnabled(true);
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setDisplayShowTitleEnabled(true);
            bar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            bar.setTitle("Settings");
            */
        }
        return layout;
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

    private void removeFeedFromList(SharedPreferences prefs, String key) {
        Set<String> feeds = prefs.getStringSet("pref_feeds", new HashSet<String>());
        int beforeHash = feeds.hashCode();
        String removedUrl = key.substring(9);
        Log.i("removed " + removedUrl);
        feeds.remove(removedUrl);

        if (beforeHash != feeds.hashCode()) {
            prefs.edit().remove(key);
            prefs.edit().putStringSet("pref_feeds", feeds).apply();

            refreshFeedList(feeds);
        }
    }

    private void updateFeedList(SharedPreferences prefs) {
        Set<String> feeds = prefs.getStringSet("pref_feeds", new HashSet<String>());
        int beforeHash = feeds.hashCode();
        feeds.addAll(prefs.getStringSet("pref_popularSources", Collections.<String>emptySet()));
        prefs.edit().remove("pref_popularSources").apply();
        String manualFeed = prefs.getString("pref_manualSource", null);
        if (manualFeed != null) {
            feeds.add(manualFeed);
            prefs.edit().remove("pref_manualSource").apply();
        }
        if (beforeHash != feeds.hashCode()) {
            prefs.edit().putStringSet("pref_feeds", feeds).apply();

            refreshFeedList(feeds);
        }
    }

    private void refreshFeedList(Set<String> feeds) {
        Log.i("Feeds: " + feeds);

        PreferenceCategory targetCategory = (PreferenceCategory) findPreference("feeds");

        targetCategory.removeAll();

        for (final String url : feeds) {
            //create one check box for each setting you need
            CheckBoxPreference checkBoxPreference = new CheckBoxPreference(this.getActivity());
            checkBoxPreference.setKey("custPref_" + url);
            checkBoxPreference.setTitle(url);
            checkBoxPreference.setChecked(true);

            targetCategory.addPreference(checkBoxPreference);
        }
    }
}