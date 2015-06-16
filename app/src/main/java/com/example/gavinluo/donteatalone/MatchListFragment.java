package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;


/**
 * A nice example of a expandable list fragment
 * https://gist.github.com/bowmanb/4052030
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MatchListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MatchListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MatchListFragment extends Fragment {
    private final String TAG = "MatchListFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ExpandableListView matchListExpand;
    private MatchListAdapter listAdapter;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchListFragment.
     */
    public static MatchListFragment newInstance(String param1, String param2) {
        MatchListFragment fragment = new MatchListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MatchListFragment() {
        // Required empty public constructor
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
        View view =  inflater.inflate(R.layout.fragment_match_list, container, false);
        matchListExpand = (ExpandableListView) view.findViewById(R.id.match_list_expandable);
        listAdapter = new MatchListAdapter();
        matchListExpand.setAdapter(listAdapter);
        return view;
    }

    public void updateData(JSONObject response){
        // TODO: Parse the data and add that to the list adapter

        final String MATCHES = "matches";
        final String ID = "id";

        // remove this after testing
        try {
            listAdapter.clearMatches(); // clear things in adapter TODO: remove and add element as necessary

            // Getting all the matches
            JSONArray matches = response.getJSONArray(MATCHES);

            // Add the IDs to the adapter
            for(int i=0; i<matches.length(); i++){
                JSONObject match = matches.getJSONObject(i);

                // TODO: Remove this after testing
                listAdapter.addMatches("id: " + match.getString(ID));
                Log.d(TAG, "id: " + match.getString(ID));
            }
            Log.d(TAG, "no matches?") ;
        } catch (Exception e){
            Log.d(TAG, e.toString());
        }
    }

    public MatchListAdapter getListAdapter(){
        return listAdapter;
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * The list adapter for expandable list
     */
    public class MatchListAdapter extends BaseExpandableListAdapter {
        private int INDEX_REQUESTS ;
        private int INDEX_MATCHES ;

        private static final String GROUP_REQUESTS = "Requests from";
        private static final String GROUP_MATCHES = "Matches";

        private ArrayList<String> groups;
        private ArrayList<ArrayList<String>> children;

        public MatchListAdapter(){
            super();
            groups = new ArrayList<String>(Arrays.asList(GROUP_REQUESTS, GROUP_MATCHES));
            children = new ArrayList<ArrayList<String>>();

            children.add(new ArrayList<String>());
            children.add(new ArrayList<String>());

            INDEX_REQUESTS = groups.indexOf(GROUP_REQUESTS);
            INDEX_MATCHES = groups.indexOf(GROUP_MATCHES);
        }

        public void addMatches(String s){
            children.get(INDEX_MATCHES).add(s);
        }

        public void clearMatches(){
            children.set(INDEX_MATCHES, new ArrayList<String>());
        }

        @Override
        public int getGroupCount() {
            return groups.size();
        }

        @Override
        public int getChildrenCount(int i){
            return children.get(i).size();
        }

        @Override
        public Object getGroup(int i){
            return groups.get(i);
        }

        @Override
        public Object getChild(int i, int j){
            return children.get(i).get(j);
        }

        @Override
        public long getGroupId(int i){
            return i;
        }

        @Override
        public long getChildId(int i, int j){
             return j;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(MatchListFragment.this.getActivity());
            textView.setText(getGroup(i).toString());
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(MatchListFragment.this.getActivity());
            textView.setText(getChild(i, i1).toString());
            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }
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
    public interface OnFragmentInteractionListener {
        public void onFragmentInteraction(Uri uri);
    }

}
