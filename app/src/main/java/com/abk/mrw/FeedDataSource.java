package com.abk.mrw;

import java.util.HashMap;
import java.util.Map;

public class FeedDataSource {

    private static final Map<String, FeedDataSource> sourceMap = new HashMap<>();

    private final Feed feed;

    private FeedDataSource(String url) {
        RSSFeedParser parser = new RSSFeedParser(url);
        feed = parser.readFeed();
    }

    public Feed getFeed() {
        return feed;
    }

    public static FeedDataSource get(String url) {
        if (!sourceMap.containsKey(url)) {
            sourceMap.put(url, new FeedDataSource(url));
        }

        return sourceMap.get(url);
    }
}
