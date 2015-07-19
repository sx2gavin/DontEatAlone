package com.example.gavinluo.donteatalone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

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
 * {@link RequestListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RequestListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestListFragment extends Fragment {
    private final String TAG = "RequestListFragment";

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ExpandableListView requestListExpand;
    private RequestListAdapter listAdapter;
    private Context context;

//    private FacadeModule facade2;
    private static boolean stopFetchingRequests;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MatchListFragment.
     */
    public static RequestListFragment newInstance(String param1, String param2) {
        RequestListFragment fragment = new RequestListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RequestListFragment() {
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
        requestListExpand = (ExpandableListView) view.findViewById(R.id.match_list_expandable);
        listAdapter = new RequestListAdapter();
        requestListExpand.setAdapter(listAdapter);

//        facade2 = FacadeModule.getFacadeModule(this.context);

//        updateData();
//        addTestData();

        stopFetchingRequests = true;
        startUpdateRequests();

        return view;
    }

    public void stopUpdateRequests(){
        stopFetchingRequests = true;
    }

    public void startUpdateRequests(){
//        stopFetchingRequests = false;
//        if(facade2 == null){
//            facade2 = FacadeModule.getFacadeModule(context);
//        }
        if(!stopFetchingRequests){
            // do not start the update again if it's already started
            return;
        }
        stopFetchingRequests = false;

        Thread looper = new Thread() {
            public void run() {
                String response = "";

                // infinite loop to keep checking for new matches
                while(!stopFetchingRequests) {
                    // create a new thread if the response is empty
                    if(response.compareTo("")==0){
                        try {
                            FacadeModule.getFacadeModule(context).SendRequestForRequestList();
                            Thread checker = new Thread() {
                                public void run() {
                                    boolean running = true;
                                    while (!stopFetchingRequests && running == true) {
                                        String response = FacadeModule.getFacadeModule(context).GetResponseMessage();
                                        try {
                                            // Get the match list
                                            if (FacadeModule.getFacadeModule(context).GetResponse().compareTo("") != 0) {
                                                ArrayList requests = FacadeModule.getFacadeModule(context).GetRequestList();

                                                Log.d("tag", "requests-size:" +  requests.size());
                                                Log.d("tag", "response: " + FacadeModule.getFacadeModule(context).GetResponse());
                                                listAdapter.setUserList(requests);
                                                Log.d("tag", "actual list size: " + listAdapter.getUserList().size());
                                                running = false;
                                            }

                                            Thread.sleep(1000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                            running = false;
                                            Thread.currentThread().interrupt();
                                        }
                                    }
                                }
                            };
                            checker.start();

                            // sleep for 10 seconds
                            Thread.sleep(10001);
                        } catch (InterruptedException e){
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }
            }
        };
        looper.start();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        stopFetchingRequests = true;
    }

    @Override
    public void onDestroyView(){
        super.onDestroyView();
        stopFetchingRequests = true;
    }
//    public void updateData(){
//        Thread looper = new Thread() {
//            public void run() {
//                String response = "";
//
//                // infinite loop to keep checking for new matches
//                while(true) {
//                    // create a new thread if the response is empty
//                    if(response.compareTo("")==0){
//                        try {
//                            facade.SendRequestForMatchList();
//                            Thread checker = new Thread() {
//                                public void run() {
//                                    boolean running = true;
//                                    while (running == true) {
//                                        String response = FacadeModule.getFacadeModule(context).GetResponseMessage();
//                                        try {
//                                            // Get the match list
//                                            if (facade.GetResponse().compareTo("") != 0) {
//                                                ArrayList matches = facade.GetMatchList();
//
//                                                Log.d("tag", "matches-size:" +  matches.size());
//                                                Log.d("tag", "response: " + facade.GetResponse());
//                                                listAdapter.setUserList(matches);
//                                                Log.d("tag", "actual list size: " + listAdapter.getUserList());
//                                                running = false;
//                                            }
//
//                                            Thread.sleep(1000);
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                            running = false;
//                                            Thread.currentThread().interrupt();
//                                        }
//                                    }
//                                }
//                            };
//                            checker.start();
//
//                            // sleep for 10 seconds
//                            Thread.sleep(10001);
//                        } catch (InterruptedException e){
//                            e.printStackTrace();
//                            Thread.currentThread().interrupt();
//                        }
//                    }
//                }
//            }
//        };
//        looper.start();
//    }

    // add 2 test users
    public void addTestData(){
        User user1 = new User();
        user1.setId(3);
        user1.setName("test user 3");
        user1.setGender("female");
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
        user2.setId(4);
        user2.setName("test user 4");
        user2.setGender("male");
        user2.setMaxDistance(35);
        user2.setLatitude(52);
        user2.setLongitude(12);
        user2.setDistance(3);
        user2.setMinAge(42);
        user2.setMaxAge(10);
        user2.setMinPrice(2);
        user2.setMaxPrice(2000);
        user2.setStartTime("12:30am");
        user2.setEndTime("10:59pm");
        user2.setLikes(12);
        user2.setDislikes(234);

        this.listAdapter.addUser(user1);
        this.listAdapter.addUser(user2);
    }


    public RequestListAdapter getListAdapter(){
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

    /**
     * The list adapter for expandable list
     */
    public class RequestListAdapter extends UserListAdapter {

        public RequestListAdapter(){
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
            Button acceptButton = (Button) view.findViewById(R.id.matches_group_invite);

            // format the strings
            Resources res = context.getResources();
            String basicInfoText = String.format(res.getString(R.string.matches_user_basic_info),
                    user.getName());
            // TODO: change the age or remove it

            // set the text
            basicInfoView.setText(basicInfoText);
            thumbsUpView.setText(user.getLikes()+"");
            thumbsDownView.setText(user.getDislikes()+"");
            acceptButton.setText(res.getString(R.string.request_accept));

            // add event listener to the invite button
            acceptButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.matches_group_invite:
                            // go to the messenger
                            Intent intent = new Intent (context, MessengerActivity.class);
                            startActivity(intent);
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
            String distText = String.format(res.getString(R.string.matches_distance), String.format("%.1f", user.getDistance()));
            String timeText = String.format(res.getString(R.string.matches_time), user.getStartTime(), user.getEndTime());
            String priceText = String.format(res.getString(R.string.matches_price), String.format("%.2f", user.getMinPrice()), String.format("%.2f", user.getMaxPrice()));
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
