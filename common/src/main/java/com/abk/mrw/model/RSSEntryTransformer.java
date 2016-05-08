package com.abk.mrw.model;

import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.base.Optional;

import java.util.Map;

import trikita.log.Log;

/**
 * Transformer for RSS feeds.
 *
 * Example:
 * <item>
 *  <title>VNC Roulette</title>
 *  <link>http://vncroulette.com</link>
 *  <pubDate>Sat, 26 Mar 2016 22:04:20 +0000</pubDate>
 *  <comments>https://news.ycombinator.com/item?id=11367666</comments>
 *  <description><![CDATA[<a href="https://news.ycombinator.com/item?id=11367666">Comments</a>]]></description>
 * </item>
 */
public class RSSEntryTransformer implements XMLObjectIterable.Transformer<FeedEntry> {

    /**
     * XML Element Path to the item
     */
    public static final String RSS_PATH = "rss/channel/item";

    private String pubDate;
    private String link;
    private String title;

    @Override
    public Optional<FeedEntry> transform() {
        if (link != null
                && title != null) {

            final FeedEntry item = new FeedEntry(title, link, pubDate);

            Log.d(FeedEntry.class.getCanonicalName(), "Created " + item.toString());

            return Optional.of(item);
        }

        return Optional.absent();
    }

    @Override
    public void visit(String name, String value, Map<String, String> attribs) {
        switch (name) {
            case "title":
                this.title = value;
                break;
            case "link":
                this.link = value;
                break;
            case "pubDate":
                this.pubDate = value;
                break;
        }
    }

    @Override
    public void reset() {
        title = null;
        link = null;
        pubDate = null;
    }

    @Override
    public String getPath() {
        return RSS_PATH;
    }
}
