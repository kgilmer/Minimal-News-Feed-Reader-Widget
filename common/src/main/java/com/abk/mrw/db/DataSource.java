package com.abk.mrw.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.abk.mrw.model.FeedEntry;
import com.abk.mrw.model.TransformFactory;
import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.base.Optional;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.iheart.interleaver.Interleaver;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import trikita.log.Log;

/**
 * Encapsulates network operations.  Retrieves feed data.
 */
public final class DataSource {
    //Max number of feed items to cache.
    public static final int MAX_CACHED_FEEDS = 512;

    private static final String PREFS_NAME = DataSource.class.getCanonicalName();

    private static final LoadingCache<String, List<FeedEntry>> feedLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(MAX_CACHED_FEEDS)
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build(
                    new CacheLoader<String, List<FeedEntry>>() {
                        public List<FeedEntry> load(@NonNull final String urlStr) {
                            try {
                                final URL url = new URL(urlStr);
                                final InputStream is = url.openStream();

                                if (is != null) {
                                    //Buffered stream used to peek at first bytes to determine feed format (Atom, RSS).
                                    final BufferedInputStream bis = new BufferedInputStream(is, TransformFactory.BUFFER_SIZE);

                                    //Peek at data to determine format.
                                    Optional<XMLObjectIterable.Transformer<FeedEntry>> transformer =
                                            TransformFactory.getTransformer(bis);

                                    if (transformer.isPresent()) {
                                        //Parse feed XML into FeedEntry instances.
                                        XMLObjectIterable<FeedEntry> xoi = new XMLObjectIterable.Builder<FeedEntry>()
                                                .from(bis)
                                                .withTransform(transformer.get())
                                                .create();

                                        //Load FeedItems into a collection.  Iterable can only be consumed once.
                                        return Lists.newArrayList(xoi);
                                    } else {
                                        Log.w("Returning no data, no factory found for URL: ", urlStr);
                                    }
                                }
                            } catch (java.io.IOException e) {
                                Log.e("Failed to load from " + urlStr);

                                //Display an error to user that network or parse error occurred.
                                final FeedEntry errorItem = new FeedEntry("Failed to load " + urlStr, null, null);
                                return Collections.singletonList(errorItem);
                            }

                            //Display an error to user that network or parse error occurred.
                            final FeedEntry errorItem = new FeedEntry("No data for " + urlStr, null, null);
                            return Collections.singletonList(errorItem);
                        }
                    });

    private DataSource() {}

    /**
     * Load all items from RSS urls;
     *
     * @param urls list of URLs
     * @return all feeds interleaved.
     */
    public static Iterable<FeedEntry> getRSSItems(Set<String> urls) {

        List<List<FeedEntry>> rssItems = new ArrayList<>();

        for (final String urlStr : urls) {
            try {
                List<FeedEntry> items = feedLoadingCache.get(urlStr);
                rssItems.add(items);
            } catch (ExecutionException | RuntimeException e) {
                Log.e("Failed to load data.", e);
            }
        }

        return Interleaver.fromIterables(rssItems);
    }


    /**
     * @param context Context
     * @param widgetId widgetId
     * @return array of URLs as strings of feeds for widget
     */
    public static String[] getSubscribedFeeds(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME + widgetId, 0);

        Set<String> subs = prefs.getStringSet("subscriptions", Collections.<String>emptySet());

        return subs.toArray(new String[subs.size()]);
    }

}
