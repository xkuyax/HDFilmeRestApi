package me.xkuyax.hdfilme.rest.api;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RegexUtilsTest {

    @Test
    public void extractLast() throws Exception {
        String extract = RegexUtils.extractLast("StreetDance: New York 2015 (2016)", ".*([0-9]{4,}).*");
        assertEquals(extract, "2016");
    }

    @Test
    public void extract() throws Exception {
        String extract = RegexUtils.extract("StreetDance: New York (2016)", "\\([0-9]{1,}\\)");
        assertEquals(extract, "(2016)");
    }
}