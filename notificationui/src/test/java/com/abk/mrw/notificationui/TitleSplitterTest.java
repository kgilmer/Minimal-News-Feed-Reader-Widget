package com.abk.mrw.notificationui;

import com.google.common.base.Splitter;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by kgilmer on 5/3/16.
 */
public class TitleSplitterTest {

    private final static String [] TEST_TITLES = {
            "Uvloop: Blazing fast Python networking",
            "WhatsApp is back online in Brazil",
            "The price of solar power just fell 50% in 16 months",
            "21 Makes Bitcoin Useful to Developers",
            "Preparing for the Future of Artificial Intelligence",
            "Stack Overflow: How We Do Deployment",
            "Introducing the LEDE project – A reboot of the OpenWrt community",
            "Ruby Bug: SecureRandom should try /dev/urandom first",
            "Waybackpack: download the entire Wayback Machine archive for a given URL",
            "Engineer? Swiftype is hiring",
            "Scientists say sudden oak death epidemic is no longer stoppable",
            "Remote code execution vulnerability in ImageMagick",
            "AdBlock Plus teams up with Flattr to help readers pay publishers",
            "Improving Angular performance with 1 line of code",
            "My path to OpenAI",
            "Calysto: A Scheme kernel for Jupyter that can use Python libraries",
            "PieMessage: iMessage on Android",
            "Google Deactivates Web Search API",
            "Deep Language Modeling for Question Answering Using Keras",
            "React Native bridge core moving to C++",
            "The cryptographically provable con man"
    };

    @Test
    public void testSplitTitles() throws Exception {
        for (String title : TEST_TITLES) {
            String[] ft = NewsFeedService.formatTitle(title);
            assertNotNull(ft);
            assertTrue(ft.length == 2);

            System.out.println(ft[0] + " - " + ft[1]);
        }

    }


}
