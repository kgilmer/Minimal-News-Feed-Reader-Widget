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
 * Created by kgilmer on 3/27/16.
 */
public final class DataSource {

    private static final String PREFS_NAME = DataSource.class.getCanonicalName();

    private static final LoadingCache<String, List<FeedEntry>> feedLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(512)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, List<FeedEntry>>() {
                        public List<FeedEntry> load(@NonNull final String urlStr) {
                            Log.i("Loading: " + urlStr);

                            try {
                                final URL url = new URL(urlStr);
                                final InputStream is = url.openStream();

                                if (is != null) {
                                    final BufferedInputStream bis = new BufferedInputStream(is, TransformFactory.BUFFER_SIZE);

                                    Optional<XMLObjectIterable.Transformer<FeedEntry>> transformer =
                                            TransformFactory.getTransformer(bis);

                                    if (transformer.isPresent()) {
                                        XMLObjectIterable<FeedEntry> xoi = new XMLObjectIterable.Builder<FeedEntry>()
                                                .from(bis)
                                                .withTransform(transformer.get())
                                                .create();

                                        List<FeedEntry> itemList = new ArrayList<FeedEntry>();
                                        Iterables.addAll(itemList, xoi);

                                        return itemList;
                                    } else {
                                        Log.i("Returning no data, no factory found for URL: ", urlStr);
                                    }
                                }
                            } catch (java.io.IOException e) {
                                Log.e("Failed to load from " + urlStr);

                                final FeedEntry errorItem = new FeedEntry("Failed to load " + urlStr, null, null);
                                return Collections.singletonList(errorItem);
                            }

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


    public static String[] getSubscribedFeeds(Context context, int widgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME + widgetId, 0);

        Set<String> subs = prefs.getStringSet("subscriptions", Collections.<String>emptySet());

        return subs.toArray(new String[subs.size()]);
    }

}
