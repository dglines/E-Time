package edu.tacoma.uw.css.group7.e_time;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import edu.tacoma.uw.css.group7.e_time.video.Video;

/**
 * This class serves as navigation by using the drawer view
 * and handles logun and swapping fragments in and out based on user activity.
 */
public class MainActivity extends AppCompatActivity implements RecentFragment.OnListFragmentInteractionListener,
                                                                MainFragment.OnFragmentInteractionListener,
                                                                newTimerFragment.OnFragmentInteractionListener {

    // a reference to the drawer
    private DrawerLayout mDrawerLayout;

    // works as a listener for the drawer
    private ActionBarDrawerToggle mToggle;

    private CallbackManager callbackManager;

    protected boolean mLoggedIn;

    protected String mUserId = "";


    /**
     * onCreate function is called for this class when the app is launched.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LoginButton loginButton = (LoginButton) findViewById(R.id.fblogin_button);

        // begin by launching home fragment
        MainFragment mainFragment = new MainFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content_frame, mainFragment)
                .commit();

        //facebook
        // fb says to use this but idk why
        mLoggedIn = AccessToken.getCurrentAccessToken() == null;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        mUserId = loginResult.getAccessToken().getUserId();
                        mLoggedIn = true;
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        mUserId = "";
                        mLoggedIn = false;
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        mUserId = "";
                        mLoggedIn = false;
                    }
                });

        // navigation menu
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);           // checked
        navView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                        // close drawer if item selected
                        mDrawerLayout.closeDrawers();

                        // we can add code here to swap FRAGMENTS, or maybe activities
                        // based on the item selected
                        // here's a Toast to demonstrate this:
                        CharSequence text = item.getTitle();
                        if (item.getItemId() == R.id.nav_recent_timers) {
                            RecentFragment recentsFragment = new RecentFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, recentsFragment)
                                    .commit();
                        } else if (item.getItemId() == R.id.log_In){
                            loginButton.performClick();
                        } else if (item.getItemId() == R.id.nav_home) {
                            MainFragment mainFragment = new MainFragment();
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .replace(R.id.content_frame, mainFragment)
                                    .commit();
                        } else if (item.getItemId() == R.id.nav_new_timer){
                            newTimerFragment newTimerFragment = new newTimerFragment();
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.content_frame, newTimerFragment)
                                    .commit();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        return true;
                    }
                });

        // allow switching between hamburger icon and back arrow icon
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, 0, 0);
        mDrawerLayout.addDrawerListener(mToggle);

        // sysnState allows opening and closing the nav menu
        mToggle.syncState();
        // enable a back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    /**
     *  implemented for facebook login functionality.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Enables users to make selections in the navigation drawer.
     * @param item the item that was selected.
     * @return a reference to the options item selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // check kif hamburger menu toggle selected
        return mToggle.onOptionsItemSelected(item);
    }

    /**
     * Alows users to make selections on a list fragment.
     * used for the recent timers fragment.  Will be used in the future
     * for the favorites fragment as well.
     * @param item a reference to the item that was selected.
     */
    @Override
    public void onListFragmentInteraction(Video item) {
        // simulate passing information to timer activity or fragment
        Toast toast = Toast.makeText(getApplicationContext(), item.getVidid(), Toast.LENGTH_LONG);
        toast.show();
    }


    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that activity.
    */
    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void setTimer(String lenTitle, int length, String termTitle, String searchTerm) {
        Intent intent = new Intent(this, TimerActivity.class);
        intent.putExtra(lenTitle, length);
        intent.putExtra(termTitle, searchTerm);
        startActivity(intent);


    }

    public void newTimer(View view)   {
        Intent intent = new Intent(this, TimerActivity.class);
        startActivity(intent);

    }
}
