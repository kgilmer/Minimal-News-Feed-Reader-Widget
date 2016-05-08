package com.abk.mrw.model;

import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.base.Optional;

import java.io.BufferedInputStream;
import java.io.IOException;

import trikita.log.Log;

/**
 * Utility class to return a valid transformer based on the contents of the
 * data to be transformed.
 */
public class TransformFactory {

    //Number of bytes needed to determine message format.
    public static final int BUFFER_SIZE = 1024;

    //RSS transformer
    private static final XMLObjectIterable.Transformer<FeedEntry> rssTransformer = new RSSEntryTransformer();

    //Atom transformer
    private static final XMLObjectIterable.Transformer<FeedEntry> atomTransformer = new AtomEntryTransformer();

    /**
     * Inspect the input stream and optionally return a Transformer capable of parsing it into FeedEntries.
     * @param inputStream BufferedInputStream
     * @return optional transformer if format can be identified.
     */
    public static Optional<XMLObjectIterable.Transformer<FeedEntry>> getTransformer(BufferedInputStream inputStream) {
        inputStream.mark(BUFFER_SIZE);
        try {
            byte[] buf = new byte[BUFFER_SIZE];
            for (int i = 0; i < BUFFER_SIZE; ++i) {
                buf[i] = (byte) inputStream.read();
            }
            inputStream.reset();

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
