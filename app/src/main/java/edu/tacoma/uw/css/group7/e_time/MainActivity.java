package edu.tacoma.uw.css.group7.e_time;

import android.content.Intent;
import android.drm.DrmStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

public class MainActivity extends AppCompatActivity {

    // a reference to the drawer
    private DrawerLayout mDrawrLayout;

    // works as a listener for the drawer
    private ActionBarDrawerToggle mToggle;

    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //facebook
        // fb says to use this but idk why
        boolean loggedIn = AccessToken.getCurrentAccessToken() == null;

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

        // nav menu
        mDrawrLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawrLayout, 0, 0);
        mDrawrLayout.addDrawerListener(mToggle);

        // sysnState allows opening and closing the nav menu
        mToggle.syncState();
        // enable a back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // chechkif hamburger menu toggle selected
        return mToggle.onOptionsItemSelected(item);
    }


}
