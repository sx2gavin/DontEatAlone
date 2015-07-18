package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


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
    private Context context;

    private FacadeModule facade;

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
        context = this.getActivity();

        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_match_list, container, false);
        matchListExpand = (ExpandableListView) view.findViewById(R.id.match_list_expandable);
        listAdapter = new MatchListAdapter();
        matchListExpand.setAdapter(listAdapter);

//        updateData();
        addTestData();

        return view;
    }

    public void updateData(){
        ArrayList<User> matchList = FacadeModule.getFacadeModule(this.context).GetMatchList();
        this.listAdapter.setUserList(matchList);
        Log.d("list-size", "matchlist-size: " + matchList.size());
    }

    // add 2 test users
    public void addTestData(){
        User user1 = new User();
        user1.setId(1);
        user1.setName("test user 1");
        user1.setGender("male");
        user1.setMaxDistance(3);
        user1.setLatitude(23);
        user1.setLongitude(32);
        user1.setDistance(3);
        user1.setMinAge(10);
        user1.setMaxAge(100);
        user1.setMinPrice(1);
        user1.setMaxPrice(1000);
        user1.setStartTime("10:30am");
        user1.setEndTime("11:59pm");
        user1.setLikes(110);
        user1.setDislikes(2);

        User user2 = new User();
        user2.setId(2);
        user2.setName("test user 2");
        user2.setGender("male");
        user2.setMaxDistance(5);
        user2.setLatitude(54);
        user2.setLongitude(32);
        user2.setDistance(4);
        user2.setMinAge(20);
        user2.setMaxAge(200);
        user2.setMinPrice(2);
        user2.setMaxPrice(2000);
        user2.setStartTime("12:30am");
        user2.setEndTime("10:59pm");
        user2.setLikes(12);
        user2.setDislikes(234);

        this.listAdapter.addUser(user1);
        this.listAdapter.addUser(user2);
    }

    public void updateData(JSONObject response){
        // TODO: Parse the data and add that to the list adapter
//
//        final String MATCHES = "matches";
//        final String ID = "id";
//
//        // remove this after testing
//        try {
//            listAdapter.clearMatches(); // clear things in adapter TODO: remove and add element as necessary
//
//            // Getting all the matches
//            JSONArray matches = response.getJSONArray(MATCHES);
//
//            // Add the IDs to the adapter
//            for(int i=0; i<matches.length(); i++){
//                JSONObject match = matches.getJSONObject(i);
//
//                // TODO: Remove this after testing
//                listAdapter.addMatches("id: " + match.getString(ID));
//                Log.d(TAG, "id: " + match.getString(ID));
//            }
//            Log.d(TAG, "no matches?") ;
//        } catch (Exception e){
//            Log.d(TAG, e.toString());
//        }
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
    public class MatchListAdapter extends UserListAdapter {

        public MatchListAdapter(){
            super();
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.matchesgrouprow, null);
            }

            // get the user object
            User user = this.getGroup(i);

            TextView basicInfoView = (TextView) view.findViewById(R.id.matches_group_basic_info);
            TextView thumbsUpView = (TextView) view.findViewById(R.id.matches_group_thumb_up);
            TextView thumbsDownView = (TextView) view.findViewById(R.id.matches_group_thumb_down);
            Button inviteButton = (Button) view.findViewById(R.id.matches_group_invite);

            // format the strings
            Resources res = context.getResources();
            String basicInfoText = String.format(res.getString(R.string.matches_user_basic_info),
                    user.getName());
            // TODO: change the age or remove it

            final View wholeView = view;

            // set the text
            basicInfoView.setText(basicInfoText);
            thumbsUpView.setText(user.getLikes() + "");
            thumbsDownView.setText(user.getDislikes() + "");

            // add event listener to the invite button
            final Button listenerBtn = inviteButton;
            inviteButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.matches_group_invite:
                            ImageView image = (ImageView) wholeView.findViewById(R.id.group_profile_image);
//                            loadImage(image, "https://s-media-cache-ak0.pinimg.com/736x/a1/e3/6b/a1e36bcb8ce179bd8cc8db28ff4ef6fb.jpg");
                            String url = "https://s-media-cache-ak0.pinimg.com/736x/a1/e3/6b/a1e36bcb8ce179bd8cc8db28ff4ef6fb.jpg";
                            new DownloadImageTask((ImageView)image).execute(url);

                            // disable button when clicked
                            listenerBtn.setEnabled(false);

                            Log.d(TAG, "invite button event fired");
                            break;
                    }
                }
            });

            return view;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            if(view == null){
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.matcheschildrow, null);
            }

            // get the user object
            User user = this.getGroup(i);

            TextView distView = (TextView) view.findViewById(R.id.matches_distance);
            TextView timeView = (TextView) view.findViewById(R.id.matches_time);
            TextView priceView = (TextView) view.findViewById(R.id.matches_price);
            TextView commentView = (TextView) view.findViewById(R.id.matches_comment);

            // format the strings
            Resources res = context.getResources();
            String distText = String.format(res.getString(R.string.matches_distance), user.getDistance());
            String timeText = String.format(res.getString(R.string.matches_time), user.getStartTime(), user.getEndTime());
            String priceText = String.format(res.getString(R.string.matches_price), user.getMinPrice(), user.getMaxPrice());
            String commentText = String.format(res.getString(R.string.matches_comment), "testing testing");

            // set the text
            distView.setText(distText);
            timeView.setText(timeText);
            priceView.setText(priceText);
            commentView.setText(commentText);

            return view;
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
