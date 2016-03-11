package com.abk.mrw;

import android.support.annotation.NonNull;
import android.util.Log;
import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.sql.SQLOutput;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Feed {

    private static final Feed EMPTY_FEED = new Feed();
    private static final LoadingCache<String, Feed> feedLoadingCache = CacheBuilder.newBuilder()
            .maximumSize(512)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build(
                    new CacheLoader<String, Feed>() {
                        public Feed load(@NonNull final String url) {
                            Log.i(Feed.class.getCanonicalName(), "Loading: " + url);
                            RSSFeedParser parser = new RSSFeedParser(url);

                            return MoreObjects.firstNonNull(parser.readFeed(), EMPTY_FEED);
                        }
                    });

    public synchronized static Feed get(String url) {
        try {
            return feedLoadingCache.get(url);
        } catch (ExecutionException e) {
            Log.e(Feed.class.getCanonicalName(), "Failed to load feed.", e);
            return EMPTY_FEED;
        }
    }

    final String title;
    final String link;
    final String description;
    final String language;
    final String copyright;
    final String pubDate;

    final List<FeedMessage> entries = new ArrayList<FeedMessage>();

    private Feed() {
        this("empty", "", null, null, null, null);
    }


    public Feed(String title, String link, String description, String language,
                String copyright, String pubDate) {
        this.title = title;
        this.link = link;
        this.description = description;
        this.language = language;
        this.copyright = copyright;
        this.pubDate = pubDate;
    }

    public List<FeedMessage> getMessages() {
        return entries != null ? entries : Collections.<FeedMessage>emptyList();
    }

    public String getTitle() {
        return title;
    }

    public String getLink() {
        return link;
    }

    public String getDescription() {
        return description;
    }

    public String getLanguage() {
        return language;
    }

    public String getCopyright() {
        return copyright;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public String toString() {
        return "Feed [copyright=" + copyright + ", description=" + description
                + ", language=" + language + ", link=" + link + ", pubDate="
                + pubDate + ", title=" + title + "]";
    }

}
