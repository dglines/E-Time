
package edu.tacoma.uw.css.group7.e_time.authenticate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.tacoma.uw.css.group7.e_time.MainActivity;
import edu.tacoma.uw.css.group7.e_time.R;

/**
 * SignInActivity is used to contain the LoginFragment used to log users in.  Additionally, it will log
 * out the current user instead if there is already a user logged in.
 *
 * @author David Glines, Alexander Reid
 * @version 5/31/2018
 */
public class SignInActivity extends AppCompatActivity implements LoginFragment.LoginInteractionListener {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mSharedPreferences = getSharedPreferences(getString(R.string.LOGIN_PREFS)
                , Context.MODE_PRIVATE);
        // Of we are not logged in, start up the LoginFragment.
        if (!mSharedPreferences.getBoolean(getString(R.string.LOGGEDIN), false)) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.signin_fragment_container, new LoginFragment())
                    .commit();
        } else {
            // else, log out the current user and return to the MainActivity.
            mSharedPreferences.edit()
                    .putBoolean("loggedin", false)
                    .putString("username", "")
                    .commit();
            Intent i = new Intent(this, MainActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void login(String email, String pwd) {
        // Switch our logged in boolean in Shared Preferences to true and commit a username.
        mSharedPreferences.edit()
                .putBoolean(getString(R.string.LOGGEDIN), true)
                .putString("username", email)
                .commit();
        // Return to MainActivity.
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("userId", email);
        startActivity(intent);
        this.finish();
    }

}
