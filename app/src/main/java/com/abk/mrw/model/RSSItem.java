package com.abk.mrw.model;

import com.abk.xmlobjectiterable.core.XMLObjectIterable;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * RSS Feed Items
 * <p>
 * Example:
 * <item>
 * <title>VNC Roulette</title>
 * <link>http://vncroulette.com</link>
 * <pubDate>Sat, 26 Mar 2016 22:04:20 +0000</pubDate>
 * <comments>https://news.ycombinator.com/item?id=11367666</comments>
 * <description><![CDATA[<a href="https://news.ycombinator.com/item?id=11367666">Comments</a>]]></description>
 * </item>
 */
public class RSSItem {

    /**
     * XML Element Path to the item
     */
    public static final String RSS_PATH = "rss/channel/item";

    private final String title;
    private final String url;
    private final String pubDate;
    private final String comments;
    private final String description;

    public RSSItem(String title, String url, String pubDate, String comments, String description) {
        this.title = title;
        this.url = url;
        this.pubDate = pubDate;
        this.comments = comments;
        this.description = description;
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

    public String getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return title;
    }

    public static final XMLObjectIterable.Transformer<RSSItem> RSS_TRANSFORMER = new XMLObjectIterable.Transformer<RSSItem>() {
        private String description;
        private String comments;
        private String pubDate;
        private String link;
        private String title;

        @Override
        public Optional<RSSItem> transform() {
            if (description != null
                    && comments != null
                    && pubDate != null
                    && link != null
                    && title != null) {

                return Optional.of(new RSSItem(title, link, pubDate, comments, description));
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
                case "comments":
                    this.comments = value;
                    break;
                case "description":
                    this.description = value;
                    break;
            }
        }

        @Override
        public void reset() {
            title = null;
            link = null;
            pubDate = null;
            comments = null;
            description = null;
        }
    };
}
