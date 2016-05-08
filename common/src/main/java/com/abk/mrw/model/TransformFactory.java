package com.abk.mrw.model;

import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.base.Optional;

import java.io.BufferedInputStream;
import java.io.IOException;

import trikita.log.Log;

/**
 * Created by kgilmer on 4/5/16.
 */
public class TransformFactory {

    public static final int BUFFER_SIZE = 1024;
    private static final XMLObjectIterable.Transformer<FeedEntry> rssTransformer = new RSSEntryTransformer();
    private static final XMLObjectIterable.Transformer<FeedEntry> atomTransformer = new AtomEntryTransformer();

    public static Optional<XMLObjectIterable.Transformer<FeedEntry>> getTransformer(BufferedInputStream bis) {
        bis.mark(BUFFER_SIZE);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            for (int i = 0; i < BUFFER_SIZE; ++i) {
                buf[i] = (byte) bis.read();
            }
            bis.reset();

            String input = new String(buf);
            if (input.contains("<rss") || input.contains("<RSS")) {
                return Optional.of(rssTransformer);
            } else if (input.contains("<feed") || input.contains("<FEED")) {
                return Optional.of(atomTransformer);
            } else {
                Log.e("Failed to identify format for: ", input);
            }
        } catch (IOException e) {
            Log.e(TransformFactory.class.getCanonicalName(), "Failed to peek at input.", e);
        }

        return Optional.absent();
    }
}
