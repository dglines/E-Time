package edu.tacoma.uw.css.group7.e_time.video;

import org.junit.Test;

import static org.junit.Assert.*;

public class VideoTest {

    private Video testVid1 = new Video("123", "Testing", "1000", "500", "Test");

    @Test
    public void getVidId() {
        assertEquals("123", testVid1.getVidId());
    }

    @Test
    public void getTitle() {
    }

    @Test
    public void getLength() {
    }

    @Test
    public void getRemaining() {
    }

    @Test
    public void getSearchTerm() {
    }

    @Test
    public void parseVideoJSON() {
    }
}