package edu.tacoma.uw.css.group7.e_time.video;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Video implements Serializable {
    // constants
    public static final String VIDID = "vidId";
    public static final String LENGTH = "length";
    public static final String REMAINING = "remaining";
    public static final String SEARCH = "search";
    public static final String TITLE = "title";

    // member variables
    private String mVidId, mTitle, mLength, mRemaining, mSearchTerm;


    /**
     * Constructs a video Object.
     * @param vidId the ID of the video.
     * @param length the length of the video.
     * @param remaining the time remaining in the video.
     * @param title the title of the video.
     */

    public Video(String vidId, String title, String length, String remaining, String searchTerm) {

        mVidId = vidId;
        mTitle = title;
        mLength = length;
        mRemaining = remaining;
        mSearchTerm = searchTerm;

    }

    public String getVidId() {
        return mVidId;
    }

    public String getTitle() { return mTitle; }

    public String getLength() {
        return mLength;
    }

    public String getRemaining() {
        return mRemaining;
    }

    public String getSearchTerm() { return mSearchTerm; }

    public static List<Video> parseVideoJSON(String videoJSON) throws JSONException {
        List<Video> videoList = new ArrayList<Video>();
        if (videoJSON != null) {

            JSONArray arr = new JSONArray(videoJSON);

            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                Video video = new Video(obj.getString(Video.VIDID), obj.getString(Video.TITLE), obj.getString(Video.LENGTH)
                        , obj.getString(Video.REMAINING), obj.getString(Video.SEARCH));

                videoList.add(video);
            }

        }

        return videoList;
    }


}
