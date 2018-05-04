package edu.tacoma.uw.css.group7.e_time;

import android.drm.DrmStore;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    // a reference to the drawer
    private DrawerLayout mDrawrLayout;

    // works as a listener for the drawer
    private ActionBarDrawerToggle mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDrawrLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mToggle = new ActionBarDrawerToggle(this, mDrawrLayout, 0, 0);
        mDrawrLayout.addDrawerListener(mToggle);

        // sysnState allows opening and closing the nav menu
        mToggle.syncState();
        // enable a back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // chechkif hamburger menu toggle selected
        return mToggle.onOptionsItemSelected(item);
    }


}
