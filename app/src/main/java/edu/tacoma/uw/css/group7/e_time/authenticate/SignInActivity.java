package edu.tacoma.uw.css.group7.e_time.authenticate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import edu.tacoma.uw.css.group7.e_time.MainActivity;
import edu.tacoma.uw.css.group7.e_time.R;

public class SignInActivity extends AppCompatActivity implements LoginFragment.LoginInteractionListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.signin_fragment_container, new LoginFragment() )
                .commit();

    }

    @Override
    public void login(String email, String pwd) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("userId", email);
        startActivity(intent);

        this.finish();
    }

}
