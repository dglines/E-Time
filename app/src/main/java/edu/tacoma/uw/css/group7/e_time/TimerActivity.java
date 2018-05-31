package edu.tacoma.uw.css.group7.e_time;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayer.ErrorReason;
import com.google.android.youtube.player.YouTubePlayer.PlaybackEventListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStateChangeListener;
import com.google.android.youtube.player.YouTubePlayer.PlayerStyle;
import com.google.android.youtube.player.YouTubePlayer.PlaylistEventListener;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import edu.tacoma.uw.css.group7.e_time.data.RecentDB;

/**
 * TimerActivity handles the YouTube timer for the E-Time app.
 * Uses similar implementation to YouTube's demonstration apps located
 * on their GitHub: https://github.com/youtube/yt-android-player
 *
 * @author Alexander Reid
 * @version 5/29/2018
 */
public class TimerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener,
        View.OnClickListener,
        PlayerStateChangeListener,
        PlaybackEventListener,
        PlaylistEventListener   {

    // Developer Key for YouTube API
    private static final String DEVELOPER_KEY = "AIzaSyC-DjQD5CVAU9ufKS1ZRah_0NYM40FSwu4";

    // Default video ID if something breaks
    private static final String ERROR_VID = "oHg5SJYRHA0";

    // Number of videos requested at a time
    private static final long NUMBER_OF_VIDEOS_RETURNED = 10;

    // Base URL for communicating with the database
    private static final String BASE_URL = "http://olivep3.000webhostapp.com/Android/"; // addRecent.php?userId=<userid>&vidId=<vidid>&length=<length>&remaining=<remaining(float)>

    // Youtube class variable
    private static YouTube youtube;

    // YouTube player object and view
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer mPlayer;

    // Other views
    private TextView mCurrentVideo;
    private Button mBtnFavorite;
    private Button mBtnPlay;

    // Necessary fields
    private Iterator<SearchResult> mIterator;
    private Thread mTimer;
    private String mCurrentVideoId;
    private String mCurrentVideoTitle;
    private String mNextPage;
    private String mSearchTerm;
    private String mUserId;
    private long mTimeOfLastUpdate;
    private int mCurrentTime;
    private int mDuration;
    private int mStartingSpot;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Getting views
        findViews();

        // Initial setup
        initialize();

        startTimer();
        // For the play button
        mBtnPlay.setOnClickListener(this);
        findViewById(R.id.btn_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBtnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(that, "Unimplemented", Toast.LENGTH_LONG).show();
                if (!mUserId.equals("")) {


                    AddFavoriteTask task = new AddFavoriteTask();
                    String favsURL = BASE_URL + "addFavorite.php?userId=" + mUserId
                            + "&title=" + mCurrentVideoTitle
                            + "&search=" + mSearchTerm
                            + "&vidId=" + mCurrentVideoId
                            + "&length=" + mDuration;
                    task.execute(new String[]{favsURL});

                }
            }
        });
        // Initializing the YouTube player
        youTubePlayerView.initialize(DEVELOPER_KEY, this);
        // Enabling the play button
        mBtnPlay.setEnabled(true);
    }

    @Override
    public void onDestroy() {
        mTimer.interrupt();
        super.onDestroy();
    }

    /**
     * Assigns all the view fields to their respective views.
     */
    public void findViews() {
        youTubePlayerView = findViewById(R.id.youtube_view);
        mCurrentVideo = findViewById(R.id.current_video);
        mBtnPlay = findViewById(R.id.play_button);
        mBtnFavorite = findViewById(R.id.btn_favorite);
    }

    /**
     * Sets up the initial states for the fields and launches the search async task
     */
    public void initialize() {
        Bundle extra = getIntent().getBundleExtra("video");

        if (extra != null)  { //Bundle extra exists, get data and store in proper fields.
            mCurrentVideoId = extra.getString("vidId");
            mCurrentVideoTitle = extra.getString("vidTitle");
            mCurrentTime = extra.getInt("remainingTime");
            mSearchTerm = extra.getString("searchTerm");
            mDuration = extra.getInt("length");
            mUserId = extra.getString("userId");
        } else  { // Bundle was not used.
            mCurrentVideoId = getIntent().getStringExtra("vidId");
            mCurrentVideoTitle = getIntent().getStringExtra("vidTitle");
            mSearchTerm = getIntent().getStringExtra("searchTerm");
            mDuration = getIntent().getIntExtra("length", 10000);
            mCurrentTime = getIntent().getIntExtra("remainingTime", mDuration);
            mUserId = getIntent().getStringExtra("userId");
        }

        if (mCurrentTime > mDuration)   {
            mCurrentTime = mDuration;
        }
        mStartingSpot = mDuration-mCurrentTime;

        if (mCurrentVideoId == null) {
            mCurrentVideoId = ERROR_VID;
            mCurrentVideoTitle = "Testing!";
        }
        if (mSearchTerm == null || mSearchTerm.length() == 0)   {
            mSearchTerm = mCurrentVideoId;
        }
        if (mUserId == null)    {
            mUserId = "";
        }
        if (mUserId.length() == 0)  {
            mBtnFavorite.setEnabled(false);
        }

        //Bad code that waits for the search async task to finish
        try {
            Object result = new SearchTask().execute().get();
        } catch (Exception e)   {
            // REEEEE
        }
    }

    /**
     * Starts a thread that updates the timer every 500 milliseconds.
     */
    public void startTimer()    {

        mTimeOfLastUpdate = System.currentTimeMillis();
        // thread used to update clock display
        mTimer = new Thread()    {
            @Override
            public void run()   {
                try {
                    while(!isInterrupted()) {
                        runOnUiThread(new Runnable()    {
                            @Override
                            public void run() {
                                long time = System.currentTimeMillis();
                                long tick = time - mTimeOfLastUpdate;
                                if (findViewById(R.id.digital_timer) != null && mPlayer != null && mPlayer.isPlaying()) {
                                    mCurrentTime -= (tick);
                                    if (mCurrentTime < 0)  {
                                        mCurrentTime = 0;
                                        mPlayer.pause();
                                        mBtnPlay.setEnabled(false);
                                    }
                                    ((TextView) findViewById(R.id.digital_timer)).setText(formatTime(mCurrentTime));
                                }
                                mTimeOfLastUpdate = time;
                            }
                        });
                        Thread.sleep(500);
                    }
                } catch (InterruptedException e)    {
                    //Thread interrupted before clock reset
                }
            }
        };
        mTimer.start();

        // For the play button
        mBtnPlay.setOnClickListener(this);
        findViewById(R.id.btn_favorite).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        findViewById(R.id.btn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // Initializing the YouTube player
        youTubePlayerView.initialize(DEVELOPER_KEY, this);
        // Enabling the button
        mBtnPlay.setEnabled(true);
    }

    /**
     * On success, the YouTube player is configured and listeners are set.
     * @param provider The YouTube Provider
     * @param player The YouTubePlayer inside the YouTubeView
     * @param wasRestored A boolean
     */
    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer player,
                                        boolean wasRestored) {
        this.mPlayer = player;
        player.setPlayerStyle(PlayerStyle.CHROMELESS);
        player.setPlaylistEventListener(this);
        player.setPlayerStateChangeListener(this);
        player.setPlaybackEventListener(this);

        if (!wasRestored) {
            log("Restored!");
            playNextVideo(1);
        }
        // Enable the play button
        mBtnPlay.setEnabled(true);
    }

    /**
     * The method that's called when the YouTubePlayer fails to initialize.
     * @param provider - The YouTube Provider
     * @param youTubeInitializationResult - The failure reason
     */
    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
        if (youTubeInitializationResult.isUserRecoverableError()) {
            youTubeInitializationResult.getErrorDialog(this, 1).show();
        } else {
            String errorMessage = String.format(getString(R.string.error_player), youTubeInitializationResult.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Updates the YouTube player with the next video and plays it.
     */
    private void playNextVideo(int i)    {
        if (i == 1)
            mPlayer.loadVideo(mCurrentVideoId, mStartingSpot);
        else
            mPlayer.loadVideo(mCurrentVideoId);

    }


    /**
     * On click, the current video will either pause if playing, or play if paused.
     * @param v The button pressed.
     */
    @Override
    public void onClick(View v) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            mBtnPlay.setText(R.string.play);
        } else {
            mPlayer.play();
            mBtnPlay.setText(R.string.pause);
        }
    }

    @Override
    public void onStop()    {
        addRecent();
        super.onStop();
    }

    /**
     * Updates the text at the top of the Activity to display current video.
     * Also logs the current video state.
     */
    private void updateText() {
        mCurrentVideo.setText(String.format(getString(R.string.currently_playing), mCurrentVideoTitle));
        Log.v("Video State:", String.format("Current state: %s %s",
                playerState, playbackState));
    }

    /**
     * Logs info that is passed as a param.
     * @param message - message to be logged.
     */
    private void log(String message) {
        Log.v("Player Info: ", message);
    }

    /**
     * Formats time from milliseconds to minutes and seconds. Used for logging video information
     * @param millis - Time in milliseconds
     * @return String containing the time (MM:SS)
     */
    private String formatTime(int millis) {
        int seconds = millis / 1000;
        int minutes = seconds / 60;
        int hours = minutes / 60;

        return (hours == 0 ? "" : hours + ":")
                + String.format("%02d:%02d", minutes % 60, seconds % 60);
    }

    /**
     * Formats and returns a String that shows the current time of the video and its total time.
     * @return String containing current video time and duration.
     */
    private String getTimesText() {
        int currentTimeMillis = mPlayer.getCurrentTimeMillis();
        int durationMillis = mPlayer.getDurationMillis();
        return String.format("(%s/%s)", formatTime(currentTimeMillis), formatTime(durationMillis));
    }

    //***************************************//
    // START PLAYBACK EVENT LISTENER METHODS //
    //***************************************//

    // String used for displaying the playback state
    String playbackState = "Not Running";

    /**
     * Logs when a video is playing
     */
    @Override
    public void onPlaying() {
        playbackState = "Playing.";
        updateText();
        log(playbackState+ " " + getTimesText());
    }

    /**
     * Unimplemented
     * @param isBuffering
     */
    @Override
    public void onBuffering(boolean isBuffering) {
    }

    /**
     * Logs when the video is completely stopped.
     */
    @Override
    public void onStopped() {
        playbackState = "Stopped.";
        updateText();
        log(playbackState);
    }

    /**
     * Logs when the video is paused.
     */
    @Override
    public void onPaused() {
        playbackState = "Paused.";
        updateText();
        log(playbackState + " " + getTimesText());
    }

    /**
     * Not implemented.
     * @param endPositionMillis
     */
    @Override
    public void onSeekTo(int endPositionMillis) {
    }
    //***END PLAYBACK LISTENERS***//

    //*************************************//
    // START PLAYER STATE LISTENER METHODS //
    //*************************************//

    // String used to display the player state
    String playerState = "Uninitialized";

    /**
     * Logs when a video is loading.
     */
    @Override
    public void onLoading() {
        playerState = "Loading.";
        updateText();
        log(playerState);
    }

    /**
     * Logs when a video is fully loaded. Updates the text at the top of the Activity to display
     * the current video's id.
     * @param videoId
     */
    @Override
    public void onLoaded(String videoId) {
        mCurrentVideoId = videoId;
        playerState = String.format("Loaded: %s", videoId);
        updateText();
        log(playerState);
    }

    /**
     * Logs when an ad starts...
     */
    @Override
    public void onAdStarted() {
        playerState = "ADS!! AAAAAH";
        updateText();
        log(playerState);
    }

    /**
     * Logs when a video starts.
     */
    @Override
    public void onVideoStarted() {
        playerState = "Video Started.";
        updateText();
        log(playerState);
    }

    /**
     * Logs and plays the next video when a video ends.
     */
    @Override
    public void onVideoEnded() {
        playerState = "Video Ended.";
        try {
            Object result = new SearchTask().execute().get();
        } catch (Exception e){

        }
        playNextVideo(0);
        updateText();
        log(playerState);
    }

    /**
     * A method that handles any errors regarding the YouTube Player.
     * @param reason - Error code
     */
    @Override
    public void onError(ErrorReason reason) {
        playerState = "Error : (" + reason + ")";
        if (reason == ErrorReason.UNEXPECTED_SERVICE_DISCONNECTION) {
            mPlayer = null;
            mCurrentVideo.setText("ERROR: Service Disconnected!");
        }
        updateText();
        log(playerState);
    }
    //***END STATE LISTENERS***//

    //***************************************//
    // START PLAYLIST EVENT LISTENER METHODS //
    //***************************************//

    /**
     * Logs when the next video is called.
     */
    @Override
    public void onNext() {
        log("Next video.");
    }
    /**
     * Logs when the previous video is called.
     */
    @Override
    public void onPrevious() {
        log("Previous video.");
    }
    /**
     * Logs when the playlist ends.
     */
    @Override
    public void onPlaylistEnded() {
        log("Playlist done.");
    }

    //***END PLAYLIST LISTENERS***//

    /**
     * Adds the current timer to the recent timer list using an AsyncTask
     */
    private void addRecent()    {
        //if (!mUserId.equals("")) {
            AddRecentTask task = new AddRecentTask();
            String url = urlBuilder();
            Log.e("databaseURL",url);
            task.execute(new String[]{url.toString()});
//        } else  {
//            Log.e("TimerActivity", "Bad user ID: \" " + mUserId.toString() + "\"");
//        }

    }

    /**
     * Creates a url that is used to communicate to our database.
     * @return String - Contains the url
     */
    private String urlBuilder() { //addRecent.php?userId=<userid>&vidId=<vidid>&length=<length>&remaining=<remaining(float)>
        int remainingTime = mCurrentTime;
        int newDuration = remainingTime + mPlayer.getCurrentTimeMillis();
        String url = BASE_URL + "addRecent.php?"
                + "userId=" + mUserId
                + "&vidId=" + mCurrentVideoId
                + "&title=" + mCurrentVideoTitle
                + "&length=" + newDuration
                + "&remaining=" + remainingTime
                + "&search=" + mSearchTerm;

        RecentDB recentDB = RecentFragment.getRecentDB();
        if (recentDB == null)   {
            recentDB = new RecentFragment().getRecentDB();
        }
        String duration = "" + newDuration;
        String currTime = "" + mCurrentTime;
        String vidTitle = mCurrentVideoTitle;
        if (vidTitle.length() > 20)
            vidTitle = vidTitle.substring(0,17) + "...";
        recentDB.insertRecent(mCurrentVideoId, vidTitle, duration, currTime);

        return url;
    }

    /**
     * An AsyncTask that pulls a list of YouTube videos using the Google API.  The results are
     * stored in an iterator.
     */
    private class SearchTask extends AsyncTask<Void, Void, Boolean> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void...voids) {
            if (mSearchTerm != null && mSearchTerm.length() > 0) {
                SearchResult sr = null;
                if (mIterator == null || !mIterator.hasNext()) {
                    try {
                        youtube = new YouTube.Builder(new NetHttpTransport(),
                                new JacksonFactory(),
                                new HttpRequestInitializer() {
                                    @Override
                                    public void initialize(HttpRequest httpRequest) throws IOException {
                                    }
                                }).setApplicationName("E-Time").build();
                        YouTube.Search.List search = youtube.search().list("id,snippet");
                        search.setKey(DEVELOPER_KEY);
                        search.setQ(mSearchTerm);
                        search.setType("video");
                        search.setFields("items(id/kind,id/videoId,snippet/title,snippet/thumbnails/default/url)");
                        search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
                        if (mNextPage != null && mNextPage.length() > 0)
                            search.setPageToken(mNextPage);
                        SearchListResponse resp = search.execute();
                        mNextPage = resp.getNextPageToken();
                        List<SearchResult> results = resp.getItems();
                        if (results != null) {
                            mIterator = results.iterator();
                            boolean videoFound = false;
                            if (mIterator.hasNext()) {
                                sr = mIterator.next();
                                mCurrentVideoId = sr.getId().getVideoId();
                                mCurrentVideoTitle = sr.getSnippet().getTitle();
                            }
                        } else {
                            mCurrentVideoId = ERROR_VID;
                        }
                    } catch (Exception e) {
                        Log.e("YouTubeBuilder", "FAILURE!");
                        e.printStackTrace();
                        return false;
                    }
                } else  {
                    sr = mIterator.next();
                    mCurrentVideoId = sr.getId().getVideoId();
                    mCurrentVideoTitle = sr.getSnippet().getTitle();
                }
            } else  {
                mCurrentVideoId = ERROR_VID;
            }
            return true;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(Boolean result) {
            //Unimplemented.
        }
    }

    /**
     * An AsyncTask that adds the timer to the recents list.
     */
    private class AddRecentTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add recent entry, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }

            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            //Nothing to see here.
        }
    }
    private final Activity that = this;

    /**
     * An AsyncTask that is used to add a timer to the favorite menu.
     */
    private class AddFavoriteTask extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            String response = "";
            HttpURLConnection urlConnection = null;
            for (String url : urls) {
                try {
                    URL urlObject = new URL(url);
                    urlConnection = (HttpURLConnection) urlObject.openConnection();

                    InputStream content = urlConnection.getInputStream();

                    BufferedReader buffer = new BufferedReader(new InputStreamReader(content));
                    String s = "";
                    while ((s = buffer.readLine()) != null) {
                        response += s;
                    }

                } catch (Exception e) {
                    response = "Unable to add recent entry, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }

            }
            return response;
        }


        /**
         * It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         * If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            //Nothing to see here.
            Toast.makeText(that,"Added to Favorites", Toast.LENGTH_LONG).show();
        }
    }
}
