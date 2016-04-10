package com.abk.mrw.model;

/**
 * RSS Feed Items
 * <p/>
 * Example:
 * <item>
 * <title>VNC Roulette</title>
 * <link>http://vncroulette.com</link>
 * <pubDate>Sat, 26 Mar 2016 22:04:20 +0000</pubDate>
 * <comments>https://news.ycombinator.com/item?id=11367666</comments>
 * <description><![CDATA[<a href="https://news.ycombinator.com/item?id=11367666">Comments</a>]]></description>
 * </item>
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
