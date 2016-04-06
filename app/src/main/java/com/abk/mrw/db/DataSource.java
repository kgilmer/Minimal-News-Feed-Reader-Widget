package com.abk.mrw.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;
import com.abk.mrw.model.RSSBookmarkItem;
import com.abk.mrw.model.RSSItem;
import com.abk.mrw.model.TransformFactory;
import com.abk.mrw.util.Interleaver;
import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;

import java.io.BufferedInputStream;
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

    private static final LoadingCache<String, List<RSSItem>> feedLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(512)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<RSSItem>>() {
                        public List<RSSItem> load(@NonNull final String urlStr) {
                            Log.i(DataSource.class.getCanonicalName(), "Loading: " + urlStr);

                            try {
                                final URL url = new URL(urlStr);
                                final InputStream is = url.openStream();

                                if (is != null) {
                                    final BufferedInputStream bis = new BufferedInputStream(is, TransformFactory.BUFFER_SIZE);

                                    XMLObjectIterable.Transformer<RSSItem> transformer =
                                            TransformFactory.getTransformer(bis);

                                    if (transformer != null) {
                                        XMLObjectIterable<RSSItem> xoi = new XMLObjectIterable.Builder<RSSItem>()
                                                .from(bis)
                                                .withTransform(transformer)
                                                .create();

                                        List<RSSItem> itemList = new ArrayList<RSSItem>();
                                        Iterables.addAll(itemList, xoi);

                                        return itemList;
                                    }
                                }
                            } catch (java.io.IOException e) {
                                Log.e(DataSource.class.getCanonicalName(), "Failed to load from " + urlStr);

                                final RSSItem errorItem = new RSSItem("Failed to load " + urlStr, null, null, null, null);
                                return Collections.singletonList(errorItem);
                            }

                            final RSSItem errorItem = new RSSItem("No data for " + urlStr, null, null, null, null);
                            return Collections.singletonList(errorItem);
                        }
                    });

    /**
     * Load all items from RSS urls;
     *
     * @param urls list of URLs
     * @return all feeds interleaved.
     */
    public static Iterable<RSSItem> getRSSItems(Set<String> urls) {

        List<List<RSSItem>> rssItems = new ArrayList<>();

        for (final String urlStr : urls) {
            try {
                List<RSSItem> items = feedLoadingCache.get(urlStr);
                rssItems.add(items);
            } catch (ExecutionException | RuntimeException e) {
                Log.e(DataSource.class.getCanonicalName(), "Failed to load data.", e);
            }
        }

        return Interleaver.fromIterables(rssItems);
    }

    public static Iterable<RSSBookmarkItem> getCuratedFeedSources(Context context) throws IOException {

        InputStream input = context.getAssets().open("sources-opml.xml");

        final XMLObjectIterable<RSSBookmarkItem> xoi = new XMLObjectIterable.Builder<RSSBookmarkItem>()
                .from(input)
                .withTransform(RSSBookmarkItem.TRANSFORMER)
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
