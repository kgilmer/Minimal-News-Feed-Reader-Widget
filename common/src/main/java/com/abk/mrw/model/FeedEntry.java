package com.abk.mrw.model;

/**
 * Protocol independent model class for a feed entry.
 */
public class FeedEntry {

    private final String title;
    private final String url;
    private final String pubDate;

    public FeedEntry(String title, String url, String pubDate) {
        this.title = title;
        this.url = url;
        this.pubDate = pubDate;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPubDate() {
        return pubDate;
    }

    @Override
    public String toString() {
        return title;
    }
}
