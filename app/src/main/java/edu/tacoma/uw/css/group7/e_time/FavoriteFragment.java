package edu.tacoma.uw.css.group7.e_time;

import android.content.Context;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import edu.tacoma.uw.css.group7.e_time.data.RecentDB;
import edu.tacoma.uw.css.group7.e_time.video.Video;

import org.json.JSONException;


/**
 * A fragment representing a list of recent video timers.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class FavoriteFragment extends Fragment {


    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String RECENT_URL = "http://olivep3.000webhostapp.com/Android/recentList.php?cmd=recents&userId=test";
    private static final String BASE_URL = "http://olivep3.000webhostapp.com/Android/recentList.php?cmd=favorites&userId=";

    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private List<Video> mFavoritesList;
    private RecyclerView mRecyclerView;
    //private static RecentDB mRecentDB;


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public FavoriteFragment() {
    }

    public static FavoriteFragment newInstance(int columnCount) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Called to do initial creation of a fragment.
     *
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // code to pull recents list from web service
            FavoriteAsyncTask favoriteAsyncTask = new FavoriteAsyncTask();
            favoriteAsyncTask.execute(new String[]{BASE_URL + ((MainActivity) getActivity()).getUserId()});

//            if (mRecentDB == null) {
//                mRecentDB = new RecentDB(getActivity());
//            }
//            if (mFavoritesList == null) {
//                mFavoritesList = mRecentDB.getRecents();
//            }


            //Log.e("THIS GUY", mFavoritesList.toString());
            mRecyclerView.setAdapter(new FavoritesRecyclerView(mFavoritesList, mListener));
            // meneka says to remove this

            //Toast.makeText(context,BASE_URL + ((MainActivity)getActivity()).getUserId(), Toast.LENGTH_LONG).show();
        }
        return view;
    }


    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(Video item);
    }

    /**
     * This class allows communication between the fragment and the web service.
     */
    private class FavoriteAsyncTask extends AsyncTask<String, Void, String> {

        /**
         * required for AsyncTask.  pulls list of recent timers from web serviec.
         *
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

                } catch (Exception e) {
                    response = "Unable to download the list of favorites, Reason:" + e.getMessage();

                } finally {
                    if (urlConnection != null)
                        urlConnection.disconnect();
                }
            }
            return response;
        }

        /**
         * Runs on the UI thread after {@link #doInBackground}.
         *
         * @param result the value returned by {@link #doInBackground}.
         */
        @Override
        protected void onPostExecute(String result) {
            // Log.i(TAG, "onPostExecute");

            if (result.startsWith("{\"result")) {
//                Toast.makeText(getActivity().getApplicationContext(), result, Toast.LENGTH_LONG)
//                        .show();
                return;
            }

            try {
                mFavoritesList = Video.parseVideoJSON(result);
            } catch (JSONException e) {
//                Toast.makeText(getActivity().getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT)
//                        .show();
                return;
            }

// Everything is good, show the list of courses.
            if (!mFavoritesList.isEmpty()) {
                mRecyclerView.setAdapter(new FavoritesRecyclerView(mFavoritesList, mListener));
            }
        }
    }
}