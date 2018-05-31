package edu.tacoma.uw.css.group7.e_time;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link newTimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link newTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class newTimerFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String SEARCH_TERM = "searchTerm";
    private static final String LENGTH = "length";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText mEditDuration;
    private EditText mEditSearchTerm;


    public newTimerFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment newTimerFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static newTimerFragment newInstance(String param1, String param2) {
        newTimerFragment fragment = new newTimerFragment();
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
        View v = inflater.inflate(R.layout.fragment_new_timer, container, false);
        mEditDuration = (EditText)v.findViewById(R.id.duration);
        mEditSearchTerm = (EditText)v.findViewById(R.id.search_term);

        Button button = (Button) v.findViewById(R.id.btnNewTimer);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String searchTerm = mEditSearchTerm.getText().toString();
                String lengthString = mEditDuration.getText().toString();
                int duration = 0;
                try {
                    duration = Integer.parseInt(lengthString);
                }catch(NumberFormatException e){
                    Toast.makeText(v.getContext(), "Invalid input!\n" + "video length: "
                                    + duration + " term: " + searchTerm
                                    , Toast.LENGTH_LONG).show();
                    return;
                }
                
                mListener.setTimer(LENGTH, duration * 1000, SEARCH_TERM, searchTerm);
            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);

        /**
         * set a new timer with specified length and search term
         * @param lenTitle holds "length"
         * @param length length of the timer
         * @param termTitle holds title of search term
         * @param searchTerm the search term
         */
        void setTimer(String lenTitle, int length, String termTitle, String searchTerm);
    }
}
