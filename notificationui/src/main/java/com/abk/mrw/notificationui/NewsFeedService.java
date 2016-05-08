package com.abk.mrw.notificationui;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.abk.mrw.db.DataSource;
import com.abk.mrw.model.FeedEntry;
import com.abk.mrw.notificationui.model.SeenItem;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.squareup.okhttp.OkHttpClient;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Loads the WOTD from the WS and creates an Android notification.
 */
public class NewsFeedService extends IntentService {

    private static final long ONE_DAY_IN_MILLIS = 86400000;
    private static final long MAX_SAVE_TIME_MILLIS = 86400000;
    private static final int NOTIFICATION_ID = 643258234;
    private static final String FEED_SEEN_ITEM_PREF_KEY = "seenFeedItems6";

    private final OkHttpClient client = new OkHttpClient();

    public NewsFeedService() {
        super(NewsFeedService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(final Intent intent) {
        if (isEnabled()) {
            renderNotification();
        }
    }

    private void renderNotification() {
        final PendingIntent skipPendingIntent =
                PendingIntent.getService(this, 0, new Intent(this, NewsFeedService.class), 0);

        // Get Feed Data
        List<FeedEntry> newsItems = new ArrayList<>();
        Iterables.addAll(newsItems, DataSource.getRSSItems(Collections.singleton("https://news.ycombinator.com/rss")));

        // Get previously seen items
        final SharedPreferences prefs = this.getSharedPreferences(NewsFeedService.class.getCanonicalName(), Context.MODE_PRIVATE);
        final Set<SeenItem> seenItems = Sets.newHashSet(Iterables.transform(prefs.getStringSet(FEED_SEEN_ITEM_PREF_KEY, Collections.<String>emptySet()), new Function<String, SeenItem>() {
            @Override
            public SeenItem apply(String input) {
                return new SeenItem(input);
            }
        }));

        // Get URLs of seen items for filtering.
        final Set<String> seenItemURLs = Sets.newHashSet(Iterables.transform(seenItems, new Function<SeenItem, String>() {
            @Override
            public String apply(SeenItem input) {
                return input.getUrl();
            }
        }));

        Predicate<FeedEntry> seenItemFilter = new Predicate<FeedEntry>() {
            @Override
            public boolean apply(FeedEntry input) {
                return !seenItemURLs.contains(input.getUrl());
            }
        };
        Iterable<FeedEntry> newNewsItems = Iterables.filter(newsItems, seenItemFilter);
        Iterator<FeedEntry> itr = newNewsItems.iterator();

        if (itr.hasNext()) {
            FeedEntry nextNewsItem = itr.next();

            seenItems.add(new SeenItem(System.currentTimeMillis(), nextNewsItem.getUrl()));
            Iterable<SeenItem> filteredItems = Iterables.filter(seenItems, new Predicate<SeenItem>() {
                @Override
                public boolean apply(SeenItem input) {
                    return !((System.currentTimeMillis() - input.getCreateDate()) > MAX_SAVE_TIME_MILLIS);
                }
            });
            Set<String> filteredItemSet = Sets.newHashSet(Iterables.transform(filteredItems, new Function<SeenItem, String>() {
                @Override
                public String apply(SeenItem input) {
                    return input.toString();
                }
            }));
            prefs.edit().putStringSet(FEED_SEEN_ITEM_PREF_KEY, filteredItemSet).apply();

            final Intent linkIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(nextNewsItem.getUrl()));
            final PendingIntent linkPendingIntent =
                    PendingIntent.getActivity(this, 0, linkIntent, 0);

            String [] title = formatTitle(nextNewsItem.getTitle());

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_hot_tub_white_48dp)
                    .setContentTitle(title[0])
                    .setContentIntent(linkPendingIntent)
                    .setPriority(Notification.PRIORITY_MIN)
                    .addAction(R.drawable.ic_skip_next_black_18dp, "Skip", skipPendingIntent)
                    .addAction(R.drawable.ic_info_black_24dp, "Read", linkPendingIntent);

            if (!Strings.isNullOrEmpty(title[1])) {
                notificationBuilder.setContentText(title[1]);
            }

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    protected static void scheduleAlarm(final Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 7);

        final PendingIntent alarmIntent =
                PendingIntent.getService(context, 0, new Intent(context, NewsFeedService.class), 0);

        // With setInexactRepeating(), you have to use one of the AlarmManager interval
        // constants--in this case, AlarmManager.INTERVAL_DAY.
        AlarmManager alarmMgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        alarmMgr.cancel(alarmIntent);
        alarmMgr.setInexactRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, alarmIntent);
    }

    public boolean isEnabled() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        return sp.getBoolean("appEnabledPref", true);
    }

    protected static String[] formatTitle(String title) {
        int maxCharLenInTitle = 32;

        List<String> words = Splitter.on(' ').trimResults().splitToList(title);
        StringBuilder header = new StringBuilder();
        StringBuilder summary = new StringBuilder();

        for (String word : words) {
            if (header.length() + word.length() < maxCharLenInTitle) {
                header.append(word);
                header.append(' ');
            } else {
                summary.append(word);
                summary.append(' ');
            }
        }

        if (header.length() == 0) {
            return new String[] {summary.toString(), ""};
        }

        return new String[] {header.toString(), summary.toString()};
    }

}
