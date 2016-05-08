package com.abk.mrw.notificationui.model;

/**
 * Created by kgilmer on 5/2/16.
 */
public class SeenItem {
    private static final String FIELD_DELIMITIER = " ";

    final private long createDate;
    final private String url;

    public SeenItem(long createDate, String url) {
        this.createDate = createDate;
        this.url = url;
    }

    public SeenItem(String prefStr) {
        final int index = prefStr.indexOf(FIELD_DELIMITIER, 0);

        if (index < 0 || index > prefStr.length()) {
            throw new IllegalArgumentException("Unable to parse input: " + prefStr);
        }

        createDate = Long.parseLong(prefStr.substring(0, index));
        url = prefStr.substring(index + 1);
    }

    public long getCreateDate() {
        return createDate;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return createDate + FIELD_DELIMITIER + url;
    }
}
