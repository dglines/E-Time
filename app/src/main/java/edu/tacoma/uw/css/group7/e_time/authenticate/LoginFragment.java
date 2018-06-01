package edu.tacoma.uw.css.group7.e_time.authenticate;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.css.group7.e_time.MainActivity;
import edu.tacoma.uw.css.group7.e_time.R;
import edu.tacoma.uw.css.group7.e_time.data.RecentDB;
import edu.tacoma.uw.css.group7.e_time.video.Video;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * LoginFragment is a fragment used with SigninActivity.  It handles receiving login information
 * from the user and communicating with the online database for authentication.
 *
 * @author Parker Olive, David Glines
 * @version 5/31/2018
 */
public class LoginFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String DB_URL = "https://olivep3.000webhostapp.com/Android/Login.php?";
    private static final String RECENTSDB_URL = "http://olivep3.000webhostapp.com/Android/recentList.php?cmd=recents";

    private String mParam1;
    private String mParam2;
    protected EditText emailText;
    protected EditText pwdText;
    
    private String username;
    private String pwd;

    private Button signInButton;

    private LoginInteractionListener mListener;
    private RecentDB mRecentDB;
    private Context mContext;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        getActivity().setTitle("Login");
        mContext = v.getContext();

        emailText = (EditText) v.findViewById(R.id.edit_email);
        pwdText = (EditText) v.findViewById(R.id.edit_pwd);

        signInButton = (Button) v.findViewById(R.id.btn_signin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = emailText.getText().toString();
                pwd = pwdText.getText().toString();
                if (TextUtils.isEmpty(username)) {
                    Toast.makeText(v.getContext(), "Enter valid username"
                            , Toast.LENGTH_SHORT)
                            .show();
                    emailText.requestFocus();
                }

                else if (TextUtils.isEmpty(pwd) || pwd.length() < 6) {
                    Toast.makeText(v.getContext(), "Enter valid password (at least 6 characters)"
                            , Toast.LENGTH_SHORT)
                            .show();
                    pwdText.requestFocus();
                }
                else {
                    String url = buildURL(username, pwd);
                    AuthenticateTask task = new AuthenticateTask();
                    task.execute(new String[]{url.toString()});


                    String recentURL = buildRecentURL(username);
                    UpdateDBTask nextTask = new UpdateDBTask();
                    nextTask.execute(new String[]{recentURL.toString()});
                    //mListener.login(username, pwd);

                }
            }
        });
        return v;
    }

    /**
     * Builds a URL String using the provided username and password.
     * @param username - Username of the user
     * @param pwd - Password
     * @return String url
     */
    private String buildURL(String username, String pwd){
        StringBuilder sb = new StringBuilder(DB_URL);
        sb.append("userId=" + username);
        sb.append("&pass=" + pwd);
        return sb.toString();
    }

    /**
     * Builds a URL String used to receive recent video information related to the user.
     * @param userID - Username
     * @return String url
     */
    private String buildRecentURL(String userID){
        StringBuilder sb = new StringBuilder(RECENTSDB_URL);
        sb.append("&userId=" + userID);
        //Toast.makeText(getActivity(), sb.toString(), Toast.LENGTH_LONG);
        Log.e("Recent", sb.toString());
        return sb.toString();
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof LoginInteractionListener) {
            mListener = (LoginInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface LoginInteractionListener {
        void login(String email, String pwd);

    }

    /**
     * this class will update a local database
     * with recent timer information based on user login credentials.
     */
    private class UpdateDBTask extends AsyncTask<String, Void, String> {
        private List<Video> mRecentList;

        /**
         * required for AsyncTask.  pulls list of recent timers from web service.
         * @param urls
         * @return
         */
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

                } catch(Exception e) {
                    response = "Unable to download the list of recents, Reason:" + e.getMessage();
                }
                finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Runs on the UI thread after {@link #doInBackground}.
         * @param result the value returned by {@link #doInBackground}.
         */
        @Override
        protected void onPostExecute(String result) {
            // Log.i(TAG, "onPostExecute");



            if (result.startsWith("Unable to")) {
//                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
//                        .show();
                return;
            }
            try {
                mRecentList = Video.parseVideoJSON(result);
            }
            catch (JSONException e) {
//                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG)
//                        .show();
                return;
            }

// Everything is good, show the list of courses.

                if (!mRecentList.isEmpty()) {
                    if (mRecentDB == null) {
                        mRecentDB = new RecentDB(mContext);
                    }
                    //Delete old data so that you can refresh the local db w/ network data.
                    mRecentDB.deleteRecents();  //#### this caused problems with table not existing
                    //also, add to local db
                    Log.e("These guys", mRecentList.toString());
                    for (int i = 0; i < mRecentList.size(); i++) {
                        Video video = mRecentList.get(i);
                        String vidTitle = video.getTitle();
                        if (vidTitle != null && vidTitle.length() > 20)
                            vidTitle = vidTitle.substring(0, 17) + "...";
                        if (!mRecentDB.insertRecent(video.getVidId(), vidTitle,
                                video.getLength(), video.getRemaining())) {
                            Log.e("Tried to use insert.", "IT WAS NOT VERY EFFECTIVE!");
                        }
                    }
                    //  mRecyclerView.setAdapter(new MyVideoRecyclerViewAdapter(mRecentsList, mListener));
                }

        }
    }

    /**
     * The AuthenticateTask is an AsyncTask used to authenticate the user information using an
     * online database.
     */
    private class AuthenticateTask extends AsyncTask<String, Void, String> {


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
                    response = "Unable to add course, Reason: "
                            + e.getMessage();
                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /** It checks to see if there was a problem with the URL(Network) which is when an
         * exception is caught. It tries to call the parse Method and checks to see if it was successful.
         *  If not, it displays the exception.
         *
         * @param result
         */
        @Override
        protected void onPostExecute(String result) {
            // Something wrong with the network or the URL.
            try {
                JSONObject jsonObject = new JSONObject(result);
                String status = (String) jsonObject.get("result");
                if (status.equals("logged in")) {
                    Toast.makeText(getApplicationContext(), "Login successful"
                            , Toast.LENGTH_LONG)
                            .show();
                    mListener.login(username, pwd);
//                    NavigationView navView = getActivity().findViewById(R.id.nav_view);
//                    navView.getMenu().getItem(4).setTitle("Log out");
                } else if (status.equals("registered")) {
                    Toast.makeText(getApplicationContext(), "Please Create Account"
                            , Toast.LENGTH_LONG)
                            .show();
                    signInButton.setText("Register");
                    //mListener.login(username, pwd);
                } else if (status.equals("incorrect password")) {
                    Toast.makeText(getApplicationContext(), "Incorrect password"
                            , Toast.LENGTH_LONG)
                            .show();

                } else {
                    Toast.makeText(getApplicationContext(), "Failed to login "
                                    + jsonObject.get("error")
                            , Toast.LENGTH_LONG)
                            .show();
                }
            } catch (JSONException e) {
                Toast.makeText(getApplicationContext(), "Something wrong with the data" +
                        e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }


}

