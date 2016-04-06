package com.abk.mrw.model;

import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * RSS OPML
 *
 * Example:
 * <opml version="1.1">
 * <head>
 *  <title>NewsBlur Feeds</title>
 *  <dateCreated>2013-12-24 23:58:13.332791</dateCreated>
 *  <dateModified>2013-12-24 23:58:13.332791</dateModified>
 * </head>
 * <body>
 *  <outline text="Frontend" title="Frontend">
 *      <outline htmlUrl="http://www.smashingmagazine.com" text="Smashing Magazine" title="Smashing Magazine" type="rss" version="RSS" xmlUrl="http://rss1.smashingmagazine.com/feed/" />
 *  </outline>
 * </body>
 *
 */
public class RSSBookmarkItem {
    public static final String PATH = "opml/body/outline/outline";
    private final String title;
    private final String htmlUrl;
    private final String type;
    private final String xmlUrl;

    public RSSBookmarkItem(String title, String htmlUrl, String type, String xmlUrl) {
        this.title = title;
        this.htmlUrl = htmlUrl;
        this.type = type;
        this.xmlUrl = xmlUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getType() {
        return type;
    }

    public String getXmlUrl() {
        return xmlUrl;
    }

    @Override
    public String toString() {
        return title;
    }

    public static final XMLObjectIterable.Transformer<RSSBookmarkItem> TRANSFORMER = new XMLObjectIterable.Transformer<RSSBookmarkItem>() {
        public RSSBookmarkItem item;

        @Override
        public Optional<RSSBookmarkItem> transform() {
            return Optional.fromNullable(item);
        }

        @Override
        public void visit(String name, String value, Map<String, String> attribs) {
            if (name.equals("outline") && attribs.containsKey("xmlUrl") && item == null) {
                item = new RSSBookmarkItem(
                        attribs.get("title"),
                        attribs.get("htmlUrl"),
                        attribs.get("type"),
                        attribs.get("xmlUrl"));
            }
        }

        @Override
        public void reset() {
            item = null;
        }

        @Override
        public String getPath() {
            return PATH;
        }
    };
}