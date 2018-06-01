package edu.tacoma.uw.css.group7.e_time.video;

import com.google.api.client.util.Objects;

import org.json.JSONException;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class VideoTest {

    private Video testVid1 = new Video("123", "Testing", "1000", "500", "Test");

    @Test
    public void getVidId() {
        assertEquals("123", testVid1.getVidId());
    }

    @Test
    public void getTitle() {
        assertEquals("Testing", testVid1.getTitle());
    }

    @Test
    public void getLength() {
        assertEquals("1000", testVid1.getLength());
    }

    @Test
    public void getRemaining() {
        assertEquals("500", testVid1.getRemaining());
    }

    @Test
    public void getSearchTerm() {
        assertEquals("Test", testVid1.getSearchTerm());
    }

    @Test
    public void parseVideoJSON() {
        String mockJSONObject = "[{\"title\":\"Testing\",\"vidId\":\"123\"," +
                "\"length\":\"1000\",\"remaining\":\"500\",\"search\":\"Test\"},{\"title\":\"" +
                "How it feels to chew\",\"vidId\":\"iNFtH5gRXGc\",\"length\":\"3999730\"" +
                ",\"remaining\":\"3999000\",\"search\":\"five gum\"}]";
        try {
            List<Video> list = testVid1.parseVideoJSON(mockJSONObject);
            if (list == null || list.isEmpty()) {
                fail("list is null or empty");
            }
            Video vid = list.get(0);
            assertEquals(vid.getTitle(),testVid1.getTitle());
            assertEquals(vid.getVidId(),testVid1.getVidId());
            assertEquals(vid.getSearchTerm(),testVid1.getSearchTerm());
            assertEquals(vid.getRemaining(),testVid1.getRemaining());
            assertEquals(vid.getLength(),testVid1.getLength());
            Video vid2 = list.get(1);
            assertNotEquals(vid2.getTitle(),testVid1.getTitle());
            assertNotEquals(vid2.getVidId(),testVid1.getVidId());
            assertNotEquals(vid2.getSearchTerm(),testVid1.getSearchTerm());
            assertNotEquals(vid2.getRemaining(),testVid1.getRemaining());
            assertNotEquals(vid2.getLength(),testVid1.getLength());
        } catch (JSONException e)   {
            fail();
        } catch (NullPointerException e)    {
            fail("list is missing entries");
        }
    }

    @Test
    public void parseJSONFail() {
        try {
            testVid1.parseVideoJSON("fail");
            fail();
        } catch (JSONException e)   {
        }

    }
}