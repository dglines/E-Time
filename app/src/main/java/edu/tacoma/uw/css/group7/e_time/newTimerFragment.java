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


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link newTimerFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link newTimerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class newTimerFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String SEARCH_TERM = "searchTerm";
    private static final String LENGTH = "length";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private EditText mEditSearchTerm;
    private EditText mEditLength;
    private String mSearchTerm;
    private String mLength;

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
        mEditSearchTerm = (EditText)v.findViewById(R.id.search_term);
        mEditLength = (EditText)v.findViewById(R.id.length);


        return v;
    }

    public void buttonclick(View v) {
        //Bundle bundle = new Bundle();
        mSearchTerm = mEditSearchTerm.getText().toString();
        mLength = mEditLength.getText().toString();
//        bundle.putString(SEARCH_TERM, mSearchTerm);
//        bundle.putString(LENGTH, mLength);

        Intent intent = new Intent(this.getActivity(), TimerActivity.class);
        intent.putExtra(SEARCH_TERM, mSearchTerm);
        intent.putExtra(LENGTH, mLength);

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
