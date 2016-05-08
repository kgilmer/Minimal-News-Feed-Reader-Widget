package com.abk.mrw.notificationui;

import com.abk.mrw.notificationui.model.SeenItem;

import org.junit.Test;

import static org.junit.Assert.*;

public class LastSeenTest {
    @Test
    public void testParseInput() throws Exception {
        final String validStr = "1234|http://boo.com";

        SeenItem si = new SeenItem(validStr);

        System.out.println(si);

        assertTrue(si.getCreateDate() == 1234);
        assertTrue(si.getUrl().equals("http://boo.com"));
    }
}