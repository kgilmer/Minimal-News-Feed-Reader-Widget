package com.abk.mrw.common;

import com.abk.mrw.model.AtomEntryTransformer;
import com.abk.mrw.model.FeedEntry;
import com.abk.mrw.model.RSSEntryTransformer;
import com.abk.mrw.model.TransformFactory;
import com.abk.xmlobjectiterable.XMLObjectIterable;
import com.google.common.base.Optional;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;

import static org.junit.Assert.*;

/**
 * Tests for TransformFactory
 */
public class TransformFactoryTest {

    @Test
    public void testIdentifyRSSFormat() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("rss_sample.xml");

        Optional<XMLObjectIterable.Transformer<FeedEntry>> transformerOpt = TransformFactory.getTransformer(new BufferedInputStream(resource.openStream()));

        assertNotNull("TransformFactory returns non-null value.", transformerOpt);
        assertTrue("TransformOpt contains value.", transformerOpt.isPresent());
        assertTrue("Transform is of RSS type.", transformerOpt.get() instanceof RSSEntryTransformer);
    }

    @Test
    public void testIdentifyAtomFormat() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("atom_sample.xml");

        Optional<XMLObjectIterable.Transformer<FeedEntry>> transformerOpt = TransformFactory.getTransformer(new BufferedInputStream(resource.openStream()));

        assertNotNull("TransformFactory returns non-null value.", transformerOpt);
        assertTrue("TransformOpt contains value.", transformerOpt.isPresent());
        assertTrue("Transform is of RSS type.", transformerOpt.get() instanceof AtomEntryTransformer);
    }

    @Test
    public void testEmptyInput() throws Exception {
        Optional<XMLObjectIterable.Transformer<FeedEntry>> transformerOpt = TransformFactory.getTransformer(new BufferedInputStream(new ByteArrayInputStream("".getBytes())));

        assertNotNull("TransformFactory returns non-null value.", transformerOpt);
        assertTrue("TransformOpt contains no value.", !transformerOpt.isPresent());
    }

    @Test
    public void testInvalidFormat() throws Exception {
        URL resource = this.getClass().getClassLoader().getResource("books.xml");

        Optional<XMLObjectIterable.Transformer<FeedEntry>> transformerOpt = TransformFactory.getTransformer(new BufferedInputStream(resource.openStream()));

        assertNotNull("TransformFactory returns non-null value.", transformerOpt);
        assertTrue("TransformOpt contains no value.", !transformerOpt.isPresent());
    }
}