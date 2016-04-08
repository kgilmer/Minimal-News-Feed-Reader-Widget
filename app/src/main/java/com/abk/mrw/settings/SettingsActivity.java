package com.abk.mrw.settings;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.abk.mrw.R;
import com.abk.mrw.RSSLoadService;
import com.abk.mrw.util.PrefsUtil;
import com.google.common.base.Optional;

import java.util.List;

import static android.appwidget.AppWidgetManager.EXTRA_APPWIDGET_ID;
import static android.appwidget.AppWidgetManager.INVALID_APPWIDGET_ID;

/**
 * Created by kgilmer on 3/29/16.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);

        setTitle("Settings");

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();

        Optional<Integer> widgetIdOpt = getSourceWidgetId();
        if (widgetIdOpt.isPresent()) {
            PrefsUtil.setWidgetId(widgetIdOpt.get());
        } else if (!PrefsUtil.hasWidgetId()) {
            Log.w(SettingsActivity.class.getCanonicalName(), "No widget id.");
            finish();
            return;
        }
    }

    @Override
    public void onBackPressed() {
        //Root settings page.
        Intent startService = new Intent(this,
                RSSLoadService.class);
        startService.putExtra(EXTRA_APPWIDGET_ID, PrefsUtil.getWidgetId());
        setResult(RESULT_OK, startService);
        startService(startService);

        super.onBackPressed();
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        //loadHeadersFromResource(R.xml.settings, target);

        setContentView(R.layout.settings_page);
        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar bar = getSupportActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(true);
        bar.setHomeAsUpIndicator(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        bar.setTitle("boo");
        */
    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        return SettingsFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
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