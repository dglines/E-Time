package edu.tacoma.uw.css.group7.e_time;

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

import java.text.SimpleDateFormat;


/**
 * TimerActivity handles the YouTube timer for the E-Time app.
 * Uses similar implementation to YouTube's demonstration apps located
 * on their GitHub: https://github.com/youtube/yt-android-player
 *
 * @author Alexander Reid
 * @version 5/18/2018
 */
public class TimerActivity extends YouTubeBaseActivity implements
        YouTubePlayer.OnInitializedListener,
        View.OnClickListener,
        PlayerStateChangeListener,
        PlaybackEventListener,
        PlaylistEventListener   {

    // Developer Key for YouTube API
    private static final String DEVELOPER_KEY = "AIzaSyC-DjQD5CVAU9ufKS1ZRah_0NYM40FSwu4";

    // Test Video IDs
    private static final String DEFAULT_VIDEO = "BJ0xBCwkg3E";  //Running in the 90s
    private static final String TESTING_VIDEO = "5xRCR3r4pGg";  //Music for testing

    // YouTube player object and view
    private YouTubePlayerView youTubePlayerView;
    private YouTubePlayer mPlayer;

    private TextView currentVideo;
    private Button playButton;
    private String currentVideoId;

    private Thread mTimer;
    private int mDuration;
    private long mTimeOfLastUpdate;
    private boolean isPaused;
    private String mSearchTerm;

    /**
     * Sets up the video player and timer
     * @param savedInstanceState - Saved instance data.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        // Getting views
        youTubePlayerView = findViewById(R.id.youtube_view);
        currentVideo = findViewById(R.id.current_video);
        playButton = findViewById(R.id.play_button);

        //checking for extras
        Bundle extra = getIntent().getBundleExtra("video");

        // Setting up first video
        if (extra != null)  {
            currentVideoId = extra.getString("vidId");
            mSearchTerm = extra.getString("searchTerm");
            mDuration = extra.getInt("duration");
        } else  {
            currentVideoId = getIntent().getStringExtra("vidId");
            mSearchTerm = getIntent().getStringExtra("searchTerm");
            mDuration = getIntent().getIntExtra("duration", 10000);
            if (currentVideoId == null) currentVideoId = DEFAULT_VIDEO;
        }

        if (currentVideoId == null) {
            currentVideoId = DEFAULT_VIDEO;
        }
        mTimeOfLastUpdate = System.currentTimeMillis();
        isPaused = true;
        // thread used to update clock display
        mTimer = new Thread()    {
            @Override
            public void run()   {
                try {
                    while(!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable()    {
                            @Override
                            public void run() {
                                long time = System.currentTimeMillis();
                                long tick = time - mTimeOfLastUpdate;
                                if (findViewById(R.id.digital_timer) != null && mPlayer.isPlaying()) {
                                    mDuration -= (tick);
                                    if (mDuration < 0)  {
                                        mDuration = 0;
                                        mPlayer.pause();
                                        playButton.setText(R.string.goBack);
                                    }
                                    ((TextView) findViewById(R.id.digital_timer)).setText(formatTime(mDuration));
                                }
                                mTimeOfLastUpdate = time;
                            }
                        });
                    }
                } catch (InterruptedException e)    {
                    //Thread interrupted before clock reset
                }
            }
        };
        mTimer.start();
        // For the play button
        playButton.setOnClickListener(this);
        // Initializing the YouTube player
        youTubePlayerView.initialize(DEVELOPER_KEY, this);

        // Enabling the button
        playButton.setEnabled(true);

    }

    @Override
    public void onDestroy() {
        mTimer.interrupt();
        super.onDestroy();
    }

    /**4
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
            playNextVideo();
        }
        // Enable the play button
        playButton.setEnabled(true);
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
    private void playNextVideo()    {
        mPlayer.loadVideo(currentVideoId);

    }


    /**
     * On click, the current video will either pause if playing, or play if paused.
     * @param v The button pressed.
     */
    @Override
    public void onClick(View v) {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
            playButton.setText(R.string.play);
        } else if (mDuration > 0)  {
            mPlayer.play();
            playButton.setText(R.string.pause);
        } else  {
            finish();
        }
    }

    /**
     * Updates the text at the top of the Activity to display current video.
     * Also logs the current video state.
     */
    private void updateText() {
        currentVideo.setText("Currently Playing: " + currentVideoId);
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
        currentVideoId = videoId;
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
        playNextVideo();
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
            currentVideo.setText("ERROR: Service Disconnected!");
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

}
