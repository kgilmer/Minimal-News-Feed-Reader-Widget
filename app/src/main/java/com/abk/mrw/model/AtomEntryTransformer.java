package com.abk.mrw.model;

import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.base.Optional;

import java.util.Map;

/**
 * <feed xmlns="http://www.w3.org/2005/Atom">
 * ...
 * <entry>
 * <title type="html"><![CDATA[Passwords are still an anti-pattern]]></title>
 * <link href="http://shapeshed.com/passwords-are-still-an-anti-pattern"/>
 * <updated>2014-05-30T10:14:01.000Z</updated>
 * <id>http://shapeshed.com/passwords-are-still-an-anti-pattern</id>
 */
public class AtomEntryTransformer implements XMLObjectIterable.Transformer<FeedEntry> {

    public static final String ATOM_ENTRY_PATH = "feed/entry";

    private String title;
    private String link;
    private String updated;

    @Override
    public Optional<FeedEntry> transform() {
        if (title == null || link == null || updated == null) {
            return Optional.absent();
        }

        return Optional.of(new FeedEntry(title, link, updated));
    }

    @Override
    public void visit(String element, String value, Map<String, String> attribs) {
        switch (element.toLowerCase()) {
            case "title":
                this.title = value;
                break;
            case "link":
                this.link = attribs.get("href");
                break;
            case "updated":
                this.updated = value;
                break;
            default:
                break;
        }
    }

    @Override
    public void reset() {
        this.link = null;
        this.title = null;
        this.updated = null;
    }

    @Override
    public String getPath() {
        return ATOM_ENTRY_PATH;
    }
}
