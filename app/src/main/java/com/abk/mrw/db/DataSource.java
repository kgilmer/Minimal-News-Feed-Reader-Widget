package com.abk.mrw.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.abk.mrw.model.RSSBookmarkItem;
import com.abk.mrw.model.RSSItem;
import com.abk.mrw.util.Interleaver;
import com.abk.xmlobjectiterable.core.XMLObjectIterable;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by kgilmer on 3/27/16.
 */
public class DataSource {

    private static final String PREFS_NAME = DataSource.class.getCanonicalName();

    private static final LoadingCache<URL, List<RSSItem>> feedLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(512)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<URL, List<RSSItem>>() {
                        public List<RSSItem> load(@NonNull final URL url) {
                            Log.i(DataSource.class.getCanonicalName(), "Loading: " + url);

                            try {
                                final InputStream is = url.openStream();
                                if (is != null) {
                                    XMLObjectIterable<RSSItem> xoi = new XMLObjectIterable.Builder<RSSItem>()
                                            .from(is)
                                            .pathOf(RSSItem.RSS_PATH)
                                            .withTransform(RSSItem.RSS_TRANSFORMER)
                                            .create();

                                    return Lists.newArrayList(xoi);
                                }
                            } catch (java.io.IOException e) {
                                Log.e(DataSource.class.getCanonicalName(), "Failed to load from " + url);
                            }

                            return Collections.emptyList();
                        }
                    });

    /**
     * Load all items from RSS urls;
     *
     * @param urls list of URLs
     * @return all feeds interleaved.
     */
    public static Iterable<RSSItem> getRSSItems(Iterable<URL> urls) {

        List<List<RSSItem>> rssItems = new ArrayList<>();

        for (final URL url : urls) {
            try {
                rssItems.add(Lists.newArrayList(feedLoadingCache.get(url)));
            } catch (ExecutionException e) {
                Log.e(DataSource.class.getCanonicalName(), "Failed to load data.", e);
            }
        }

        return Interleaver.fromIterables(rssItems);
    }

    public static Iterable<RSSBookmarkItem> getCuratedFeedSources(Context context) throws IOException {

        InputStream input = context.getAssets().open("sources-opml.xml");

        XMLObjectIterable<RSSBookmarkItem> xoi = new XMLObjectIterable.Builder<RSSBookmarkItem>()
                .from(input)
                .withTransform(RSSBookmarkItem.TRANSFORMER)
                .pathOf(RSSBookmarkItem.PATH)
                .create();

        return xoi;
    }

    public static String[] getSubscribedFeeds(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME + widgetId, 0);

        Set<String> subs = prefs.getStringSet("subscriptions", Collections.<String>emptySet());

        return subs.toArray(new String[subs.size()]);
    }

    public static void addSubscription(Context context, int widgetId, RSSBookmarkItem bookmark) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME + widgetId, 0);

        Set<String> subscriptions = prefs.getStringSet("subscriptions", null);
        if (subscriptions == null) {
            subscriptions = new HashSet<>(1);
        }

        subscriptions.add(bookmark.getXmlUrl());
        prefs.edit().putStringSet("subscriptions", subscriptions).apply();

        Toast.makeText(context, "Added " + bookmark.getTitle(), Toast.LENGTH_SHORT).show();
    }

    public static void removeSubscription(Context context, int widgetId, RSSBookmarkItem bookmark) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME + widgetId, 0);

        Set<String> subscriptions = prefs.getStringSet("subscriptions", null);
        if (subscriptions != null) {
            if (subscriptions.remove(bookmark.getXmlUrl())) {
                prefs.edit().putStringSet("subscriptions", subscriptions).apply();
            }
        }

        Toast.makeText(context, "Removed " + bookmark.getTitle(), Toast.LENGTH_SHORT).show();
    }
}
